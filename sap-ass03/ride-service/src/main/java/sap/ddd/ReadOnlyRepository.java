package sap.ddd;

import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.RideDTO;
import sap.ass02.domain.dto.UserDTO;

import java.util.Optional;

public interface ReadOnlyRepository extends Repository {
    Optional<RideDTO> getRideById(final String rideId);
    Optional<RideDTO> getRideById(final String userId, final String ebikeId);
    Optional<RideDTO> getOngoingRideById(String userId, String ebikeId);
    Iterable<RideDTO> getAllRides();
    Optional<EBikeDTO> getEBikeById(final String ebikeId);
    Optional<UserDTO> getUserById(String userId);
    
}
