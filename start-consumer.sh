#!/bin/bash

docker exec -i kafka1 /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka1:9092 --topic Topic1 --from-beginning

