package sap.ass02.infrastructure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EventManager;

import java.util.HashMap;
import java.util.Map;

public final class KafkaRideServiceEventManagerVerticle extends AbstractVerticle implements EventManager {
    private static final Logger LOGGER = LogManager.getLogger(KafkaRideServiceEventManagerVerticle.class);
    Map<String, String> producerConfig = new HashMap<>();
    KafkaProducer<String, String> producer;
    
    @Override
    public void start() {
        this.producerConfig.put("bootstrap.servers", "kafka:9092");
        this.producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("acks", "1");
        
        this.producer = KafkaProducer.create(this.vertx, this.producerConfig);
        
        this.vertx.eventBus().consumer("ebike-update", message -> {
            LOGGER.trace("Received vertx ebike-update event '{}'", message.body());
            JsonObject jsonObject = new JsonObject(String.valueOf(message.body()));
            this.producer.write(KafkaProducerRecord.create("ebike-service", "ebike-update", jsonObject.encode()));
            LOGGER.trace("Sent ebike-update event to Kafka");
        });
        
        this.vertx.eventBus().consumer("user-update", message -> {
            LOGGER.trace("Received vertx user-update event '{}'", message.body());
            JsonObject jsonObject = new JsonObject(String.valueOf(message.body()));
            this.producer.write(KafkaProducerRecord.create("user-service", "user-update", jsonObject.encode()));
            LOGGER.trace("Sent ebike-update event to Kafka");
        });
    }
}
