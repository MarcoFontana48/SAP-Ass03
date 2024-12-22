package sap.ddd;

import sap.ass02.domain.dto.RideDTO;

import java.util.Optional;

public interface Repository {
    void init();
    void insertRide(final RideDTO ride);
    void updateRideEnd(final RideDTO ride);
    Optional<RideDTO> getRideById(final String rideId);
    Optional<RideDTO> getRideById(final String userId, final String ebikeId);
    Optional<RideDTO> getOngoingRideById(String userId, String ebikeId);
    Iterable<RideDTO> getAllRides();
}
