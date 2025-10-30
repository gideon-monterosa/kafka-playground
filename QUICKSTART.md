# Quick Start Guide

Get up and running with Kafka in 5 minutes!

## Prerequisites

- Nix with flakes enabled
- devenv installed
- Docker running

## Steps

### 1. Enter Development Environment

```bash
devenv shell
```

### 2. Start Kafka

```bash
start-kafka
```

Wait 10 seconds for Kafka to be ready.

### 3. Set Up Topics

```bash
setup-topics
```

### 4. Build Examples

```bash
build-examples
```

### 5. Test Producer & Consumer

In one terminal:
```bash
run-consumer
```

In another terminal:
```bash
run-producer
```

You should see the consumer receiving messages from the producer!

### 6. Test Kafka Streams

Start the streams application:
```bash
run-streams
```

In another terminal, send some text:
```bash
docker exec -it kafka kafka-console-producer --topic streams-input --bootstrap-server localhost:9092
# Type: hello world hello kafka
# Type: kafka streams is awesome
# Press Ctrl+D
```

View the word count results:
```bash
docker exec -it kafka kafka-console-consumer --topic streams-output --bootstrap-server localhost:9092 --from-beginning --property print.key=true --property key.separator=:
```

You should see word counts like:
```
hello:1
world:1
hello:2
kafka:1
...
```

### 7. Explore Kafka UI

Open http://localhost:8080 in your browser to explore topics, messages, and cluster status visually.

### 8. Stop Kafka

```bash
stop-kafka
```

## What's Next?

- Modify the examples in `src/main/java/com/kafka/playground/`
- Read the full [README.md](README.md) for detailed documentation
- Check the [Kafka Documentation](https://kafka.apache.org/documentation/)
- Try implementing your own Kafka applications!

## Common Commands

```bash
# View logs
kafka-logs

# Rebuild after code changes
build-examples

# List topics
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Describe a topic
docker exec kafka kafka-topics --describe --topic test-topic --bootstrap-server localhost:9092
```

Happy Kafka-ing! 🚀
