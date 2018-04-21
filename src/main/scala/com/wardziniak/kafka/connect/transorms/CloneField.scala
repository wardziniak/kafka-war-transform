package com.wardziniak.kafka.connect.transorms

import java.util
import java.util.Map

import org.apache.kafka.common.cache.{Cache, LRUCache, SynchronizedCache}
import org.apache.kafka.common.config.{ConfigDef, ConfigException}
import org.apache.kafka.connect.connector.ConnectRecord
import org.apache.kafka.connect.data.{Schema, SchemaBuilder, Struct}
import org.apache.kafka.connect.errors.DataException
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
    new ConfigDef()
  }

  override def close(): Unit = {
    schemaUpdateCache = null
  }

  override def configure(configs: util.Map[String, _]): Unit = {
    from = configs.asScala
      .get(CloneField.FromField)
      .map(_.toString)
      .getOrElse(throw new ConfigException(s"Property ${CloneField.FromField} must be set"))
    to = configs.asScala.get(CloneField.ToFields).map(_.toString).map(CloneField.parseTo)
      .getOrElse(throw new ConfigException(s"Property ${CloneField.ToFields} must be set"))
    schemaUpdateCache = new SynchronizedCache[Schema, Schema](new LRUCache[Schema, Schema](16))
  }


  private def applySchemaless (record: R): R = {
    ???
  }

  private def applyWithSchema(record: R): R = {
    val value = requireStruct(operatingValue(record), "PURPOSE")

    var updatedSchema = schemaUpdateCache.get(value.schema)
    if (updatedSchema == null) {
      updatedSchema = makeUpdatedSchema(value.schema)
      schemaUpdateCache.put(value.schema, updatedSchema)
    }
    val updatedValue = new Struct(updatedSchema)

    value.schema().fields().asScala.foreach(field => updatedValue.put(field.name(), value.get(field)))
    to.foreach(fieldName => updatedValue.put(fieldName, value.get(from)))
    newRecord(record, updatedSchema, updatedValue)
  }

  protected def makeUpdatedSchema(schema: Schema): Schema = {
    validateSchema(schema)
    val builder = SchemaUtil.copySchemaBasics(schema, SchemaBuilder.struct)
    val builderWithSchema = schema.fields.asScala.foldLeft(builder)((b, field) => b.field(field.name(), field.schema()))
    val resultBuilder = to.foldLeft(builderWithSchema)((b, toFieldName) => b.field(toFieldName, schema.field(from).schema()))
    resultBuilder.build
  }

  private def validateSchema(schema: Schema): Unit = {
    val fromNotExists = !schema.fields().asScala.map(_.name()).contains(from)
    val oneOfToFieldsAlreadyExists = schema.fields().asScala.map(_.name()).exists(to.contains)
    if (fromNotExists)
      throw new DataException(s"Cant find from field in schema [$from]")
    if (oneOfToFieldsAlreadyExists)
      throw new DataException(s"Schema already contains one of to fields [$to]")
  }

}

object CloneField {

  val FromField = "clone.from"
  val ToFields = "clone.to"

  val ToFieldsDelimiter = ","

  def parseTo(toProperty: String): List[String] = {
    toProperty.split(ToFieldsDelimiter).toList
  }

  class CloneFieldValue[R <: ConnectRecord[R]]  extends CloneField[R] with ValueGenericTransform[R]

  class CloneFieldKey[R <: ConnectRecord[R]]  extends CloneField[R] with KeyGenericTransform[R]
}
