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
package com.cisco.ztt.cisco.xr.telemetry

import org.apache.log4j.Logger
import com.cisco.ztt.TimeseriesDatapoint
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JValue
import com.cisco.ztt.meta.Item
import com.cisco.ztt.meta.Telemetry

class TimeseriesMapper(config: Telemetry, namespace: String) extends Mapper with Serializable {

    object Holder extends Serializable {
        @transient lazy val logger = Logger.getLogger(getClass.getName)
    }

    val wantedKeys = config.keys.getOrElse(Array[Item]()).map( item => {
        item.name -> item
    }).toMap

    val wantedValues = config.content.map( item => {
        item.name -> item
    }).toMap

    def transform(event: TEvent): Set[TimeseriesDatapoint] = {

        val timeseries = event.Rows.flatMap( row => {
            val keys = row.Keys
                    .getOrElse(Map[String, String]())
                    .filter(k => wantedKeys.contains(k._1))
                    .map( k => {
                        val wanted = wantedKeys(k._1)
                        val name = if (wanted.ts_name != null) wanted.ts_name else k._1
                        name -> k._2
                    }).toMap + ("host" -> event.Telemetry.node_id_str)

            // Flatten nested maps into container.key -> value
            val expanded = row.Content.flatMap( v => {
                if (v._2.isInstanceOf[JObject]) {
                    JsonParser.map(v._2).map( kv => {
                        (v._1 + "." + kv._1) -> kv._2
                    })
                } else {
                    Map[String, JValue](v)
                }
            })

            val items = expanded.filter(v => wantedValues.contains(v._1))
                .map( data => {
                    val wanted = wantedValues(data._1)
                    val name = namespace + "." + (if (wanted.ts_name != null) wanted.ts_name else data._1)
                    val value = JsonParser.int(data._2)
                    val datapoint = new TimeseriesDatapoint(name, value, row.Timestamp, keys)
                    datapoint
                })
            items.toSet
        })

        timeseries.toSet
    }

}
