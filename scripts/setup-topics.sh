#!/usr/bin/env bash
# Script to create required Kafka topics

echo "Creating Kafka topics..."

# Create test-topic for producer/consumer examples
docker exec kafka kafka-topics --create --if-not-exists \
  --topic test-topic \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Create streams-input topic for Kafka Streams example
docker exec kafka kafka-topics --create --if-not-exists \
  --topic streams-input \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1

# Create streams-output topic for Kafka Streams example
docker exec kafka kafka-topics --create --if-not-exists \
  --topic streams-output \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1

echo "Topics created successfully!"
echo ""
echo "List of topics:"
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
