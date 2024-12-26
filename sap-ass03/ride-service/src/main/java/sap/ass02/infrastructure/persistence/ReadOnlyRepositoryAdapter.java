package sap.ass02.infrastructure.persistence;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EBike;
import sap.ass02.domain.Ride;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.RideDTO;
import sap.ass02.domain.dto.UserDTO;

import java.util.Optional;

public final class ReadOnlyRepositoryAdapter extends AbstractVerticleReadOnlyRepository {
    private static final Logger LOGGER = LogManager.getLogger(ReadOnlyRepositoryAdapter.class);
    private final AbstractVerticleRepository readWriteRepository;
    
    public ReadOnlyRepositoryAdapter(AbstractVerticleRepository repository) {
        super();
        this.readWriteRepository = repository;
    }
    
    @Override
    public void start() {
        this.init();
        
        this.vertx.eventBus().consumer("insert-ride", message -> {
            LOGGER.trace("Received vertx insert-ride event '{}'", message.body());
            JsonObject rideJsonObject = new JsonObject(String.valueOf(message.body()));
            RideDTO ride = new Ride(rideJsonObject).toDTO();
            //! already inserts the user and ebike that are part of the ride
            this.insertRide(ride);
            LOGGER.trace("Inserted ride '{}'", ride);
        });
        
        //! the only update a ride can have is the end date, other updates are about ebikes and users and are handled by other methods
        this.vertx.eventBus().consumer("update-ride-end", message -> {
            LOGGER.trace("Received vertx update-ride event '{}'", message.body());
            JsonObject rideJsonObject = new JsonObject(String.valueOf(message.body()));
            RideDTO ride = new Ride(rideJsonObject).toDTO();
            this.updateRideEnd(ride);
            LOGGER.trace("Updated ride '{}'", ride);
        });
        
        this.vertx.eventBus().consumer("ebike-update", message -> {
            LOGGER.trace("Received vertx ebike-update event '{}'", message.body());
            JsonObject ebikeJsonObject = new JsonObject(String.valueOf(message.body()));
            this.readWriteRepository.updateEBike(new EBike(ebikeJsonObject).toDTO());
            LOGGER.trace("Updated ebike '{}'", ebikeJsonObject);
        });
        
        this.vertx.eventBus().consumer("user-update", message -> {
            LOGGER.trace("Received vertx user-update event '{}'", message.body());
            JsonObject userJsonObject = new JsonObject(String.valueOf(message.body()));
            this.readWriteRepository.updateUser(new User(userJsonObject).toDTO());
            LOGGER.trace("Updated user '{}'", userJsonObject);
        });
    }
    
    private void init() {
        this.readWriteRepository.init();
    }
    
    private void insertRide(RideDTO ride) {
        this.readWriteRepository.insertRide(ride);
    }
    
    private void updateRideEnd(RideDTO ride) {
        this.readWriteRepository.updateRideEnd(ride);
    }
    
    public Optional<RideDTO> getRideById(String rideId) {
        return this.readWriteRepository.getRideById(rideId);
    }
    
    public Optional<RideDTO> getRideById(String userId, String ebikeId) {
        return this.readWriteRepository.getRideById(userId, ebikeId);
    }
    
    public Optional<RideDTO> getOngoingRideById(String userId, String ebikeId) {
        return this.readWriteRepository.getOngoingRideById(userId, ebikeId);
    }
    
    public Iterable<RideDTO> getAllRides() {
        return this.readWriteRepository.getAllRides();
    }
    
    @Override
    public Optional<EBikeDTO> getEBikeById(String ebikeId) {
        return this.readWriteRepository.getEBikeById(ebikeId);
    }
    
    @Override
    public Optional<UserDTO> getUserById(String userId) {
        return this.readWriteRepository.getUserById(userId);
    }
}
