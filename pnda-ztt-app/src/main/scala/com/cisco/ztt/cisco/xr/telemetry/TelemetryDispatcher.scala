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

import com.cisco.pnda.model.DataPlatformEvent
import com.cisco.ztt.TimeseriesDatapoint
import com.cisco.ztt.Transformer
import com.cisco.ztt.meta.Unit
import com.cisco.ztt.Payload


class TelemetryDispatcher(unit: Unit) extends Serializable with Transformer {

    object Holder extends Serializable {
        @transient lazy val logger = Logger.getLogger(getClass.getName)
    }

    def inputTopic: String = { unit.input_topic }

    val transformers = unit.xr_telemetry.map( d => {
        Holder.logger.warn("Choosing mapper for " + unit.processor)
        unit.processor match {
            case "timeseries" => d.path -> new TimeseriesMapper(d, unit.timeseries_namespace)
            case "inventory" => d.path -> new InventoryMapper(d)
            case _ => d.path -> new NullMapper(d.path)
        }
    }).toMap

    def transform(rawEvent: DataPlatformEvent): (Boolean, Set[Payload]) = {
        Holder.logger.trace(rawEvent.getRawdata())

        try {
            val source = if (unit.publish_src == null) { "timeseries" } else { unit.publish_src }
            val event = JsonParser.parse(rawEvent.getRawdata())
            val path = event.Telemetry.encoding_path
            if (transformers.contains(path)) {
                Holder.logger.debug("Transforming for " + path)
                val datapoints = transformers(path).transform(event)
                val payloads = datapoints.map(d => {
                    new Payload(source, unit.output_topic,
                            rawEvent.getHostIp, rawEvent.getTimestamp, d)
                })
                (true, payloads)
            } else {
                Holder.logger.trace("No transformer in unit for " + path)
                (false, Set[Payload]())
            }
        } catch {
            case t: Throwable => {
                Holder.logger.error("Failed to parse JSON: " + t.getLocalizedMessage)
                (false, Set[Payload]())
            }
        }
    }
}
