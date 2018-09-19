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
package com.cisco.ztt.ves.telemetry

import com.cisco.ztt.Transformer
import com.cisco.pnda.model.DataPlatformEvent
import com.cisco.ztt.meta.Unit
import com.cisco.ztt.Payload
import org.apache.log4j.Logger
import org.json4s.jackson.JsonMethods
import org.json4s.JsonAST.JValue
import org.json4s.JsonAST.JObject
import com.cisco.ztt.TimeseriesDatapoint
import org.json4s.JsonAST.JArray


class VesTransformer(unit: Unit) extends Serializable with Transformer {

    object Holder extends Serializable {
        @transient lazy val logger = Logger.getLogger(getClass.getName)
    }

    def inputTopic: String = { unit.input_topic }

    val mappers = unit.ves_telemetry.map(d => {
        d.path.split("/") -> new VesMapper(d, unit.timeseries_namespace)
    }) //.toMap

    def transform(rawEvent: DataPlatformEvent): (Boolean, Set[Payload]) = {
        val source = if (unit.publish_src == null) { "timeseries" } else { unit.publish_src }
        val parsed = JsonMethods.parse(rawEvent.getRawdata)

        if (! parsed.isInstanceOf[JObject]) {
            Holder.logger.warn("Cannot process parsed JSON")
            return (false, Set[Payload]())
        }
        val value = parsed.asInstanceOf[JObject].values.get("event").get.asInstanceOf[Map[String, JValue]]
        val header = value.get("commonEventHeader").get.asInstanceOf[Map[String,Any]]
        val host = header.get("reportingEntityName").get.toString
        val timestamp = header.get("lastEpochMicrosec").get.toString.dropRight(3)

        val generated = mappers.flatMap(r => {
            val path = r._1
            val mapper = r._2

            val datapoints = visit(path, value, mapper, host, timestamp)
            datapoints.map(d => {
                new Payload(source, unit.output_topic, rawEvent.getHostIp, rawEvent.getTimestamp, d)
            })
        }).toSet
        (true, generated)
    }

    def visit(
            path: Array[String],
            map: Map[String, Any],
            mapper: VesMapper,
            host: String,
            timestamp: String): Set[TimeseriesDatapoint] = {
        if (path.length > 0) {
            val option = map.get(path.head)
            option match {
                case None => {
                    Holder.logger.warn("VES mapper failed to dereference JSON " + path.head)
                    return Set[TimeseriesDatapoint]()
                }

                case _ => {
                    option.get match {
                        case l: List[_] => {
                            val list = l.asInstanceOf[List[Map[String, Any]]]
                            return list.flatMap(sub => {
                                visit(path.tail, sub, mapper, host, timestamp)
                            }).toSet
                        }
                        case m: Map[_, _] => {
                            val sub = m.asInstanceOf[Map[String, Any]]
                            return visit(path.tail, sub, mapper, host, timestamp)
                        }

                    }
                }
            }
        } else {
            val datapoints = mapper.transform(map, host, timestamp)
            return datapoints
        }

        Set[TimeseriesDatapoint]()
    }
}
