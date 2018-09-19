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
package com.cisco.ztt.meta

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import com.cisco.ztt.TimeseriesDatapoint
import java.io.StringWriter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class DatapointTest extends FlatSpec with Matchers {
    
    "TimeseriesDatapoint" should "serialize to json" in {
        val data = Array(
                new TimeseriesDatapoint("packets-in", "5", "1000000",
                        Map("host" -> "host1", "inteface" -> "GigabitEthernet0/0/0/1")),
                new TimeseriesDatapoint("packets-out", "5", "1000000",
                        Map("host" -> "host1", "inteface" -> "GigabitEthernet0/0/0/1"))
        )
        
        val mapper = new ObjectMapper()
        mapper.registerModule(DefaultScalaModule)

        val out = new StringWriter
        mapper.writeValue(out, data)
        val json = out.toString()
    }
  
}