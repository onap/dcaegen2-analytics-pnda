package com.cisco.ztt.cisco.xr.telemetry

import org.json4s.DefaultFormats
import org.json4s.JsonAST.JArray
import org.json4s.JsonAST.JInt
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods
import org.json4s.jvalue2extractable
import org.json4s.string2JsonInput
import scala.reflect.ManifestFactory.arrayType
import scala.reflect.ManifestFactory.classType
import org.json4s.JsonAST.JObject


case class TRow(Timestamp: String, Keys: Option[Map[String, String]], Content: Map[String, JValue])
case class THeader(node_id_str: String, subscription_id_str: String,
                encoding_path: String, collection_id: Int, collection_start_time: Int,
                msg_timestamp: Int, collection_end_time: Int)
case class TEvent(Source: String, Telemetry: THeader, Rows: Array[TRow])


object JsonParser {

    def parse(json: String): TEvent = {
        implicit val formats = DefaultFormats

        val parsed = JsonMethods.parse(json)
        val event = parsed.extract[TEvent]
        event
    }

    def array(value: JValue): Array[Map[String,JValue]] = {
        implicit val formats = DefaultFormats
        val array = value.asInstanceOf[JArray]
        array.extract[Array[Map[String, JValue]]]
    }

    def map(value: JValue): Map[String,JValue] = {
        implicit val formats = DefaultFormats
        val map = value.asInstanceOf[JObject]
        map.extract[Map[String, JValue]]
    }

    def int(value: JValue): String = {
        value.asInstanceOf[JInt].num.toString
    }
}
