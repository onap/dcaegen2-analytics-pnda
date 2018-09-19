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

import com.cisco.ztt.meta.Telemetry
import com.cisco.ztt.TimeseriesDatapoint
import org.json4s.JsonAST.JValue
import org.apache.log4j.Logger
import com.cisco.ztt.meta.Item
import org.json4s.JsonAST.JObject


class VesMapper(config: Telemetry, namespace: String) extends Serializable {

    object Holder extends Serializable {
        @transient lazy val logger = Logger.getLogger(getClass.getName)
    }

    def transform(map: Map[String,Any], host: String, timestamp: String): Set[TimeseriesDatapoint] = {

        val keys = config.keys.getOrElse(Array[Item]()).map( k => {
            val value = map.get(k.name).get.toString()
            k.name -> value
        }).toMap + ("host" -> host)

        val items = config.content.map( wanted => {
            val name = namespace + "." + (if (wanted.ts_name != null) wanted.ts_name else wanted.name)
            val value = map.get(wanted.name).get.toString()

            new TimeseriesDatapoint(name, value, timestamp, keys)
        })

        items.toSet
    }
}