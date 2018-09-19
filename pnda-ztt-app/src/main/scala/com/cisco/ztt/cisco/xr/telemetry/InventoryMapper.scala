package com.cisco.ztt.cisco.xr.telemetry

import com.cisco.ztt.TimeseriesDatapoint
import org.apache.log4j.Logger
import com.cisco.ztt.meta.Telemetry

class InventoryMapper(config: Telemetry) extends Mapper with Serializable {

    object Holder extends Serializable {
        @transient lazy val logger = Logger.getLogger(getClass.getName)
    }

    def transform(event: TEvent): Set[TimeseriesDatapoint] = {
        Holder.logger.debug("InventoryMapper is sinking " + config.path)
        Set[TimeseriesDatapoint]()
    }
}