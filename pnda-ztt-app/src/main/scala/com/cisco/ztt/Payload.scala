package com.cisco.ztt

class Payload(
        val publishSrc: String,
        val publishTopic: String,
        val hostIp: String,
        val timestamp: Long,
        val datapoint: TimeseriesDatapoint) {

}