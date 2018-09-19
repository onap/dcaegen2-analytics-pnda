package com.cisco.ztt

class TimeseriesDatapoint(
        val metric: String,
        val value: String,
        val timestamp: String,
        val tags: Map[String, String])  {

}