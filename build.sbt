organization := "com.github.ollik1"
name := "spark-clipboard"

version := "0.1"

licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))

scalaVersion := "2.12.8"

val sparkVersion = "2.4.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.7" % Test