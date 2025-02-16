package sap.ass02.infrastructure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EventManager;
import sap.ass02.application.ServiceVerticle;
import sap.ass02.domain.utils.JsonFieldKey;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka event manager for user service.
 */
public class KafkaUserServiceEventManagerVerticle extends AbstractVerticle implements EventManager {
    private static final Logger LOGGER = LogManager.getLogger(KafkaUserServiceEventManagerVerticle.class);
    Map<String, String> consumerConfig = new HashMap<>();
    KafkaConsumer<String, String> consumer;
    private ServiceVerticle service;
    
    public KafkaUserServiceEventManagerVerticle(ServiceVerticle service) {
        this.service = service;
    }
    
    @Override
    public void start() {
        this.consumerConfig.put("bootstrap.servers", "kafka:9092");
        this.consumerConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("group.id", "ebike-user-consumer-group");
        this.consumerConfig.put("client.id", "ebike-user-consumer-" + UUID.randomUUID());
        this.consumerConfig.put("auto.offset.reset", "earliest");
        
        this.consumer = KafkaConsumer.create(this.vertx, this.consumerConfig);
        
        this.consumer.subscribe("user-service", ar -> {
            if (ar.succeeded()) {
                LOGGER.trace("Subscribed to topic user-service");
            } else {
                LOGGER.error("Could not subscribe to topic user-service. Cause: {}", ar.cause().getMessage());
            }
        });
        
        this.consumer.handler(record -> {
            LOGGER.trace("Received record from kafka: '{}' with topic: '{}', k={}, v={}", record, record.topic(), record.key(), record.value());
            if ("user-service".equals(record.topic())) {
                if ("user-update".equals(record.key())) {
                    JsonObject entries = new JsonObject(record.value());
                    this.service.updateUserCredits(entries.getString(JsonFieldKey.USER_ID_KEY), entries.getInteger(JsonFieldKey.USER_CREDIT_KEY));
                } else {
                    LOGGER.error("Unknown record key: '{}'", record.key());
                }
            }
        });
    }
}
