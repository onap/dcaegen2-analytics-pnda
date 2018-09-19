package ves.telemetry

import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.cisco.pnda.model.DataPlatformEvent
import com.cisco.ztt.meta.YamlReader
import com.cisco.ztt.ves.telemetry.VesTransformer
import org.apache.log4j.BasicConfigurator

class VesTransformerTest extends FlatSpec with Matchers with BeforeAndAfter {

    before {
        BasicConfigurator.configure();
    }

    val yaml = """
format: ves
input_topic: ves.avro
processor: timeseries
output_topic: timeseries
timeseries_namespace: nic

ves_telemetry:
  - path: measurementsForVfScalingFields/vNicUsageArray
    keys:
      - name: vNicIdentifier
    content:
      - name: receivedTotalPacketsDelta
"""
    val payload = """{
    "event": {
        "commonEventHeader": {
            "startEpochMicrosec": 1537258053361276,
            "sourceId": "bab0c4de-cfb8-4a0d-b7e4-a3d4020bb667",
            "eventId": "TrafficStats_1.2.3.4",
            "nfcNamingCode": "vVNF",
            "reportingEntityId": "No UUID available",
            "internalHeaderFields": {
                "collectorTimeStamp": "Tue, 09 18 2018 08:01:41 GMT"
            },
            "eventType": "HTTP request rate",
            "priority": "Normal",
            "version": 3,
            "reportingEntityName": "fwll",
            "sequence": 6108,
            "domain": "measurementsForVfScaling",
            "lastEpochMicrosec": 1537258063557408,
            "eventName": "vFirewallBroadcastPackets",
            "sourceName": "vFW_SINK_VNF2",
            "nfNamingCode": "vVNF"
        },
        "measurementsForVfScalingFields": {
            "cpuUsageArray": [
                {
                    "percentUsage": 0,
                    "cpuIdentifier": "cpu1",
                    "cpuIdle": 100,
                    "cpuUsageSystem": 0,
                    "cpuUsageUser": 0
                }
            ],
            "measurementInterval": 10,
            "requestRate": 9959,
            "vNicUsageArray": [
                {
                    "transmittedOctetsDelta": 0,
                    "receivedTotalPacketsDelta": 0,
                    "vNicIdentifier": "eth0",
                    "valuesAreSuspect": "true",
                    "transmittedTotalPacketsDelta": 0,
                    "receivedOctetsDelta": 0
                }
            ],
            "measurementsForVfScalingVersion": 2.1
        }
    }
}
"""
    "VesTransformer" should "successfully transform a VES event" in {
        val unit = YamlReader.parse(yaml)
        val ves = new VesTransformer(unit)

        val event = new DataPlatformEvent("src", System.currentTimeMillis(), "127.0.0.1", payload)
        val (status, result) = ves.transform(event)

        status should be(true)
        result.toList.length should be (1)
        result.toList(0).datapoint.metric should be ("nic.receivedTotalPacketsDelta")
    }
}