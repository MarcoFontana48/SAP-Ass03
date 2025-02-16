package sap.ass02.infrastructure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EBike;
import sap.ass02.domain.EventManager;
import sap.ass02.application.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class KafkaEBikeServiceEventManagerVerticle extends AbstractVerticle implements EventManager {
    private static final Logger LOGGER = LogManager.getLogger(KafkaEBikeServiceEventManagerVerticle.class);
    Map<String, String> consumerConfig = new HashMap<>();
    KafkaConsumer<String, String> consumer;
    private final Service service;
    
    public KafkaEBikeServiceEventManagerVerticle(Service service) {
        this.service = service;
    }
    
    @Override
    public void start() {
        this.consumerConfig.put("bootstrap.servers", "kafka:9092");
        this.consumerConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("group.id", "ebike-bike-consumer-group");
        this.consumerConfig.put("client.id", "ebike-bike-consumer-" + UUID.randomUUID());
        this.consumerConfig.put("auto.offset.reset", "earliest");
        
        this.consumer = KafkaConsumer.create(this.vertx, this.consumerConfig);
        
        this.consumer.subscribe("bike-service", ar -> {
            if (ar.succeeded()) {
                LOGGER.trace("Subscribed to topic bike-service");
            } else {
                LOGGER.error("Could not subscribe to topic bike-service. Cause: '{}'", ar.cause().getMessage());
            }
        });
        
        this.consumer.handler(record -> {
            LOGGER.trace("Received record from kafka: '{}' with topic: '{}', k={}, v={}", record, record.topic(), record.key(), record.value());
            if ("bike-service".equals(record.topic())) {
                if ("ebike-update".equals(record.key())) {
                    this.service.updateEBike(new EBike(new JsonObject(record.value())));
                } else {
                    LOGGER.error("Unknown record key: '{}'", record.key());
                }
            }
        });
    }
}
