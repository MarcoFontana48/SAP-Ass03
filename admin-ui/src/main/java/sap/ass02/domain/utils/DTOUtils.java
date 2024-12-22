package sap.ass02.domain.utils;

import sap.ass02.domain.*;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.RideDTO;
import sap.ass02.domain.dto.UserDTO;

import java.util.Optional;

public class DTOUtils {
    public static User toUser(UserDTO userDTO) {
        User user = new User(userDTO.id());
        user.rechargeCredit(userDTO.credit());
        return user;
    }
    
    public static EBike toEBike(EBikeDTO ebikeDTO) {
        EBike ebike = new EBike(ebikeDTO.id());
        ebike.updateState(EBike.EBikeState.valueOf(ebikeDTO.state().toString()));
        ebike.updateLocation(new P2d(ebikeDTO.location().y(), ebikeDTO.location().x()));
        ebike.updateDirection(new V2d(ebikeDTO.direction().x(), ebikeDTO.direction().y()));
        ebike.updateSpeed(ebikeDTO.speed());
        ebike.updateBatteryLevel(ebikeDTO.batteryLevel());
        return ebike;
    }
    
    public static Ride toRide(RideDTO rideDTO) {
        User user = toUser(rideDTO.user());
        EBike ebike = toEBike(rideDTO.ebike());
        Ride ride = new Ride(rideDTO.id(), user, ebike);
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
