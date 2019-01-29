name := "kafka-war-transform"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  Dependencies.kafkaConnectTransforms,
  Dependencies.kafkaConnectApi,
  Dependencies.scalaTest,
  Dependencies.kafkaConnectRuntime
)