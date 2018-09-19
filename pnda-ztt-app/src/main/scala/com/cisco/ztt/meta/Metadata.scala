package com.cisco.ztt.meta

class Metadata(val units: Array[Unit]) extends Serializable {
    def topics() : Set[String] = {
        val topics = units.map { case (unit : Unit) => {
            unit.input_topic
        }}
        
        topics.toSet
    }
}