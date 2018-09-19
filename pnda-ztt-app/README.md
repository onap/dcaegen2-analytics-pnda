# PNDA Zero Touch Telemetry

## Overview

The Zero Touch Telemetry application is a configurable telemetry-to-OpenTSDB solution.  Metadata
files are used to configure the ZTT application for each telemetry source.

The application receives telemetry events from Kafka, transforms the contents into a set of
timeseries datapoints and writes them to OpenTSDB.

This application demonstrates ingest of VES events from a virtual firewall into PNDA. The
extracted metrics get stored in HDFS and the timeseries database. This enables direct
visualization via Grafana as well as downstream Spark based analytics.
