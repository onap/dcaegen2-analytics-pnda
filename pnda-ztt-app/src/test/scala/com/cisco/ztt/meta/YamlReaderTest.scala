package com.cisco.ztt.meta

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class YamlReaderTest extends FlatSpec with Matchers {

  val interface_yaml = """
input_topic: telemetry
output_topic: timeseries

xr_telemetry:
  - path: Cisco-IOS-XR-infra-statsd-oper:infra-statistics/interfaces/interface/latest/generic-counters
    keys:
      - name: interface-name
        display_name: "Interface Name"

    content:
      - name: bytes-received
        display_name: "Bytes Received"

      - name: bytes-sent
        display_name: "Bytes Sent"

      - name: packets-received
        display_name: "Packets Received"

      - name: packets-sent
        display_name: "Packets Sent"
"""

  "YamlReader" should "successfully parse YAML text" in {
    val meta = YamlReader.parse(interface_yaml)

    meta.input_topic should be ("telemetry")
    meta.output_topic should be ("timeseries")
    meta.xr_telemetry.length should be (1)
    meta.xr_telemetry(0).keys.get.length should be(1)
    meta.xr_telemetry(0).content.length should be(4)
  }

  it should "read multiple files from classpath" in {
    val meta = YamlReader.load("classpath*:meta/test-*.yaml")
    meta.units.length should be (3)
  }
}
