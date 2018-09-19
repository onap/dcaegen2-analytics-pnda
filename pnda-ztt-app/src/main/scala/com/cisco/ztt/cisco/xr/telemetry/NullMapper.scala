/*
 * Copyright (c) 2018 Cisco Systems. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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