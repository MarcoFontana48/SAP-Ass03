package sap.ass02.domain.dto;

import sap.ass02.domain.*;

import java.util.Optional;

/**
 * Utility class for DTOs.
 */
public class DTOUtils {
    
    /**
     * Converts a user to a user DTO.
     * @param userDTO The user to convert.
     * @return The user DTO.
     */
    public static User toUser(UserDTO userDTO) {
        return new User(userDTO.id(), userDTO.credit(), userDTO.xLocation(), userDTO.yLocation());
    }
    
    /**
     * Converts an e-bike DTO to an e-bike.
     * @param ebikeDTO The e-bike DTO to convert.
     * @return The e-bike.
     */
    public static EBike toEBike(EBikeDTO ebikeDTO) {
        EBike ebike = new EBikeImpl(ebikeDTO.id());
        ebike.updateState(EBikeImpl.BikeState.valueOf(ebikeDTO.state().toString()));
        ebike.updateLocation(new P2d(ebikeDTO.location().x(), ebikeDTO.location().y()));
        ebike.updateDirection(new V2d(ebikeDTO.direction().x(), ebikeDTO.direction().y()));
        ebike.updateSpeed(ebikeDTO.speed());
        ebike.updateBatteryLevel(ebikeDTO.batteryLevel());
        return ebike;
    }
    
    /**
     * Converts a ride DTO to a ride.
     * @param rideDTO The ride DTO to convert.
     * @return The ride.
     */
    public static Ride toRide(RideDTO rideDTO) {
        UserDTO userDTO = rideDTO.user();
        EBikeDTO ebikeDTO = rideDTO.ebike();
        Ride ride = new Ride(rideDTO.id(), DTOUtils.toUser(userDTO), DTOUtils.toEBike(ebikeDTO));
        ride.setStartedDate(rideDTO.startedDate());
        if (rideDTO.endDate().isPresent()) {
            ride.setEndDate(rideDTO.endDate().get());
        } else {
            ride.setEndDate(Optional.empty());
        }
        ride.setOngoing(rideDTO.ongoing());
        return ride;
    }
}
