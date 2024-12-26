package sap.ass02.infrastructure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EventManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class KafkaRideServiceEventManagerVerticle extends AbstractVerticle implements EventManager {
    private static final Logger LOGGER = LogManager.getLogger(KafkaRideServiceEventManagerVerticle.class);
    Map<String, String> producerConfig = new HashMap<>();
    Map<String, String> consumerConfig = new HashMap<>();
    KafkaProducer<String, String> producer;
    KafkaConsumer<String, String> consumer;
    
    @Override
    public void start() {
        this.startKafkaProducer();
        this.startKafkaConsumer();
    }
    
    private void startKafkaProducer() {
        this.producerConfig.put("bootstrap.servers", "kafka:9092");
        this.producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("acks", "1");
        
        this.producer = KafkaProducer.create(this.vertx, this.producerConfig);
        
        this.vertx.eventBus().consumer("ebike-update", message -> {
            LOGGER.trace("Received vertx ebike-update event '{}'", message.body());
            JsonObject jsonObject = new JsonObject(String.valueOf(message.body()));
            this.producer.write(KafkaProducerRecord.create("bike-service", "ebike-update", jsonObject.encode()));
            LOGGER.trace("Sent ebike-update event to Kafka");
        });
        
        this.vertx.eventBus().consumer("user-update", message -> {
            LOGGER.trace("Received vertx user-update event '{}'", message.body());
            JsonObject jsonObject = new JsonObject(String.valueOf(message.body()));
            this.producer.write(KafkaProducerRecord.create("user-service", "user-update", jsonObject.encode()));
            LOGGER.trace("Sent ebike-update event to Kafka");
        });
    }
    
    private void startKafkaConsumer() {
        this.consumerConfig.put("bootstrap.servers", "kafka:9092");
        this.consumerConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("group.id", "ebike-client-user-consumer-group");
        this.consumerConfig.put("client.id", "ebike-client-user-consumer-" + UUID.randomUUID());
        this.consumerConfig.put("auto.offset.reset", "earliest");
        
        this.consumer = KafkaConsumer.create(this.vertx, this.consumerConfig);
        
        this.consumer.subscribe("bike", ar -> {
            if (ar.succeeded()) {
                LOGGER.trace("Subscribed to topic bike");
            } else {
                LOGGER.error("Could not subscribe to topic bike. Cause: '{}'", ar.cause().getMessage());
            }
        });
        
        this.consumer.handler(record -> {
            LOGGER.trace("Received record from kafka: '{}' with topic: '{}', k={}, v={}", record, record.topic(), record.key(), record.value());
            if ("bike-service".equals(record.topic())) {
                if ("insert-ebike".equals(record.key())) {
                    LOGGER.trace("Publishing vertx event insert-ebike received from event stream: '{}'", record.value());
                    this.vertx.eventBus().publish("insert-ebike", record.value());
                } else if ("ebike-update".equals(record.key())) {
                    JsonObject ebikeJsonObject = new JsonObject(record.value());
                    LOGGER.trace("Sending event ebike-update to vertx event bus: {}", ebikeJsonObject);
                    this.vertx.eventBus().publish("ebike-update", ebikeJsonObject);
                } else {
                    LOGGER.error("Unknown record key: '{}'", record.key());
                }
            } else if ("user-service".equals(record.topic())) {
                if ("insert-user".equals(record.key())) {
                    LOGGER.trace("Publishing vertx event insert-user received from event stream: '{}'", record.value());
                    this.vertx.eventBus().publish("insert-user", record.value());
                } else if ("user-update".equals(record.key())) {
                    JsonObject userJsonObject = new JsonObject(record.value());
                    LOGGER.trace("Sending event user-update to vertx event bus: {}", userJsonObject);
                    this.vertx.eventBus().publish("user-update", userJsonObject);
                } else {
                    LOGGER.error("Unknown record key: '{}'", record.key());
                }
            } else {
                LOGGER.error("Unknown record topic: '{}'", record.topic());
            }
        });
    }
}
