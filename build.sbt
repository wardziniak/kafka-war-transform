name := "kafka-war-transform"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += "Confluent" at "http://packages.confluent.io/maven/"

libraryDependencies ++= Seq(
  Dependencies.kafkaConnectTransforms,
  Dependencies.kafkaConnectApi,
//  Dependencies.confluentJdbcConnector,
  Dependencies.scalaTest
//  Dependencies.avro4sCore,
//  Dependencies.connectJson,
//  Dependencies.kafkaTools,
//  Dependencies.kafkaAvroSerializer,
//  Dependencies.kafkaConnectHdfs
)