package com.kafka.playground;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Simple Kafka Consumer Example
 * 
 * This example demonstrates how to:
 * - Configure a Kafka consumer
 * - Subscribe to a topic
 * - Poll and process messages
 * - Handle graceful shutdown
 */
public class ConsumerExample {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerExample.class);
    private static final String TOPIC = "test-topic";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "test-consumer-group";

    public static void main(String[] args) {
        logger.info("Starting Kafka Consumer Example");

        // Create consumer properties
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        // Create the consumer
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // Subscribe to the topic
            consumer.subscribe(Collections.singletonList(TOPIC));
            logger.info("Subscribed to topic: {}", TOPIC);

            // Set up shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down consumer...");
                consumer.wakeup();
            }));

            int messageCount = 0;
            int maxMessages = 20; // Stop after processing 20 messages for demo purposes

            // Poll for messages
            while (messageCount < maxMessages) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("Received message: key={}, value={} | partition={}, offset={}",
                            record.key(), record.value(), record.partition(), record.offset());
                    messageCount++;
                    
                    if (messageCount >= maxMessages) {
                        break;
                    }
                }
            }

            logger.info("Consumer processed {} messages. Exiting...", messageCount);

        } catch (Exception e) {
            logger.error("Error in consumer", e);
        }
    }
}
