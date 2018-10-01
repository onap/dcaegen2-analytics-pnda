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

/**
  * Name:       DataPlatformEvent
  * Purpose:    Data model class for an avro event on Kafka
  * Author:     PNDA team
  *
  * Created:    07/04/2016
  */

package com.cisco.pnda.model;

import java.io.IOException;
import java.io.Serializable;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

public class DataPlatformEvent implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected static ObjectMapper _mapper = new ObjectMapper();

    private String _src;
    private Long _timestamp;
    private String _hostIp;
    private String _rawdata;

    public DataPlatformEvent(String src, Long timestamp, String host_ip, String rawdata)
    {
        _src = src;
        _timestamp = timestamp;
        _hostIp = host_ip;
        _rawdata = rawdata;
    }

    public String getSrc()
    {
        return _src;
    }

    public Long getTimestamp()
    {
        return _timestamp;
    }

    public String getHostIp()
    {
        return _hostIp;
    }

    public String getRawdata()
    {
        return _rawdata;
    }

    @Override
    public String toString()
    {
        try
        {
            return _mapper.writeValueAsString(this);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    @Override
    public boolean equals(Object other)
    {
        boolean result = false;
        if (other instanceof DataPlatformEvent)
        {
            DataPlatformEvent that = (DataPlatformEvent) other;
            result =   (this.getSrc()       == that.getSrc()       || (this.getSrc()       != null && this.getSrc().equals(that.getSrc())))
                    && (this.getTimestamp() == that.getTimestamp() || (this.getTimestamp() != null && this.getTimestamp().equals(that.getTimestamp())))
                    && (this.getHostIp()    == that.getHostIp()    || (this.getHostIp()    != null && this.getHostIp().equals(that.getHostIp())))
                    && (this.getRawdata()   == that.getRawdata()   || (this.getRawdata()  != null && this.getRawdata().equals(that.getRawdata())));
        }
        return result;

    }

    public JsonNode RawdataAsJsonObj() throws JsonProcessingException, IOException
    {
        return _mapper.readTree(_rawdata);
    }

}
