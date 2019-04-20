organization := "org.ollik1"
name := "spark-clipboard"

version := "0.1"

scalaVersion := "2.12.8"

val sparkVersion = "2.4.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.7" % Test