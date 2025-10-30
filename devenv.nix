{ pkgs, lib, config, ... }:

{
  # https://devenv.sh/basics/
  env.KAFKA_PLAYGROUND_ROOT = config.env.DEVENV_ROOT;

  # https://devenv.sh/packages/
  packages = with pkgs; [
    git
    maven
    jdk17
    docker-compose
    kafkactl
  ];

  # https://devenv.sh/languages/
  languages.java = {
    enable = true;
    jdk.package = pkgs.jdk17;
    maven.enable = true;
  };

  # https://devenv.sh/processes/
  processes = {
    kafka-info.exec = "echo 'Kafka playground environment loaded. Use docker-compose to start Kafka cluster.'";
  };

  # https://devenv.sh/services/
  # We'll use docker-compose for Kafka instead of nix services for better control

  # https://devenv.sh/scripts/
  scripts.start-kafka.exec = ''
    echo "Starting Kafka cluster with Docker Compose..."
    docker compose up -d
    echo "Waiting for Kafka to be ready..."
    sleep 10
    echo "Kafka cluster is ready!"
    echo "Kafka broker: localhost:9092"
    echo "Zookeeper: localhost:2181"
  '';

  scripts.stop-kafka.exec = ''
    echo "Stopping Kafka cluster..."
    docker compose down
    echo "Kafka cluster stopped."
  '';

  scripts.kafka-logs.exec = ''
    docker compose logs -f
  '';

  scripts.setup-topics.exec = ''
    echo "Setting up Kafka topics..."
    ${pkgs.bash}/bin/bash scripts/setup-topics.sh
  '';

  scripts.build-examples.exec = ''
    echo "Building Java examples..."
    mvn clean compile
    echo "Build complete!"
  '';

  scripts.run-producer.exec = ''
    echo "Running Kafka Producer example..."
    mvn exec:java -Dexec.mainClass="com.kafka.playground.ProducerExample"
  '';

  scripts.run-consumer.exec = ''
    echo "Running Kafka Consumer example..."
    mvn exec:java -Dexec.mainClass="com.kafka.playground.ConsumerExample"
  '';

  scripts.run-streams.exec = ''
    echo "Running Kafka Streams example..."
    mvn exec:java -Dexec.mainClass="com.kafka.playground.StreamsExample"
  '';

  # https://devenv.sh/pre-commit-hooks/
  # pre-commit.hooks = {
  #   shellcheck.enable = true;
  # };

  # https://devenv.sh/starship/
  starship.enable = true;

  enterShell = ''
    echo "🚀 Kafka Playground Development Environment"
    echo ""
    echo "Available commands:"
    echo "  start-kafka    - Start Kafka cluster with Docker Compose"
    echo "  stop-kafka     - Stop Kafka cluster"
    echo "  kafka-logs     - View Kafka logs"
    echo "  setup-topics   - Create required Kafka topics"
    echo "  build-examples - Build Java examples"
    echo "  run-producer   - Run producer example"
    echo "  run-consumer   - Run consumer example"
    echo "  run-streams    - Run Kafka Streams example"
    echo ""
    echo "Java version:"
    java -version
    echo ""
    echo "Maven version:"
    mvn -version
    echo ""
  '';
}
