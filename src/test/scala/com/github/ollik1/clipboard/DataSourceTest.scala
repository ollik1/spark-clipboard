package com.github.ollik1.clipboard

import org.apache.spark.sql.types.{
  IntegerType,
  StringType,
  StructField,
  StructType
}
import org.apache.spark.sql.{Row, SparkSession}
import org.scalatest.{FunSuite, Matchers}

class DataSourceTest extends FunSuite with Matchers with ClipboardFunctions {

  val spark = SparkSession
    .builder()
    .master("local")
    .config("fs.clipboard.impl", "com.github.ollik1.clipboard.ClipboardFileSystem")
    .getOrCreate()
    .sqlContext
  import spark.implicits._

  test("infer schema") {
    withClipboard("""
        |+----+----+
        ||foo |bar |
        |+----+----+
        ||   1|   a|
        ||   2|   b|
        |+----+----+
      """.stripMargin) {
      val df = spark.read
        .format("com.github.ollik1.clipboard")
        .load("clipboard:///*")

      df.schema shouldBe StructType(
        Seq(StructField("foo", IntegerType), StructField("bar", StringType))
      )

      df.select($"foo").collect().map {
        case Row(x: Int) => x
      } should contain theSameElementsAs List(1, 2)

      df.select($"bar").collect().map {
        case Row(x: String) => x
      } should contain theSameElementsAs List("a", "b")
    }
  }

  test("explicit schema") {
    val schema = StructType(
      Seq(StructField("foo", StringType), StructField("bar", StringType))
    )

    withClipboard("""
        |+----+----+
        ||foo |bar |
        |+----+----+
        ||   1|   a|
        ||   2|   b|
        |+----+----+
      """.stripMargin) {
      val df = spark.read
        .format("com.github.ollik1.clipboard")
        .schema(schema)
        .load("clipboard:///*")

      df.schema shouldBe schema

      df.select($"foo").collect().map {
        case Row(x: String) => x
      } should contain theSameElementsAs List("1", "2")

      df.select($"bar").collect().map {
        case Row(x: String) => x
      } should contain theSameElementsAs List("a", "b")
    }
  }

  test("trailing and leading whitespace") {
    val schema1 = withClipboard("""
                    |+----+----+
                    ||foo |bar |
                    |+----+----+
                    ||   1|   a|
                    ||   2|   b|
                    |+----+----+
                  """.stripMargin) {
      spark.read
      .format("com.github.ollik1.clipboard")
      .load("clipboard:///*").schema
    }

    val schema2 = withClipboard("""
                                  |+----+----+
                                  | |foo |bar |
                                  |  +----+----+
                                  | |   1|   a|
                                  | |   2|   b|
                                  |   +----+----+
                                """.stripMargin) {
      spark.read
        .format("com.github.ollik1.clipboard")
        .load("clipboard:///*").schema
    }

    schema1 shouldEqual schema2
  }
}
