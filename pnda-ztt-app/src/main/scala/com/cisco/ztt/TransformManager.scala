package com.cisco.ztt


import com.cisco.ztt.meta.Metadata
import com.cisco.ztt.cisco.xr.telemetry.TelemetryDispatcher
import com.cisco.ztt.ves.telemetry.VesTransformer

class TransformManager(metadata: Metadata) {

    val transformers: Array[Transformer] = metadata.units.map( unit => {

        unit.format match {
            case "ves" => new VesTransformer(unit)
            case "cisco.xr.telemetry" => new TelemetryDispatcher(unit)
            case _ => new TelemetryDispatcher(unit)
        }
    })

    val byTopic = transformers.groupBy( t => { t.inputTopic })
}
