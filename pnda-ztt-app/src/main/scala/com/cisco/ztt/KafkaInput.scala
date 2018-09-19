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
import org.apache.spark.streaming.kafka.KafkaUtils

import com.cisco.pnda.model.DataPlatformEventCodec
import com.cisco.pnda.model.StaticHelpers

import kafka.serializer.DefaultDecoder
import kafka.serializer.StringDecoder
import org.apache.log4j.Logger

class KafkaInput extends Serializable {

    object Holder extends Serializable {
        @transient lazy val logger = Logger.getLogger(getClass.getName)
    }

    def readFromKafka(ssc: StreamingContext, topic: String) = {
        val props = AppConfig.loadProperties();
        val kafkaParams = collection.mutable.Map[String, String]("metadata.broker.list" -> props.getProperty("kafka.brokers"))
        if (props.getProperty("kafka.consume_from_beginning").toBoolean) {
            kafkaParams.put("auto.offset.reset", "smallest");
        }

        Holder.logger.info("Registering with kafka using broker " + kafkaParams("metadata.broker.list"))
        Holder.logger.info("Registering with kafka using topic " + topic)

        val messages = KafkaUtils.createDirectStream[String, Array[Byte], StringDecoder, DefaultDecoder](
            ssc, kafkaParams.toMap, Set(topic)).repartition(Integer.parseInt(props.getProperty("app.processing_parallelism")));

        // Decode avro container format
        val avroSchemaString = StaticHelpers.loadResourceFile("dataplatform-raw.avsc");
        val rawMessages = messages.map(x => {
            val eventDecoder = new DataPlatformEventCodec(avroSchemaString);
            val payload = x._2;
            val dataPlatformEvent = eventDecoder.decode(payload);
            dataPlatformEvent;
        });
        rawMessages;
    };
}
