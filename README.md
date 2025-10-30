# Kafka Playground

A comprehensive development environment for testing and learning Apache Kafka with Java, Kafka Streams, and Docker. This playground uses Nix and devenv for reproducible development environment management.

> 🚀 **New to this project?** Check out the [QUICKSTART.md](QUICKSTART.md) guide to get running in 5 minutes!

## 🎯 Overview

This project provides a complete Kafka development environment with:
- **Kafka Producer**: Send messages to Kafka topics
- **Kafka Consumer**: Consume and process messages from topics
- **Kafka Streams**: Real-time stream processing (word count example)
- **Docker Compose**: Local Kafka cluster with Zookeeper and Kafka UI
- **Nix & devenv**: Reproducible development environment

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

1. **Nix Package Manager** (with flakes enabled)
   ```bash
   # Install Nix
   curl -L https://nixos.org/nix/install | sh
   
   # Enable flakes (add to ~/.config/nix/nix.conf or /etc/nix/nix.conf)
   experimental-features = nix-command flakes
   ```

2. **devenv**
   ```bash
   nix profile install --accept-flake-config github:cachix/devenv/latest
   ```

3. **Docker with Docker Compose**
   - Docker must be running to start the Kafka cluster
   - Docker Compose v2 is required (comes with Docker Desktop or can be installed separately)
   - Install from: https://docs.docker.com/get-docker/

## 🚀 Quick Start

### 1. Enter the Development Environment

```bash
# Clone the repository (if you haven't already)
git clone <repository-url>
cd kafka-playground

# Enter the devenv shell
devenv shell
```

This will:
- Install all required dependencies (Java 17, Maven, kafkactl, etc.)
- Set up environment variables
- Display available commands

### 2. Start the Kafka Cluster

```bash
start-kafka
```

This starts:
- **Zookeeper** on `localhost:2181`
- **Kafka Broker** on `localhost:9092`
- **Kafka UI** on `http://localhost:8080` (web interface)

### 3. Set Up Kafka Topics (Optional but Recommended)

```bash
setup-topics
```

This creates the topics needed for the examples. Topics are also auto-created when needed, but explicit creation gives you more control.

### 4. Build the Java Examples

```bash
build-examples
```

This compiles all Java code using Maven.

### 5. Run the Examples

#### Producer Example
Send 10 test messages to the `test-topic`:
```bash
run-producer
```

#### Consumer Example
Consume messages from the `test-topic`:
```bash
run-consumer
```

#### Kafka Streams Example
Run the word count stream processing application:
```bash
# First, create the input topic and send some data
docker exec -it kafka kafka-topics --create --topic streams-input --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# Send some text data
docker exec -it kafka kafka-console-producer --topic streams-input --bootstrap-server localhost:9092
# Type some sentences and press Enter after each
# Press Ctrl+D to exit

# Run the streams application
run-streams

# In another terminal, view the output
docker exec -it kafka kafka-console-consumer --topic streams-output --bootstrap-server localhost:9092 --from-beginning --property print.key=true --property key.separator=:
```

### 6. Monitor Kafka

#### View Logs
```bash
kafka-logs
```

#### Access Kafka UI
Open your browser and navigate to: http://localhost:8080

Here you can:
- View topics and their messages
- Monitor consumer groups
- Check broker status
- View topic configurations

### 7. Stop the Kafka Cluster

```bash
stop-kafka
```

## 📁 Project Structure

```
kafka-playground/
├── src/
│   ├── main/
│   │   ├── java/com/kafka/playground/
│   │   │   ├── ProducerExample.java      # Kafka producer implementation
│   │   │   ├── ConsumerExample.java      # Kafka consumer implementation
│   │   │   └── StreamsExample.java       # Kafka Streams word count
│   │   └── resources/
│   │       └── logback.xml                # Logging configuration
│   └── test/
│       └── java/com/kafka/playground/     # Test files (add your tests here)
├── docker-compose.yml                     # Kafka cluster configuration
├── pom.xml                                # Maven project configuration
├── devenv.nix                             # Devenv configuration
├── devenv.yaml                            # Devenv inputs
├── flake.nix                              # Nix flake configuration
└── README.md                              # This file
```

## 🔧 Available Commands

When in the devenv shell, you have access to these commands:

| Command | Description |
|---------|-------------|
| `start-kafka` | Start the Kafka cluster with Docker Compose |
| `stop-kafka` | Stop the Kafka cluster |
| `kafka-logs` | View Kafka cluster logs |
| `setup-topics` | Create required Kafka topics |
| `build-examples` | Build all Java examples with Maven |
| `run-producer` | Run the Kafka producer example |
| `run-consumer` | Run the Kafka consumer example |
| `run-streams` | Run the Kafka Streams word count example |

## 🧪 Examples Explained

### Producer Example
The producer example demonstrates:
- Creating a Kafka producer with proper configuration
- Sending messages with keys and values
- Using asynchronous sends with callbacks
- Proper resource management

**Key concepts:**
- Bootstrap servers configuration
- Serialization (StringSerializer)
- Acknowledgments and retries
- Callbacks for send confirmation

### Consumer Example
The consumer example demonstrates:
- Creating a Kafka consumer with consumer group
- Subscribing to topics
- Polling for messages in a loop
- Processing records
- Graceful shutdown handling

**Key concepts:**
- Consumer groups
- Deserialization (StringDeserializer)
- Auto-commit vs manual commit
- Offset management (earliest vs latest)

### Kafka Streams Example
The Kafka Streams example demonstrates:
- Setting up a streaming topology
- Reading from input topics
- Transforming data (word count)
- Writing to output topics
- Stateful operations (counting)

**Key concepts:**
- KStream and KTable
- Topology building
- Stateful transformations
- Exactly-once semantics support

## 🛠️ Manual Operations

### Using Maven Directly

```bash
# Clean and compile
mvn clean compile

# Run a specific class
mvn exec:java -Dexec.mainClass="com.kafka.playground.ProducerExample"

# Package the application
mvn package
```

### Using Kafka Command-Line Tools

```bash
# Create a topic
docker exec -it kafka kafka-topics --create --topic my-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# List topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Describe a topic
docker exec -it kafka kafka-topics --describe --topic test-topic --bootstrap-server localhost:9092

# Console producer
docker exec -it kafka kafka-console-producer --topic test-topic --bootstrap-server localhost:9092

# Console consumer
docker exec -it kafka kafka-console-consumer --topic test-topic --bootstrap-server localhost:9092 --from-beginning

# List consumer groups
docker exec -it kafka kafka-consumer-groups --list --bootstrap-server localhost:9092

# Describe consumer group
docker exec -it kafka kafka-consumer-groups --describe --group test-consumer-group --bootstrap-server localhost:9092
```

## 📚 Learning Resources

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Kafka Streams Documentation](https://kafka.apache.org/documentation/streams/)
- [Confluent Kafka Tutorials](https://kafka-tutorials.confluent.io/)
- [devenv Documentation](https://devenv.sh/)

## 🐛 Troubleshooting

### Docker not running
**Error:** Cannot connect to Docker daemon
**Solution:** Ensure Docker is running: `docker ps`

### Port already in use
**Error:** Port 9092 or 2181 already in use
**Solution:** Stop any existing Kafka instances or change ports in `docker-compose.yml`

### Kafka not ready
**Error:** Connection refused when running examples
**Solution:** Wait a few seconds after starting Kafka. The cluster needs time to initialize.

### Topic doesn't exist
**Error:** Unknown topic or partition
**Solution:** Kafka auto-creates topics by default, but you can create them manually:
```bash
docker exec -it kafka kafka-topics --create --topic your-topic --bootstrap-server localhost:9092
```

## 🤝 Contributing

Feel free to:
- Add more examples
- Improve documentation
- Report issues
- Submit pull requests

## 📝 License

This project is provided as-is for educational purposes.

## 🎓 Next Steps

Once you're comfortable with the basics, try:
1. Implementing custom serializers/deserializers
2. Working with Avro or JSON schemas
3. Setting up multi-broker clusters
4. Implementing error handling and retry logic
5. Exploring Kafka Connect
6. Testing with Kafka Streams Test Utils
7. Implementing exactly-once semantics
8. Setting up monitoring with JMX metrics

Happy Kafka Learning! 🚀
