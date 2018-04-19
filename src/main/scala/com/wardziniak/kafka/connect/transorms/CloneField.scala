package com.wardziniak.kafka.connect.transorms

import java.util

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.ConnectRecord
import org.apache.kafka.connect.transforms.Transformation

/**
  * Created by wardziniak on 19.04.2018.
  */
class CloneField[R<: ConnectRecord[R]] extends Transformation[R] {
  override def apply(record: R): R = ???

  override def config(): ConfigDef = ???

  override def close(): Unit = ???

  override def configure(configs: util.Map[String, _]): Unit = ???
}
