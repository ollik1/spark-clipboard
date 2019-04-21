package com.github.ollik1.clipboard

import org.apache.spark.sql.{DataFrameReader, SQLContext}
import org.apache.spark.sql.sources.{
  BaseRelation,
  RelationProvider,
  SchemaRelationProvider
}
import org.apache.spark.sql.types.StructType

/**
  * Exposes format "org.ollik1.clipboard" to [[DataFrameReader]]
  */
class DefaultSource extends RelationProvider with SchemaRelationProvider {
  override def createRelation(
      sqlContext: SQLContext,
      parameters: Map[String, String]
  ): BaseRelation =
    new SparkShowRelation(sqlContext, parameters, None)

  override def createRelation(
      sqlContext: SQLContext,
      parameters: Map[String, String],
      schema: StructType
  ): BaseRelation = new SparkShowRelation(sqlContext, parameters, Some(schema))
}
