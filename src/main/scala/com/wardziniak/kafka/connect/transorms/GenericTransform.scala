package com.wardziniak.kafka.connect.transorms

import org.apache.kafka.connect.connector.ConnectRecord
import org.apache.kafka.connect.data.{Schema, SchemaBuilder, Timestamp}
import org.apache.kafka.connect.transforms.Transformation
import org.apache.kafka.connect.transforms.util.SchemaUtil

trait GenericTransform[R <: ConnectRecord[R]] extends Transformation[R]{

  protected def operatingSchema(record: R): Schema

  protected def operatingValue(record: R): Any

  protected def newRecord(record: R, updatedSchema: Schema, updatedValue: Any): R

}

trait ValueGenericTransform[R <: ConnectRecord[R]] extends GenericTransform[R]{
  override protected def operatingSchema(record: R) = record.valueSchema()

  override protected def operatingValue(record: R) = record.value()

  override protected def newRecord(record: R, updatedSchema: Schema, updatedValue: Any) = {
    record.newRecord(record.topic, record.kafkaPartition, record.keySchema(), record.key(), updatedSchema, updatedValue, record.timestamp)
  }
}

trait KeyGenericTransform[R <: ConnectRecord[R]] extends GenericTransform[R]{
  override protected def operatingSchema(record: R) = record.keySchema()

  override protected def operatingValue(record: R) = record.key()

  override protected def newRecord(record: R, updatedSchema: Schema, updatedValue: Any) = {
    record.newRecord(record.topic, record.kafkaPartition, updatedSchema, updatedValue, record.valueSchema, record.value, record.timestamp)
  }
}

