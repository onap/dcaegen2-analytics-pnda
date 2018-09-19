package com.cisco.ztt





import java.io.StringWriter

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.log4j.Logger
import org.apache.spark.streaming.dstream.DStream

import com.cisco.pnda.model.DataPlatformEvent
import com.cisco.pnda.model.DataPlatformEventCodec
import com.cisco.pnda.model.StaticHelpers
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.benfradet.spark.kafka.writer.dStreamToKafkaWriter


class KafkaOutput extends Serializable {

    object Holder extends Serializable {
        @transient lazy val logger = Logger.getLogger(getClass.getName)
    }

    def writeToKafka(output: DStream[Payload]) = {

        val props = AppConfig.loadProperties();
        val producerConfig = Map(
            "bootstrap.servers" -> props.getProperty("kafka.brokers"),
            "key.serializer" -> classOf[StringSerializer].getName,
            "value.serializer" -> classOf[ByteArraySerializer].getName
)
        output.writeToKafka(
            producerConfig,
            s => {
                val mapper = new ObjectMapper()
                mapper.registerModule(DefaultScalaModule)

                val out = new StringWriter
                mapper.writeValue(out, s.datapoint)
                val json = out.toString()

                val event = new DataPlatformEvent(s.publishSrc, s.timestamp, s.hostIp, json)
                val avroSchemaString = StaticHelpers.loadResourceFile("dataplatform-raw.avsc");
                val codec = new DataPlatformEventCodec(avroSchemaString);

                new ProducerRecord[String, Array[Byte]](s.publishTopic, codec.encode(event))
            }
        )
    }
}