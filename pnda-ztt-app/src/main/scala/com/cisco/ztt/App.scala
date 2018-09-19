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
package com.cisco.ztt

import org.apache.spark.streaming.StreamingContext
import com.cisco.pnda.StatReporter
import org.apache.log4j.Logger
import com.cisco.ztt.meta.YamlReader
import org.apache.log4j.BasicConfigurator

object App {

    private[this] val logger = Logger.getLogger(getClass().getName())

    def main(args: Array[String]) {

        BasicConfigurator.configure();

        val props = AppConfig.loadProperties();
        val loggerUrl = props.getProperty("environment.metric_logger_url")
        val appName = props.getProperty("component.application")
        val checkpointDirectory = props.getProperty("app.checkpoint_path");
        val batchSizeSeconds = Integer.parseInt(props.getProperty("app.batch_size_seconds"));

        val metadata = YamlReader.load()
        if (metadata.units.length == 0) {
            logger.error("Trying to run app without metadata")
            System.exit(1)
        }
        val pipeline = new ZttPipeline(metadata)

        // Create the streaming context, or load a saved one from disk
        val ssc = if (checkpointDirectory.length() > 0)
            StreamingContext.getOrCreate(checkpointDirectory, pipeline.create) else pipeline.create();

        sys.ShutdownHookThread {
            logger.info("Gracefully stopping Spark Streaming Application")
            ssc.stop(true, true)
            logger.info("Application stopped")
        }

        if (loggerUrl != null) {
            logger.info("Reporting stats to url: " + loggerUrl)
            ssc.addStreamingListener(new StatReporter(appName, loggerUrl))
        }
        logger.info("Starting spark streaming execution")
        ssc.start()
        ssc.awaitTermination()
    }
}