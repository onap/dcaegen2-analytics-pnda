package com.cisco.ztt.cisco.xr.telemetry

import com.cisco.ztt.TimeseriesDatapoint
import org.apache.log4j.Logger


class NullMapper(path: String) extends Mapper with Serializable {

    object Holder extends Serializable {
        @transient lazy val logger = Logger.getLogger(getClass.getName)
    }

    def transform(event: TEvent): Set[TimeseriesDatapoint] = {
        Holder.logger.debug("NullMapper is sinking " + path)
        Set[TimeseriesDatapoint]()
    }
}