package sap.ass02.application;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EBike;
import sap.ass02.domain.Ride;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.DTOUtils;
import sap.ass02.domain.dto.RideDTO;
import sap.ddd.Repository;
import sap.ddd.Service;

import java.util.ArrayList;

public class RideServiceVerticle extends AbstractVerticle implements ServiceVerticle {
    private static final Logger LOGGER = LogManager.getLogger(RideServiceVerticle.class);
    private final Service rideService = new RideService();
    private Repository queryOnlyRepository;
    
    @Override
    public void attachQueryOnlyRepository(Repository repository) {
        this.queryOnlyRepository = repository;
    }
    
    @Override
    public boolean startRide(Ride ride, User user, EBike ebike) {
        LOGGER.trace("Starting ride for user with id '{}' and eBike with id '{}'", user.getId(), ebike.getId());
        this.rideService.startRide(ride, user, ebike);
        RideDTO rideDTO = ride.toDTO();
        LOGGER.trace("Publishing insert-ride event '{}'", rideDTO.toJsonString());
        this.vertx.eventBus().publish("insert-ride", rideDTO.toJsonString());
        return true;
    }
    
    @Override
    public Ride getRide(String rideId) {
        LOGGER.trace("Getting ride with id '{}'", rideId);
        var ride = this.queryOnlyRepository.getRideById(rideId);
        if (ride.isPresent()) {
            return DTOUtils.toRide(ride.get());
        } else {
            return null;
        }
    }
    
    @Override
    public Ride getOngoingRide(String userId, String ebikeId) {
        LOGGER.trace("Getting ongoing ride for user with id '{}' and eBike with id '{}'", userId, ebikeId);
        var ride = this.queryOnlyRepository.getOngoingRideById(userId, ebikeId);
        if (ride.isPresent()) {
            return DTOUtils.toRide(ride.get());
        } else {
            return null;
        }
    }
    
    @Override
    public Iterable<Ride> getRides() {
        LOGGER.trace("Getting all rides");
        var rides = this.queryOnlyRepository.getAllRides();
        var rideList = new ArrayList<Ride>();
        rides.forEach(ride -> rideList.add(DTOUtils.toRide(ride)));
        return rideList;
    }
    
    @Override
    public boolean stopRide(String rideId) {
        return this.rideService.stopRide(rideId);
    }
    
    @Override
    public void attachRepository(Repository repository) {
        this.rideService.attachRepository(repository);
    }
    
    @Override
    public void updateUserCredits(Ride ride) {
        this.rideService.updateUserCredits(ride);
    }
    
    @Override
    public void updateEBike(Ride ride) {
        this.rideService.updateEBike(ride);
    }
    
    @Override
    public void attachEventBus(EventBus eventBus) {
        this.rideService.attachEventBus(eventBus);
    }
}
