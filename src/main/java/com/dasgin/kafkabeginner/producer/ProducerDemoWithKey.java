package com.dasgin.kafkabeginner.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithKey {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerDemoWithKey.class);

    public static void main(String[] args) {
        String bootstrapServers = "127.0.0.1:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for(int i=0; i<10; i++){

            String topic = "first_topic";
            String value = "hello world " + i;
            String key = "id_" + i;

            // create a producer record
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, value);

            // send data - ( this is async process)
            producer.send(record, (recordMetadata, e) -> {
                // executes every time record is successfully send or an exception is thrown
                if (e == null) {
                    // the record was successfully sent
                    LOGGER.info("Received new metadata. \n" +
                            "Topic: " + recordMetadata.topic() + "\n" +
                            "Partition: " + recordMetadata.partition() + "\n" +
                            "Offset: " + recordMetadata.offset() + "\n" +
                            "Timestamp: " + recordMetadata.timestamp());
                } else {
                    LOGGER.error("Error while producing", e);
                }
            });
        }

        producer.flush();
        producer.close();
    }
}
