/*
 * Copyright (c) 2018 Cisco Systems. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Name:       KafkaInput
 * Purpose:    Generate a dstream from Kafka
 * Author:     PNDA team
 *
 * Created:    07/04/2016
 */

/*
Copyright (c) 2016 Cisco and/or its affiliates.

This software is licensed to you under the terms of the Apache License, Version 2.0 (the "License").
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

The code, technical concepts, and all information contained herein, are the property of Cisco Technology, Inc.
and/or its affiliated entities, under various laws including copyright, international treaties, patent,
and/or contract. Any use of the material herein must be in accordance with the terms of the License.
All rights not expressly granted by the License are reserved.

Unless required by applicable law or agreed to separately in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied.
*/

package com.cisco.ztt

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.KafkaUtils

import com.cisco.pnda.model.DataPlatformEventCodec
import com.cisco.pnda.model.StaticHelpers

import kafka.serializer.DefaultDecoder
import kafka.serializer.StringDecoder
import org.apache.log4j.Logger
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.PreferConsistent
import java.util.Arrays
import scala.collection.JavaConversions
import java.util.Collections
import org.springframework.core.serializer.DefaultDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.ByteArrayDeserializer

class KafkaInput extends Serializable {

    object Holder extends Serializable {
        @transient lazy val logger = Logger.getLogger(getClass.getName)
    }

    def readFromKafka(ssc: StreamingContext, topic: String) = {
        val props = AppConfig.loadProperties();
        val kafkaParams = collection.mutable.Map[String, Object](
            "bootstrap.servers" -> props.getProperty("kafka.brokers"),
            "key.deserializer" -> classOf[StringDeserializer],
            "value.deserializer" -> classOf[ByteArrayDeserializer],
            "group.id" -> "pnda-ztt-app"
        )
        if (props.getProperty("kafka.consume_from_beginning").toBoolean) {
            kafkaParams.put("auto.offset.reset", "smallest");
        }

        Holder.logger.info("Registering with kafka using bootstrap servers " + kafkaParams("bootstrap.servers"))
        Holder.logger.info("Registering with kafka using topic " + topic)

        val messages = KafkaUtils.createDirectStream[String, Array[Byte]](
            ssc, PreferConsistent,
            Subscribe[String, Array[Byte]](Arrays.asList(topic), JavaConversions.mapAsJavaMap(kafkaParams)))
        // .repartition(Integer.parseInt(props.getProperty("app.processing_parallelism")))

        // Decode avro container format
        val avroSchemaString = StaticHelpers.loadResourceFile("dataplatform-raw.avsc");
        val rawMessages = messages.map(x => {
            val eventDecoder = new DataPlatformEventCodec(avroSchemaString);
            val payload = x.value;
            val dataPlatformEvent = eventDecoder.decode(payload);
            dataPlatformEvent;
        });
        rawMessages;
    };
}
