package com.cisco.ztt.meta

import org.apache.log4j.Logger
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

case class Unit(
        format: String,
        input_topic: String,
        processor: String,
        output_topic: String,
        publish_src: String,
        timeseries_namespace: String,
        xr_telemetry: Array[Telemetry],
        ves_telemetry: Array[Telemetry])
case class Telemetry(path: String, keys: Option[Array[Item]], content: Array[Item])
case class Item(name: String, display_name: String, ts_name: String)

object YamlReader {

    private[this] val logger = Logger.getLogger(getClass().getName());

    val mapper: ObjectMapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)

    def load(pattern: String = "classpath*:meta/*.yaml"): Metadata = {
        val patternResolver = new PathMatchingResourcePatternResolver()
        val mappingLocations = patternResolver.getResources(pattern)
        val units = mappingLocations.map(loc => {
            logger.info("Reading metadata " + loc)
            mapper.readValue(loc.getInputStream, classOf[Unit])
        });
        new Metadata(units)
    }

    def parse(yaml: String): Unit = {
        val meta: Unit = mapper.readValue(yaml, classOf[Unit])
        meta
    }

    def dump(meta: Unit) = {
        println(" input = " + meta.input_topic)
        println("output = " + meta.output_topic)
        meta.xr_telemetry.map(m => {
            println("  path = " + m.path)
            println("keys:")
            m.keys.getOrElse(Array[Item]()).map(item => {
                println("    " + item.name + " -> " + item.display_name)
            });
            println("content:")
            m.content.map(item => {
                println("    " + item.name + " -> " + item.display_name)
            });
        });
    }
}
