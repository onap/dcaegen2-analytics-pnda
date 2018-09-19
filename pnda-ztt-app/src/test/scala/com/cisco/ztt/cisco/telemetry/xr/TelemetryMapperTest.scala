package com.cisco.ztt.cisco.telemetry.xr

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.cisco.ztt.cisco.xr.telemetry.TimeseriesMapper
import com.cisco.ztt.meta.YamlReader
import com.cisco.ztt.cisco.xr.telemetry.JsonParser

class TelemetryMapperTest extends FlatSpec with Matchers {

    val ipv6_yaml = """
input_topic: telemetry.avro
output_topic: timeseries
timeseries_namespace: ipv6

xr_telemetry:
  - path: Cisco-IOS-XR-ipv6-io-oper:ipv6-io/nodes/node/statistics/traffic
    keys:
      - name: node-name
        display_name: "Node Name"
    content:
      - name: ipv6.total-packets
        display_name: "Total IPV6 Packets"
        ts_name: total-ipv6-packets

      - name: icmp.total-messages
        display_name: "Total ICMP Messages"
"""

    val ipv6_json = """
{
  "Source": "172.16.1.157:56197",
  "Telemetry": {
    "node_id_str": "IOS-XRv9k-edge-1",
    "subscription_id_str": "ipv6-io",
    "encoding_path": "Cisco-IOS-XR-ipv6-io-oper:ipv6-io/nodes/node/statistics/traffic",
    "collection_id": 282962,
    "collection_start_time": 1506008887021,
    "msg_timestamp": 1506008887021,
    "collection_end_time": 1506008887039
  },
  "Rows": [
    {
      "Timestamp": 1506008887031,
      "Keys": {
        "node-name": "0/RP0/CPU0"
      },
      "Content": {
        "icmp": {
          "output-messages": 0,
          "total-messages": 4,
          "unknown-error-type-messages": 0
        },
        "ipv6": {
          "bad-header-packets": 0,
          "total-packets": 12,
          "unknown-protocol-packets": 0
        },
        "ipv6-node-discovery": {
          "received-redirect-messages": 0,
          "sent-redirect-messages": 0
        }
      }
    }
  ]
}
"""

    "TelemetryMapper" should "Map to wanted timeseries values" in {
        val meta = YamlReader.parse(ipv6_yaml)
        val mapper = new TimeseriesMapper(meta.xr_telemetry(0), "demo")

        val event = JsonParser.parse(ipv6_json)
        val timeseries = mapper.transform(event).toList

        timeseries.length should be (2)
        timeseries(0).metric should be ("demo.icmp.total-messages")
        timeseries(1).metric should be ("demo.total-ipv6-packets")
    }
}