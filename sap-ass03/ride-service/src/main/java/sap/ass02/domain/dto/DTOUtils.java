package sap.ass02.domain.dto;

import sap.ass02.domain.*;

import java.util.Optional;

public class DTOUtils {
    
    public static User toUser(UserDTO userDTO) {
        User user = new User(userDTO.id());
        user.rechargeCredit(userDTO.credit());
        return user;
    }
    
    public static EBike toEBike(EBikeDTO ebikeDTO) {
        EBike ebike = new EBikeImpl(ebikeDTO.id());
        ebike.updateState(EBikeImpl.BikeState.valueOf(ebikeDTO.state().toString()));
        ebike.updateLocation(new P2d(ebikeDTO.location().x(), ebikeDTO.location().y()));
        ebike.updateDirection(new V2d(ebikeDTO.direction().x(), ebikeDTO.direction().y()));
        ebike.updateSpeed(ebikeDTO.speed());
        ebike.updateBatteryLevel(ebikeDTO.batteryLevel());
        return ebike;
    }
    
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
