package com.wardziniak.kafka.connect.transorms

import org.apache.kafka.connect.sink.SinkRecord
import org.scalatest.{FlatSpec, Matchers, MustMatchers}
import java.util.{Map => JMap}
import java.util.{HashMap => JHashMap}

import org.apache.kafka.common.config.ConfigException
import org.apache.kafka.connect.data.{Schema, SchemaBuilder, Struct}
import org.apache.kafka.connect.errors.DataException


import scala.collection.JavaConverters._

/**
  * Created by wardziniak on 21.04.2018.
  */
class CloneFieldTest
  extends FlatSpec
    with MustMatchers {

  val schema: Schema = SchemaBuilder.struct()
    .field("dont", Schema.STRING_SCHEMA)
    .field("abc", Schema.INT32_SCHEMA)
    .field("foo", Schema.BOOLEAN_SCHEMA)
    .field("etc", Schema.STRING_SCHEMA)
    .build()

  val valueObject = new Struct(schema)
  valueObject.put("dont", "whatever")
  valueObject.put("abc", 42)
  valueObject.put("foo", true)
  valueObject.put("etc", "etc")


  "CloneField Transforms" should s"throw exception if ${CloneField.ToFields} is not set" in {
    val cloneFieldValueTransform = new CloneField.CloneFieldValue[SinkRecord]()
    val props = new JHashMap[String, AnyRef]
    props.put("clone.from", "foo")

    val caught = intercept[ConfigException] {
      cloneFieldValueTransform.configure(props)
    }
    caught.getMessage mustBe s"Property ${CloneField.ToFields} must be set"
  }

  "CloneField Transforms" should s"throw exception if ${CloneField.FromField} is not set" in {
    val cloneFieldValueTransform = new CloneField.CloneFieldValue[SinkRecord]()
    val props = new JHashMap[String, AnyRef]
    props.put("clone.to", "some1,some2")

    val caught = intercept[ConfigException] {
      cloneFieldValueTransform.configure(props)
    }
    caught.getMessage mustBe s"Property ${CloneField.FromField} must be set"
  }

  "CloneField Transforms" should s"throw exception if value of ${CloneField.FromField} is not present in schema" in {
    val cloneFieldValueTransform = new CloneField.CloneFieldValue[SinkRecord]()

    val record = new SinkRecord("test", 0, null, null, schema, valueObject, 0)
    val props = new JHashMap[String, AnyRef]
    props.put("clone.from", "inexistingfiled")
    props.put("clone.to", "some1,some2")
    cloneFieldValueTransform.configure(props)

    val caught = intercept[DataException] {
      val tranformRecord = cloneFieldValueTransform.apply(record)
    }
    assert(caught.getMessage.startsWith("Cant find from field in schema"))
  }

  "CloneField Transforms" should s"throw exception if value of ${CloneField.ToFields} already is in schema" in {
    val cloneFieldValueTransform = new CloneField.CloneFieldValue[SinkRecord]()

    val record = new SinkRecord("test", 0, null, null, schema, valueObject, 0)
    val props = new JHashMap[String, AnyRef]
    props.put("clone.from", "foo")
    props.put("clone.to", "etc")
    cloneFieldValueTransform.configure(props)

    val caught = intercept[DataException] {
      val tranformRecord = cloneFieldValueTransform.apply(record)
    }
    assert(caught.getMessage.startsWith("Schema already contains one of to fields"))
  }

  "CloneField Transforms" should " copy value and schema of existing field" in {
    val cloneFieldValueTransform = new CloneField.CloneFieldValue[SinkRecord]()

    val record = new SinkRecord("test", 0, null, null, schema, valueObject, 0)
    val props = new JHashMap[String, AnyRef]
    props.put("clone.from", "foo")
    props.put("clone.to", "some1,some2")
    cloneFieldValueTransform.configure(props)
    val tranformRecord = cloneFieldValueTransform.apply(record)

    val newFieldName = List("some1", "some2")

    tranformRecord.valueSchema().fields().asScala.map(_.name()) must contain allElementsOf  newFieldName

    // TODO add more assertions
    tranformRecord.value().asInstanceOf[Struct]
    tranformRecord.value()

  }
}
