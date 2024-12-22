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

public class KafkaUserServiceEventManagerVerticle extends AbstractVerticle implements EventManager {
    private static final Logger LOGGER = LogManager.getLogger(KafkaUserServiceEventManagerVerticle.class);
    Map<String, String> consumerConfig = new HashMap<>();
    Map<String, String> producerConfig = new HashMap<>();
    KafkaConsumer<String, String> consumer;
    KafkaProducer<String, String> producer;
    
    @Override
    public void start() {
        this.consumerConfig.put("bootstrap.servers", "kafka:9092");
        this.consumerConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("group.id", "ebike-consumer-group");
        this.consumerConfig.put("auto.offset.reset", "earliest");
        
        this.producerConfig.put("bootstrap.servers", "kafka:9092");
        this.producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("acks", "1");
        
        this.consumer = KafkaConsumer.create(this.vertx, this.consumerConfig);
        this.producer = KafkaProducer.create(this.vertx, this.producerConfig);
        
        this.consumer.subscribe("user-service", ar -> {
            if (ar.succeeded()) {
                LOGGER.trace("Subscribed to topic user-service");
            } else {
                LOGGER.trace("Could not subscribe to topic user-service. Cause: {}", ar.cause().getMessage());
            }
        });
        
        this.consumer.handler(record -> {
            LOGGER.trace("Received record from kafka: '{}' with topic: '{}', k={}, v={}", record, record.topic(), record.key(), record.value());
            if ("user-service".equals(record.topic())) {
                if ("user-update".equals(record.key())) {
                    JsonObject userJsonObject = new JsonObject(record.value());
                    LOGGER.trace("Sending event user-update to vertx event bus: {}", userJsonObject);
                    this.vertx.eventBus().publish("user-update", userJsonObject);
                    this.producer.write(KafkaProducerRecord.create("client", "client-user-update", userJsonObject.encode()));
                } else {
                    LOGGER.error("Unknown record key: '{}'", record.key());
                }
            }
        });
        
        this.vertx.eventBus().consumer("insert-user", message -> {
            LOGGER.trace("Received message from event bus: '{}'", message.body());
            JsonObject userJsonObject = new JsonObject(message.body().toString());
            this.producer.write(KafkaProducerRecord.create("client", "client-insert-user", userJsonObject.encode()));
        });
    }
}
