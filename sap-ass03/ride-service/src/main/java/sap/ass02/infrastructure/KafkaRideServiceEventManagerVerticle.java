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
import java.util.Set;
import java.util.UUID;

public final class KafkaRideServiceEventManagerVerticle extends AbstractVerticle implements EventManager {
    private static final Logger LOGGER = LogManager.getLogger(KafkaRideServiceEventManagerVerticle.class);
    Map<String, String> consumerConfig = new HashMap<>();
    KafkaConsumer<String, String> consumer;
    
    @Override
    public void start() {
        this.startKafkaConsumer();
    }
    
    private void startKafkaConsumer() {
        this.consumerConfig.put("bootstrap.servers", "kafka:9092");
        this.consumerConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("group.id", "ebike-ride-consumer-group");
        this.consumerConfig.put("client.id", "ebike-ride-consumer-" + UUID.randomUUID());
        this.consumerConfig.put("auto.offset.reset", "earliest");
        
        this.consumer = KafkaConsumer.create(this.vertx, this.consumerConfig);
        
        Set<String> topics = Set.of("user-service", "bike-service");
        
        this.consumer.subscribe(topics, ar -> {
            if (ar.succeeded()) {
                topics.forEach(topic -> LOGGER.trace("Subscribed to topic '{}'", topic));
            } else {
                topics.forEach(topic -> LOGGER.error("Could not subscribe to topic '{}'. Cause: '{}'", topic, ar.cause().getMessage()));
            }
        });
        
        this.consumer.handler(record -> {
            LOGGER.trace("Received record from kafka: '{}' with topic: '{}', k={}, v={}", record, record.topic(), record.key(), record.value());
            if ("bike-service".equals(record.topic())) {
                if ("insert-ebike".equals(record.key())) {
                    LOGGER.trace("Publishing vertx event insert-ebike received from event stream: '{}'", record.value());
                    this.vertx.eventBus().publish("insert-ebike", record.value());
                } else {
                    LOGGER.error("Unknown record key: '{}'", record.key());
                }
            } else if ("user-service".equals(record.topic())) {
                if ("insert-user".equals(record.key())) {
                    LOGGER.trace("Publishing vertx event insert-user received from event stream: '{}'", record.value());
                    this.vertx.eventBus().publish("insert-user", record.value());
                } else {
                    LOGGER.error("Unknown record key: '{}'", record.key());
                }
            } else {
                LOGGER.error("Unknown record topic: '{}'", record.topic());
            }
        });
    }
}
