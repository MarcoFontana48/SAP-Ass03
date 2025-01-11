package sap.ass02.domain.application;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.*;
import sap.ass02.domain.dto.DTOUtils;
import sap.ass02.domain.dto.RideDTO;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.ReadOnlyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class RideServiceVerticle extends AbstractVerticle implements ServiceVerticle {
    private static final Logger LOGGER = LogManager.getLogger(RideServiceVerticle.class);
    private ReadOnlyRepository repository;
    private final Map<String, RideSimulation> currentlyActiveSimulations = new ConcurrentHashMap<>();
    
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
    
    @Override
    public Ride getOngoingRide(String userId, String ebikeId) {
        Optional<RideDTO> optionalRetrievedRide = this.repository.getOngoingRideById(userId, ebikeId);
        
        if (optionalRetrievedRide.isPresent()) {
            return DTOUtils.toRide(optionalRetrievedRide.get());
        } else {
            throw new RuntimeException("Ride not found");
        }
    }
    
    @Override
    public Ride getRide(String rideId) {
        return this.repository.getRideById(rideId).map(DTOUtils::toRide).orElse(null);
    }
    
    @Override
    public Iterable<Ride> getRides() {
        Iterable<RideDTO> allRides = this.repository.getAllRides();
        List<Ride> rides = new ArrayList<>();
        allRides.forEach(rideDTO -> rides.add(DTOUtils.toRide(rideDTO)));
        LOGGER.trace("Retrieved {} rides:", rides.size());
        rides.forEach(ride -> LOGGER.trace("\t- {}", ride.toJsonString()));
        return rides;
    }
    
    @Override
    public void attachRepository(ReadOnlyRepository repository) {
        this.repository = repository;
    }
    
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
    
    @Override
    public void updateEBike(Ride ride) {
        this.vertx.eventBus().publish("ebike-update", ride.getBike().toJsonString());
    }
    
    @Override
    public void updateEBike(EBike bike) {
        this.vertx.eventBus().publish("ebike-update", bike.toJsonString());
    }
    
    @Override
    public void updateUserCredits(Ride ride) {
        this.vertx.eventBus().publish("user-update", ride.getUser().toJsonString());
    }
    
    @Override
    public User getUser(String userId) {
        var maybeUser = this.repository.getUserById(userId);
        if (maybeUser.isPresent()) {
            return DTOUtils.toUser(maybeUser.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }
    
    @Override
    public EBike getEBike(String ebikeId) {
        var maybeEBike = this.repository.getEBikeById(ebikeId);
        if (maybeEBike.isPresent()) {
            return DTOUtils.toEBike(maybeEBike.get());
        } else {
            throw new RuntimeException("EBike not found");
        }
    }
}
