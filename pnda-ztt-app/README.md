# PNDA Zero Touch Telemetry

## Overview

The Zero Touch Telemetry application is a configurable telemetry-to-OpenTSDB solution.  Metadata
files are used to configure the ZTT application for each telemetry source.

The application receives telemetry events from Kafka, transforms the contents into a set of
timeseries datapoints and writes them to OpenTSDB.

This application demonstrates ingest of VES events from a virtual firewall into PNDA. The
extracted metrics get stored in HDFS and the timeseries database. This enables direct
visualization via Grafana as well as downstream Spark based analytics.

The __Virtual firewall (vFW)__ use case that is part of ONAP will have the following flow for its data:

    ONAP : VES -> ONAP : DMaaP -> Logstash -> PNDA : Kafka -> PNDA : Spark application -> PNDA : OpenTSDB

## Prerequisites

### ONAP : DMaaP data exposure

_Instructions_

If the port of the ONAP : DMaaP component is not exposed, it is required to adjust this accordingly:

```bash
# Find the message-router-kafka pod
kubectl -n onap get pods | grep message-router-kafka

# Create a loadbalancer for the ONAP : DMaaP component
kubectl -n onap expose deployment <k8s-cluster-name>-message-router-kafka --type=LoadBalancer --name=<k8s-cluster-name>-message-router-kafka-lb

# Check the outcome
kubectl -n onap get services | grep message-router-kafka-lb
```

This adds a public endpoint to the _message-router-kafka_ pod. It gives a public IP address and exposes the port `9092` through the new service of type LoadBalancer. In addition to this, we need to add the hostname `message-router-kafka` and the public IP address to the instances that run Logstash and the Spark application.

### Setup the Logstash component

Logstash is used for three reasons:

- to read the data from the ONAP : DMaaP component
- to serialize the data using [AVRO encoding](https://avro.apache.org/docs/current/)
- to pass the data to the PNDA : Kafka broker

This application has been tested with _Logstash 5.6.8_.

_Instructions_

Launch a Logstash instance part of a network that can reach the public endpoint of the _message-router-kafka_ pod.

```bash
# Download Logstash
sudo yum install wget
wget https://artifacts.elastic.co/downloads/logstash/logstash-5.6.8.tar.gz
tar zxvf logstash-5.6.8.tar.gz

# Download PNDA Logstash AVRO codec
git clone https://github.com/pndaproject/logstash-codec-pnda-avro
cd logstash-codec-pnda-avro
git checkout release/3.6

# Download Ruby
curl -sSL https://get.rvm.io | bash -s stable —ruby         
ruby -v

# Build the codec
gem build logstash-codec-pnda-avro.gemspec

# Install the codec as a Logstash plugin
../logstash-5.6.8/bin/logstash-plugin install logstash-codec-pnda-avro-3.1.1-java.gem  
```

Create a Logstash configuration file:
```bash
cat logstash.conf
input {
  kafka {
    topics => ["unauthenticated.VES_MEASUREMENT_OUTPUT"]
    bootstrap_servers => "<IP-ONAP-MESSAGE-ROUTER-KAFKA>:9092"
  }
}

filter {
  mutate {
    add_field => {
      "src" => "ves"
      "host_ip" => "onap.demon"
    }
    rename => { "message" => "rawdata"}
  }
  ruby {
    code => "event.set('timestamp', (event.get('@timestamp').to_f * 1000).to_i)"
  }
}

output {
  kafka {
    topic_id => "ves.avro"
    bootstrap_servers => "<IP-PNDA-KAFKA>:9094"
    value_serializer => "org.apache.kafka.common.serialization.ByteArraySerializer"
    codec => pnda-avro{
      schema_uri => "dataplatform-raw.avsc"
    }
  }
  stdout {
    codec => "json"
  }
}

```

Launch Logstash:
```bash
../logstash-5.6.8/bin/logstash -f logstash.conf &
```

### Create the output topic in PNDA Kafka
```bash
/opt/pnda/kafka/bin/kafka-topics.sh --create --topic ves.avro --partitions 2 --replication-factor 1 --zookeeper=<IP-PNDA-KAFKA-ZOOKEEPER>:2181
```

### Specify the PNDA gateway

Before building the application, configure the server that will be used for the package deployment by overwriting the value of the `SERVER` variable in the Makefile. Replace the `knox.example.com` with the address of the PNDA gateway.

```bash
cd pnda-ztt-app
vi Makefile

SERVER=https://knox.example.com:8443/gateway/pnda/repository
```

### Build the application

```bash
make app
``` 

### Package the application
```bash
make package
```

### Upload the application to the package manager
```bash
make deploy
```

### Deploy the application

Access the [list of packages in the PNDA console](https://knox.example.com:8443/gateway/pnda/console#/packages/pnda-ztt-app). Click _+ Deploy_ in order to deploy the application.

### Launch the application

Access the [list of applications in the PNDA console](https://knox.example.com:8443/gateway/pnda/console#/applications). Click _Create New Application_.
