package sap.ass02.application;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.*;
import sap.ass02.domain.dto.DTOUtils;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.RideDTO;
import sap.ass02.domain.dto.UserDTO;
import sap.ass02.domain.utils.JsonFieldKey;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the ride service verticle.
 */
public final class RideServiceVerticle extends AbstractVerticle implements ServiceVerticle {
    private static final Logger LOGGER = LogManager.getLogger(RideServiceVerticle.class);
    private Repository repository;
    private final Map<String, RideSimulation> currentlyActiveSimulations = new ConcurrentHashMap<>();
    Map<String, String> producerConfig = new HashMap<>();
    KafkaProducer<String, String> producer;
    
    /**
     * Starts the Kafka producer upon deploying the verticle.
     */
    @Override
    public void start() throws Exception {
        this.startKafkaProducer();
        this.subscribeToVertxEvents();
    }
    
    /**
     * Starts the Kafka producer.
     */
    private void startKafkaProducer() {
        this.producerConfig.put("bootstrap.servers", "kafka:9092");
        this.producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("acks", "1");
        
        this.producer = KafkaProducer.create(this.vertx, this.producerConfig);
    }
    
    /**
     * Subscribes to vertx events to apply logic to them.
     */
    private void subscribeToVertxEvents() {
        this.vertx.eventBus().consumer("ebike-update", message -> {
            LOGGER.trace("Received vertx ebike-update event '{}'", message.body());
            JsonObject jsonObject = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("Sending ebike-update event to Kafka: '{}'", jsonObject.encode());
            this.producer.write(KafkaProducerRecord.create("bike-service", "ebike-update", jsonObject.encode()));
            LOGGER.trace("Sent ebike-update event to Kafka");
        });
        
        this.vertx.eventBus().consumer("user-update", message -> {
            LOGGER.trace("Received vertx user-update event '{}'", message.body());
            JsonObject jsonObject = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("Sending user-update event to Kafka: '{}'", jsonObject.encode());
            this.producer.write(KafkaProducerRecord.create("user-service", "user-update", jsonObject.encode()));
            LOGGER.trace("Sent user-update event to Kafka");
        });
        
        this.vertx.eventBus().consumer("insert-user", message -> {
            LOGGER.trace("Received vertx insert-user event '{}'", message.body());
            JsonObject jsonObject = new JsonObject(String.valueOf(message.body()));
            this.repository.insertUser(UserDTO.fromJson(jsonObject));
            LOGGER.trace("Sent insert-user event to Kafka");
        });
        
        this.vertx.eventBus().consumer("insert-ebike", message -> {
            LOGGER.trace("Received vertx insert-ebike event '{}'", message.body());
            JsonObject jsonObject = new JsonObject(String.valueOf(message.body()));
            this.repository.insertEbike(EBikeDTO.fromJson(jsonObject));
            LOGGER.trace("Sent insert-ebike event to Kafka");
        });
    }
    
    /**
     * Starts a ride.
     * @param ride The ride to start.
     * @param user The user starting the ride.
     * @param ebike The eBike to start the ride with.
     */
    @Override
    public boolean startRide(Ride ride, User user, EBike ebike) {
        LOGGER.trace("Starting ride for user with id '{}' and eBike with id '{}'", user.getId(), ebike.getBikeId());
        ebike.updateState(EBikeImpl.BikeState.IN_USE);
        this.vertx.eventBus().publish("ebike-update", ebike.toJsonString());
        ride.start();
        
        this.vertx.eventBus().publish("insert-ride", ride.toDTO().toJsonString());
        LOGGER.trace("About to start ride simulation '{}'", ride);
        VerticleAgent aBikeAgentVerticle = new ABikeAgentMovementVerticle(ride, this);
        this.vertx.deployVerticle(aBikeAgentVerticle);
        RideSimulation rideSimulation = new RideSimulation(ride, user, this, aBikeAgentVerticle);
        rideSimulation.start();
        LOGGER.trace("Started ride simulation for ride '{}'", ride.getId());
        
        this.currentlyActiveSimulations.put(ride.getId(), rideSimulation);
        
        this.vertx.eventBus().publish("ride-started", ride.toDTO().toJsonString());
        return true;
    }
    
    /**
     * Gets the ongoing ride for a user and an eBike.
     * @param userId The id of the user.
     * @param ebikeId The id of the eBike.
     * @return The ongoing ride.
     */
    @Override
    public Ride getOngoingRide(String userId, String ebikeId) {
        Optional<RideDTO> optionalRetrievedRide = this.repository.getOngoingRideById(userId, ebikeId);
        
        if (optionalRetrievedRide.isPresent()) {
            return DTOUtils.toRide(optionalRetrievedRide.get());
        } else {
            throw new RuntimeException("Ride not found");
        }
    }
    
    /**
     * Gets the ride with the specified id.
     * @param rideId The id of the ride.
     * @return The ride.
     */
    @Override
    public Ride getRide(String rideId) {
        return this.repository.getRideById(rideId).map(DTOUtils::toRide).orElse(null);
    }
    
    /**
     * Gets all rides.
     * @return All rides.
     */
    @Override
    public Iterable<Ride> getRides() {
        Iterable<RideDTO> allRides = this.repository.getAllRides();
        List<Ride> rides = new ArrayList<>();
        allRides.forEach(rideDTO -> rides.add(DTOUtils.toRide(rideDTO)));
        LOGGER.trace("Retrieved {} rides:", rides.size());
        rides.forEach(ride -> LOGGER.trace("\t- {}", ride.toJsonString()));
        return rides;
    }
    
    /**
     * Attaches a repository to the verticle.
     * @param repository The repository to attach.
     */
    @Override
    public void attachRepository(Repository repository) {
        this.repository = repository;
    }
    
    /**
     * Stops a ride.
     * @param rideId The id of the ride to stop.
     * @return True if the ride was stopped, false otherwise.
     */
    @Override
    public boolean stopRide(String rideId) {
        LOGGER.trace("About to stop ride simulation for ride with id='{}'", rideId);
        
        RideSimulation rideSimulation = this.currentlyActiveSimulations.get(rideId);
        Ride retrievedRide;
        LOGGER.trace("Retrieved ride simulation '{}'", rideSimulation);
        if (rideSimulation != null) {
            rideSimulation.stopSimulation();
            retrievedRide = rideSimulation.getRide();
            LOGGER.trace("Retrieved ride '{}'", retrievedRide);
            retrievedRide.end();
        } else {
            LOGGER.error("Ride simulation not found for ride '{}'", rideId);
            throw new RuntimeException("Ride not found");
        }
        
        JsonObject jsonObject = new JsonObject()
                .put(JsonFieldKey.EBIKE_ID_KEY, rideId)
                .put(JsonFieldKey.EBIKE_STATE_KEY, EBikeImpl.BikeState.AVAILABLE.toString()
                );
        
        LOGGER.trace("About to publish event 'ebike-update' with payload '{}'", jsonObject.encode());
        this.vertx.eventBus().publish("ebike-update", jsonObject.encode());
        
        LOGGER.trace("About to publish event 'ride-ended' with payload '{}'", retrievedRide.toJsonString());
        this.vertx.eventBus().publish("ride-ended", retrievedRide.toJsonString());
        
        return true;
    }
    
    /**
     * Updates an eBike.
     * @param ride The ride to update.
     */
    @Override
    public void updateEBike(Ride ride) {
        this.vertx.eventBus().publish("ebike-update", ride.getBike().toJsonString());
    }
    
    /**
     * Updates the credits of a user.
     * @param bike The eBike to update.
     */
    @Override
    public void updateEBike(EBike bike) {
        this.vertx.eventBus().publish("ebike-update", bike.toJsonString());
    }
    
    /**
     * Updates the credits of a user.
     * @param ride The ride to update.
     */
    @Override
    public void updateUserCredits(Ride ride) {
        this.vertx.eventBus().publish("user-update", ride.getUser().toJsonString());
    }
    
    /**
     * Updates the credits of a user.
     * @param userId The user to update.
     */
    @Override
    public User getUser(String userId) {
        var maybeUser = this.repository.getUserById(userId);
        if (maybeUser.isPresent()) {
            return DTOUtils.toUser(maybeUser.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }
    
    /**
     * Gets the eBike with the specified id.
     * @param ebikeId The id of the eBike.
     * @return The eBike.
     */
    @Override
    public EBike getEBike(String ebikeId) {
        var maybeEBike = this.repository.getEBikeById(ebikeId);
        if (maybeEBike.isPresent()) {
            return DTOUtils.toEBike(maybeEBike.get());
        } else {
            throw new RuntimeException("EBike not found");
        }
    }
    
    /**
     * Reaches the user.
     */
    @Override
    public void reachUser(Ride ride) {
        if (this.currentlyActiveSimulations.get(ride.getId()) == null) {
            LOGGER.error("Ride simulation not found for ride '{}'", ride.getId());
            throw new RuntimeException("Ride not found");
        }
        VerticleAgent aBikeAgentVerticle = new ABikeAgentMovementVerticle(ride, this);
        this.vertx.deployVerticle(aBikeAgentVerticle).onComplete(ar -> {
            if (ar.succeeded()) {
                LOGGER.trace("Successfully deployed ABikeAgentMovementVerticle for ride '{}'", ride.getId());
            } else {
                LOGGER.error("Failed to deploy ABikeAgentMovementVerticle for ride '{}'", ride.getId());
            }
        });
        
        aBikeAgentVerticle.startToAutonomouslyReachUser();
    }
}
