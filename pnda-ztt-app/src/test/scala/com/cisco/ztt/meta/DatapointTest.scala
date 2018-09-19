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