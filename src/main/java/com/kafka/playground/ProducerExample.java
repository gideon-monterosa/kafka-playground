package com.kafka.playground;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Simple Kafka Producer Example
 * 
 * This example demonstrates how to:
 * - Configure a Kafka producer
 * - Send messages to a Kafka topic
 * - Handle send callbacks
 */
public class ProducerExample {
    private static final Logger logger = LoggerFactory.getLogger(ProducerExample.class);
    private static final String TOPIC = "test-topic";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) {
        logger.info("Starting Kafka Producer Example");

        // Create producer properties
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        // Create the producer
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            // Send 10 messages
            for (int i = 0; i < 10; i++) {
                String key = "key-" + i;
                String value = "Hello Kafka! Message number " + i;
                
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, key, value);
                
                // Send asynchronously with callback
                producer.send(record, (RecordMetadata metadata, Exception exception) -> {
                    if (exception != null) {
                        logger.error("Error sending message", exception);
                    } else {
                        logger.info("Sent message: key={}, value={} | partition={}, offset={}",
                                key, value, metadata.partition(), metadata.offset());
                    }
                });
                
                // Add a small delay to make output readable
                Thread.sleep(500);
            }
            
            // Flush and close will happen automatically with try-with-resources
            producer.flush();
            logger.info("All messages sent successfully!");
            
        } catch (Exception e) {
            logger.error("Error in producer", e);
        }
    }
}
