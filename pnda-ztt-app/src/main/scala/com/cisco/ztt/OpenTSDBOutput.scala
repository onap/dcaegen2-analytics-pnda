/**
 * Name:       OpenTSDBOutput
 * Purpose:    Write a dstream to OpenTSDB
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

import java.io.StringWriter

import scala.Iterator

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.log4j.Logger
import org.apache.spark.streaming.dstream.DStream

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.collection.mutable.ArrayBuffer

class OpenTSDBOutput extends Serializable {

    object Holder extends Serializable {
        @transient lazy val logger = Logger.getLogger(getClass.getName)
    }

    def putOpentsdb[T](opentsdbIP: String,
                       input: DStream[Payload]): DStream[Payload] = {
        input.mapPartitions(partition => {
            var size = 0
            val output = partition.grouped(20).flatMap(group => {
                val data = ArrayBuffer[TimeseriesDatapoint]()
                val passthru = group.map(item => {
                    data += item.datapoint
                    item
                })

                size += data.length

                if (data.length > 0) {
                    val mapper = new ObjectMapper()
                    mapper.registerModule(DefaultScalaModule)

                    val out = new StringWriter
                    mapper.writeValue(out, data)
                    val json = out.toString()

                    Holder.logger.debug("Posting " + data.length + " datapoints to OpenTSDB")
                    Holder.logger.debug(json)

                    if (opentsdbIP != null && opentsdbIP.length() > 0) {
                        val openTSDBUrl = "http://" + opentsdbIP + "/api/put"
                        try {
                            val httpClient = new DefaultHttpClient()
                            val post = new HttpPost(openTSDBUrl)
                            post.setHeader("Content-type", "application/json")
                            post.setEntity(new StringEntity(json))
                            val response = httpClient.execute(post)
                            // Holder.logger.debug(EntityUtils.toString(response.getEntity()))
                        } catch {
                            case t: Throwable => {
                                Holder.logger.warn(t)
                            }
                        }
                    }
                } else {
                    Holder.logger.debug("No datapoints to post to OpenTSDB")
                }
                passthru
            })
            output
        });
    }
}
