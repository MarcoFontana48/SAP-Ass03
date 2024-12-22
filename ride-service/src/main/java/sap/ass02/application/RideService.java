package sap.ass02.application;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EBike;
import sap.ass02.domain.Ride;
import sap.ass02.domain.RideSimulation;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.DTOUtils;
import sap.ass02.domain.dto.RideDTO;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.Repository;
import sap.ddd.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RideService implements Service {
    private static final Logger LOGGER = LogManager.getLogger(RideService.class);
    private Repository repository;
    private EventBus eventBus;
    private final Map<String, RideSimulation> currentlyActiveSimulations = new ConcurrentHashMap<>();
    
    @Override
    public boolean startRide(Ride ride, User user, EBike ebike) {
        LOGGER.trace("Starting ride for user with id '{}' and eBike with id '{}'", user.getId(), ebike.getId());
        ebike.updateState(EBike.EBikeState.IN_USE);
        this.eventBus.publish("ebike-update", ebike.toJsonString());
        ride.start();
        
        this.repository.insertRide(ride.toDTO());
        LOGGER.trace("About to start ride simulation '{}'", ride);
        
        RideSimulation rideSimulation = new RideSimulation(ride, user, this);
        rideSimulation.start();
        LOGGER.trace("Started ride simulation for ride '{}'", ride.getId());
        
        this.currentlyActiveSimulations.put(ride.getId(), rideSimulation);
        
        this.eventBus.publish("ride-started", ride.toDTO().toJsonString());
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
    public void attachRepository(Repository repository) {
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
                .put(JsonFieldKey.EBIKE_STATE_KEY, EBike.EBikeState.AVAILABLE.toString()
                );
        
        LOGGER.trace("About to publish event 'ebike-update' with payload '{}'", jsonObject.encode());
        this.eventBus.publish("ebike-update", jsonObject.encode());
        
        LOGGER.trace("About to update ride end onto db");
        this.repository.updateRideEnd(retrievedRide.toDTO());
        
        LOGGER.trace("About to publish event 'ride-ended' with payload '{}'", retrievedRide.toJsonString());
        this.eventBus.publish("ride-ended", retrievedRide.toJsonString());
        
        return true;
    }
    
    @Override
    public void updateEBike(Ride ride) {
        this.eventBus.publish("ebike-update", ride.getEBike().toJsonString());
    }
    
    @Override
    public void updateUserCredits(Ride ride) {
        this.eventBus.publish("user-update", ride.getUser().toJsonString());
    }
    
    @Override
    public void attachEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
