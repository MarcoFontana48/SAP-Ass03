package sap.ddd;

import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.RideDTO;
import sap.ass02.domain.dto.UserDTO;

import java.util.Optional;

public interface ReadWriteRepository extends Repository {
    void init();
    
    void insertRide(final RideDTO ride);
    
    void updateRideEnd(final RideDTO ride);
    
    Optional<RideDTO> getRideById(final String rideId);
    
    Optional<RideDTO> getRideById(final String userId, final String ebikeId);
    
    Optional<RideDTO> getOngoingRideById(String userId, String ebikeId);
    
    Iterable<RideDTO> getAllRides();
    
    Optional<EBikeDTO> getEBikeById(final String ebikeId);
    
    Optional<UserDTO> getUserById(String userId);
    
    void updateEBike(EBikeDTO dto);
    
    void updateUser(UserDTO dto);
    
    void insertUser(UserDTO user);
    
    void insertEbike(EBikeDTO ebike);
    
}
