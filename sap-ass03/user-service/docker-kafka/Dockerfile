# Use the official Confluent Kafka image from Docker Hub
FROM confluentinc/cp-kafka:latest

# Set environment variables for Kafka
ENV KAFKA_BROKER_ID=1
ENV KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
ENV KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
ENV KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1

# Expose the Kafka port
EXPOSE 9092

# Start Kafka
CMD ["sh", "-c", "bin/kafka-webController-start.sh config/webController.properties"]
