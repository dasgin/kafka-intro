package com.dasgin.kafkabeginner.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoAssignSeek {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemoAssignSeek.class.getName());

    public static void main(String[] args) {
        new ConsumerDemoAssignSeek().run();
    }

    private ConsumerDemoAssignSeek(){}

    private void run(){
        String bootstrapServers = "127.0.0.1:9092";
        String topic = "first_topic";

        CountDownLatch latch = new CountDownLatch(1);
        LOGGER.info("Creating the consumer thread");

        // create a consumer runnable
        Runnable consumerRunnable = new ConsumerRunnable(bootstrapServers, topic, latch);

        // start the thread
        Thread myThread = new Thread(consumerRunnable);
        myThread.start();

        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            LOGGER.info("Caught shotdown hook");
            ((ConsumerRunnable)consumerRunnable).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOGGER.info("Application is exited");
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.error("Application got interrupted", e);
        } finally {
            LOGGER.info("Application is closing");
        }
    }

    public class ConsumerRunnable implements Runnable {

        private Logger logger = LoggerFactory.getLogger(ConsumerRecord.class.getName());

        private CountDownLatch latch;
        private KafkaConsumer<String, String> consumer;

        public ConsumerRunnable(String bootstrapServers, String topic, CountDownLatch latch){
            this.latch = latch;

            // create consumer configs
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            consumer = new KafkaConsumer<>(properties);

            // assign
            TopicPartition partitionReadFrom = new TopicPartition(topic, 0);
            long offsetReadFrom = 15;
            consumer.assign(Collections.singletonList(partitionReadFrom));

            // seek
            consumer.seek(partitionReadFrom, offsetReadFrom);
        }

        @Override
        public void run() {
            // poll for new data
            try {
                int numberOfMessagesRead = 5;
                boolean keepOnReading = true;
                int numberOfMessagesReadSoFar = 0;

                while(keepOnReading){
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord record : records) {
                        numberOfMessagesReadSoFar += 1;
                        LOGGER.info("Key: " + record.key() + ", Value : " + record.value());
                        LOGGER.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                        if(numberOfMessagesReadSoFar >= numberOfMessagesRead){
                            keepOnReading = false;
                            break;
                        }
                    }
                }
            } catch (WakeupException e) {
                logger.info("Received shutdown signal!");
            } finally {
                consumer.close();
                latch.countDown();  // tell main code we're done with the consumer
            }
        }

        public void shutdown() {
            consumer.wakeup();
        }
    }
}
