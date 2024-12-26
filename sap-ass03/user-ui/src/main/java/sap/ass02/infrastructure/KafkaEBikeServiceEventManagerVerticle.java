package sap.ass02.infrastructure;

import io.vertx.core.AbstractVerticle;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import io.vertx.kafka.client.consumer.KafkaConsumerRecords;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EventManager;

import java.time.Duration;
import java.util.*;

public final class KafkaEBikeServiceEventManagerVerticle extends AbstractVerticle implements EventManager {
    private static final Logger LOGGER = LogManager.getLogger(KafkaEBikeServiceEventManagerVerticle.class);
    private static final int FOUR_WEEKS = 28;
    private Map<String, String> consumerConfig = new HashMap<>();
    private KafkaConsumer<String, String> consumer;
    
    @Override
    public void start() {
        this.startMonitoring();
    }
    
    @Override
    public void startMonitoring() {
        this.consumerConfig.put("bootstrap.servers", "kafka:9092");
        this.consumerConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put("group.id", "ebike-client-user-consumer-group");
        this.consumerConfig.put("client.id", "ebike-client-user-consumer-" + UUID.randomUUID());
        this.consumerConfig.put("auto.offset.reset", "earliest");
        
        this.consumer = KafkaConsumer.create(this.vertx, this.consumerConfig);
        
        this.consumer.subscribe("client", ar -> {
            if (ar.succeeded()) {
                LOGGER.trace("Subscribed to topic client");
            } else {
                LOGGER.error("Could not subscribe to topic client. Cause: '{}'", ar.cause().getMessage());
            }
        });
        
        this.consumer.handler(record -> {
            LOGGER.trace("Received record from kafka: '{}' with topic: '{}', k={}, v={}", record, record.topic(), record.key(), record.value());
            if ("client".equals(record.topic())) {
                if ("client-ebike-update".equals(record.key()) || ("client-insert-ebike".equals(record.key()))) {
                    LOGGER.trace("Publishing vertx event ebike-update received from event stream: '{}'", record.value());
                    this.vertx.eventBus().publish("ebike-update", record.value());
                } else {
                    LOGGER.error("Unknown record key: '{}'", record.key());
                }
            } else {
                LOGGER.error("Unknown record topic: '{}'", record.topic());
            }
        });
    }
    
    public void updateViewWithLatestEventsCountingFrom(int daysAgo) {
        List<KafkaConsumerRecord<String, String>> records = new ArrayList<>();
        
        this.consumer.poll(Duration.ofDays(daysAgo), ar -> {
            if (ar.succeeded()) {
                KafkaConsumerRecords<String, String> kafkaRecords = ar.result();
                for (int i = 0; i < kafkaRecords.size(); i++) {
                    records.add(kafkaRecords.recordAt(i));
                }
                this.applyEvents(records);
            } else {
                LOGGER.error("Failed to poll records from Kafka. Cause: '{}'", ar.cause().getMessage());
            }
        });
    }
    
    /**
     * Applies the events to update the view.
     */
    private void applyEvents(List<KafkaConsumerRecord<String, String>> records) {
        LOGGER.warn("Applying events to update the view '{}'", records.size());
        for (KafkaConsumerRecord<String, String> record : records) {
            LOGGER.warn("Received record from kafka: '{}' with topic: '{}', k={}, v={}", record, record.topic(), record.key(), record.value());
            if ("client-ebike-update".equals(record.key()) || "client-insert-ebike".equals(record.key())) {
                LOGGER.warn("Updating view with data received from event stream: '{}'", record.value());
                this.vertx.eventBus().publish("ebike-update", record.value());
            } else {
                LOGGER.error("Unknown record key: '{}'", record.key());
            }
        }
    }
}
