import sbt.ModuleID
import sbt._

/**
  * Created by wardziniak on 18.04.2018.
  */
object Dependencies {

  val kafkaConnectApi: ModuleID = "org.apache.kafka" % "connect-api" %  Versions.Kafka

  val kafkaConnectTransforms: ModuleID = "org.apache.kafka" % "connect-transforms" % Versions.Kafka

  val confluentJdbcConnector: ModuleID = "io.confluent" % "kafka-connect-jdbc" % "4.0.0"

  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.5" % "test"

  val avro4sCore: ModuleID = "com.sksamuel.avro4s" %% "avro4s-core" % "1.7.0"

  val connectJson: ModuleID = "org.apache.kafka" % "connect-json" % "1.0.0"

  val kafkaTools: ModuleID = "org.apache.kafka" % "kafka-tools" %  Versions.Kafka

  val kafkaAvroSerializer: ModuleID = "io.confluent" % "kafka-avro-serializer" % "4.0.0"

  val kafkaConnectHdfs: ModuleID = "io.confluent" % "kafka-connect-hdfs" % "4.0.0"

  val kafkaConnectRuntime: ModuleID = "org.apache.kafka" % "connect-runtime" %  Versions.Kafka

}
