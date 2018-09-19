package com.cisco.ztt.cisco.xr.telemetry

import com.cisco.ztt.TimeseriesDatapoint

trait Mapper {
    def transform(event: TEvent): Set[TimeseriesDatapoint]
}