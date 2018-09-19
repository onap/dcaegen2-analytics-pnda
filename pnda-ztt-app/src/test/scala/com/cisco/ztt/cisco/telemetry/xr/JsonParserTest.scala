package com.cisco.ztt.cisco.telemetry.xr

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.json4s.JsonAST.JArray
import com.cisco.ztt.cisco.xr.telemetry.JsonParser

class JsonParserTest extends FlatSpec with Matchers {
  val cpu_json = """
{
    "Source": "172.16.1.157:27059",
    "Telemetry": {
        "node_id_str": "IOS-XRv9k-edge-1",
        "subscription_id_str": "cpu",
        "encoding_path": "Cisco-IOS-XR-wdsysmon-fd-oper:system-monitoring/cpu-utilization",
        "collection_id": 265673,
        "collection_start_time": 1505905434090,
        "msg_timestamp": 1505905434090,
        "collection_end_time": 1505905434103
    },
    "Rows": [
        {
            "Timestamp": 1505905434099,
            "Keys": {
                "node-name": "0/RP0/CPU0"
            },
            "Content": {
                "process-cpu_PIPELINE_EDIT": [
                    {
                        "process-cpu-fifteen-minute": 0,
                        "process-cpu-five-minute": 0,
                        "process-cpu-one-minute": 0,
                        "process-id": 1,
                        "process-name": "init"
                    },
                    {
                        "process-cpu-fifteen-minute": 0,
                        "process-cpu-five-minute": 0,
                        "process-cpu-one-minute": 0,
                        "process-id": 1544,
                        "process-name": "bash"
                    },
                    {
                        "process-cpu-fifteen-minute": 0,
                        "process-cpu-five-minute": 0,
                        "process-cpu-one-minute": 0,
                        "process-id": 26436,
                        "process-name": "sleep"
                    }
                ],
                "total-cpu-fifteen-minute": 6,
                "total-cpu-five-minute": 6,
                "total-cpu-one-minute": 6
            }
        }
    ]
}
    """

    "JsonParser" should "successfully parse cpu telemetry JSON" in {
        val event = JsonParser.parse(cpu_json)

        event.Telemetry.subscription_id_str should be ("cpu")
        event.Rows(0).Keys.size should be (1)

        val subrows = event.Rows(0).Content("process-cpu_PIPELINE_EDIT")
        val extracted = JsonParser.array(subrows)

        extracted.size should be (3)
        extracted(0).size should be (5)
    }

  val null_keys = """
{
  "Source": "172.16.1.157:49227",
  "Telemetry": {
    "node_id_str": "IOS-XRv9k-edge-1",
    "subscription_id_str": "Logging",
    "encoding_path": "Cisco-IOS-XR-infra-syslog-oper:syslog/logging-statistics",
    "collection_id": 925712,
    "collection_start_time": 1507552918199,
    "msg_timestamp": 1507552918199,
    "collection_end_time": 1507552918203
  },
  "Rows": [
    {
      "Timestamp": 1507552918201,
      "Keys": null,
      "Content": {
        "buffer-logging-stats": {
          "buffer-size": 2097152,
          "is-log-enabled": "true",
          "message-count": 221,
          "severity": "message-severity-debug"
        }
      }
    }
  ]
}
  """

  it should "successfully parse JSON with null keys" in {
      val event = JsonParser.parse(null_keys)

      event.Rows(0).Keys.size should be (0)
  }
}