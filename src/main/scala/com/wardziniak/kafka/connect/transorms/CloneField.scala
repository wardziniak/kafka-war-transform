package com.wardziniak.kafka.connect.transorms

import java.util
import java.util.Map

import org.apache.kafka.common.cache.{Cache, LRUCache, SynchronizedCache}
import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.ConnectRecord
import org.apache.kafka.connect.data.{Schema, SchemaBuilder, Struct}
import org.apache.kafka.connect.transforms.Transformation
import org.apache.kafka.connect.transforms.util.Requirements.requireStruct
import org.apache.kafka.connect.transforms.util.SchemaUtil
import scala.collection.JavaConverters._

/**
  * Created by wardziniak on 19.04.2018.
  */
abstract class CloneField[R<: ConnectRecord[R]] extends GenericTransform[R] {

  protected var to: Seq[String] = _
  protected var from: String = _
  protected var schemaUpdateCache: Cache[Schema, Schema] = _

  override def apply(record: R): R = {
    applyWithSchema(record)
  }

  override def config(): ConfigDef = {
    schemaUpdateCache = new SynchronizedCache[Schema, Schema](new LRUCache[Schema, Schema](16))
    new ConfigDef()
  }

  override def close(): Unit = ???

  override def configure(configs: util.Map[String, _]): Unit = ???


  private def applySchemaless (record: R): R = {
    ???
  }

  private def applyWithSchema(record: R): R = {
    // Insure that fields (to) with passed names, doesn't exists already
    // Insure that field (from) with passed name, exists
    // If not add field with ne
    val value = requireStruct(operatingValue(record), "PURPOSE")

    var updatedSchema = schemaUpdateCache.get(value.schema)
    if (updatedSchema == null) {
      updatedSchema = makeUpdatedSchema(value.schema)
      schemaUpdateCache.put(value.schema, updatedSchema)
    }

    val updatedValue = new Struct(updatedSchema)
    val resultValue = value.schema().fields().asScala.foldLeft(updatedValue)((accValue, field) => accValue.put(field.name(), field.schema()))
    newRecord(record, updatedSchema, resultValue)
  }

  protected def makeUpdatedSchema(schema: Schema): Schema = {
    val builder = SchemaUtil.copySchemaBasics(schema, SchemaBuilder.struct)
    val resultBuilder = to.foldLeft(builder)((b, toFieldName) => b.field(toFieldName, schema.field(from).schema()))
    resultBuilder.build
  }

}
