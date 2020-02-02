Kafka Reis
=========================

Introduction
-----------------------------

An example which shows how to play with Kafka.

Downloading Kafka
-----------------------------

You will need to download kafka 

    $ curl "https://www.apache.org/dist/kafka/2.1.1/kafka_2.11-2.1.1.tgz" -o ~/Downloads/kafka.tgz


Preparing Kafka
-----------------------------

This example requires that Kafka Server is up and running.

    $ ${KAFKA}/bin/zookeeper-server-start.sh ${KAFKA}/config/zookeeper.properties
    $ ${KAFKA}/bin/kafka-server-start.sh ${KAFKA}/config/server.properties

Let's work on kafka
-----------------------------

List topics

    $ ${KAFKA}/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

Create topics
    
    $ ${KAFKA}/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 2 --topic log

Consume logs

    $ ${KAFKA}/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic log | jq

Inspect specific consumer groups

    $ ${KAFKA}/bin/kafka-consumer-groups.sh  --bootstrap-server localhost:9092 --group order --describe

Reset offsets for specific consumer groups

    $ ${KAFKA}/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group payment --topic log --reset-offsets --to-offset 1

Delete topics

    $ ${KAFKA}/bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic log --delete
