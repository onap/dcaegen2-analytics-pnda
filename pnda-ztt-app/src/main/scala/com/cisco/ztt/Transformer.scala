package com.cisco.ztt

import com.cisco.pnda.model.DataPlatformEvent
import org.apache.spark.streaming.dstream.DStream

trait Transformer {
    def inputTopic: String
    def transform(event: DataPlatformEvent): (Boolean, Set[Payload])
}
