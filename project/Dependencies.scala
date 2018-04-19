import sbt.ModuleID
import sbt._

/**
  * Created by wardziniak on 18.04.2018.
  */
object Dependencies {

  val kafkaConnectApi: ModuleID = "org.apache.kafka" % "connect-api" % "1.0.0"

  val kafkaConnectTransforms: ModuleID = "org.apache.kafka" % "connect-transforms" % "1.0.0"

  val confluentJdbcConnector: ModuleID = "io.confluent" % "kafka-connect-jdbc" % "4.0.0"


}
