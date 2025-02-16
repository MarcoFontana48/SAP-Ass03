package sap.ass02.application;

import io.vertx.core.AbstractVerticle;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EBike;
import sap.ass02.domain.dto.EBikeDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * Proxy class for the service class to produce events upon calling it.
 */
public final class BikeServiceVerticle extends AbstractVerticle implements ServiceVerticle {
    private static final Logger LOGGER = LogManager.getLogger(BikeServiceVerticle.class);
    private final Service bikeService = new BikeService();
    Map<String, String> producerConfig = new HashMap<>();
    KafkaProducer<String, String> producer;

    @Override
    public void start() {
        this.producerConfig.put("bootstrap.servers", "kafka:9092");
        this.producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("acks", "1");
        
        this.producer = KafkaProducer.create(this.vertx, this.producerConfig);
    }
    
    /**
     * Adds an ebike to the repository and publishes an event
     *
     * @param ebikeId the ebike id
     */
    @Override
    public boolean addBike(String ebikeId) {
        LOGGER.trace("Adding ebike with id '{}'", ebikeId);
        this.bikeService.addBike(ebikeId);
        EBikeDTO addedEBike = new EBike(ebikeId).toDTO();
        LOGGER.trace("Publishing insert-ebike event '{}'", addedEBike.toJsonString());
        this.vertx.eventBus().publish("insert-ebike", addedEBike.toJsonString());
        this.producer.write(KafkaProducerRecord.create("client", "client-insert-ebike", addedEBike.toJsonString()));
        this.producer.write(KafkaProducerRecord.create("bike-service", "insert-ebike", addedEBike.toJsonString()));
        return true;
    }
    
    /**
     * Gets an ebike from the repository
     *
     * @param eBikeId the ebike id
     * @return the user
     */
    @Override
    public EBike getEBike(String eBikeId) {
        LOGGER.trace("Getting ebike with id '{}'", eBikeId);
        return this.bikeService.getEBike(eBikeId);
    }
    
    /**
     * Updates the ebike in the repository given the ebike and publishes an event
     *
     * @param eBike the ebike
     */
    @Override
    public boolean updateEBike(EBike eBike) {
        LOGGER.trace("Updating ebike with id '{}'", eBike.getId());
        this.bikeService.updateEBike(eBike);
        EBikeDTO ebikeDTO = new EBike(eBike.getId(), eBike.getState(), eBike.getLocation(), eBike.getDirection(), eBike.getSpeed(), eBike.getBatteryLevel()).toDTO();
        LOGGER.trace("Publishing update-ebike event '{}'", ebikeDTO.toJsonString());
        this.vertx.eventBus().publish("ebike-update", ebikeDTO.toJsonString());
        this.producer.write(KafkaProducerRecord.create("client", "client-insert-ebike", ebikeDTO.toJsonString()));
        return true;
    }
    
    /**
     * Gets all the ebikes from the repository
     *
     * @return the ebikes
     */
    @Override
    public Iterable<EBike> getEBikes() { return this.bikeService.getEBikes(); }
    
    /**
     * Attach a repository to the service
     *
     * @param repository the repository
     */
    @Override
    public void attachRepository(Repository repository) {
        this.bikeService.attachRepository(repository);
    }
}
