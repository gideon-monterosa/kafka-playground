package com.kafka.playground;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Kafka Streams Example - Word Count
 * 
 * This example demonstrates how to:
 * - Set up a Kafka Streams application
 * - Read from an input topic
 * - Process data (word count in this case)
 * - Write results to an output topic
 * - Handle graceful shutdown
 */
public class StreamsExample {
    private static final Logger logger = LoggerFactory.getLogger(StreamsExample.class);
    private static final String INPUT_TOPIC = "streams-input";
    private static final String OUTPUT_TOPIC = "streams-output";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String APPLICATION_ID = "streams-wordcount-example";

    public static void main(String[] args) {
        logger.info("Starting Kafka Streams Example - Word Count");

        // Create streams configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Build the topology
        StreamsBuilder builder = new StreamsBuilder();
        
        // Read from input topic
        KStream<String, String> textLines = builder.stream(INPUT_TOPIC);
        
        // Process: Split lines into words, group by word, and count
        KTable<String, Long> wordCounts = textLines
                // Split each line into words
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
                // Group by word
                .groupBy((key, word) -> word)
                // Count occurrences
                .count();
        
        // Write results to output topic
        wordCounts.toStream()
                .mapValues(count -> count.toString())
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        // Create and start the streams application
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        final CountDownLatch latch = new CountDownLatch(1);

        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                logger.info("Shutting down Kafka Streams...");
                streams.close();
                latch.countDown();
            }
        });

        try {
            logger.info("Starting Kafka Streams topology...");
            streams.start();
            logger.info("Kafka Streams is running. Processing messages from topic: {}", INPUT_TOPIC);
            logger.info("Word counts will be written to topic: {}", OUTPUT_TOPIC);
            logger.info("Press Ctrl+C to stop...");
            latch.await();
        } catch (Exception e) {
            logger.error("Error in streams application", e);
            System.exit(1);
        }
        
        System.exit(0);
    }
}
