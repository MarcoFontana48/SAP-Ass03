package sap.ass02.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.dto.RideDTO;

import static org.junit.jupiter.api.Assertions.*;

//! UNIT test
class RideTest {
    @BeforeEach
    void setUp() {
    }
    
    @AfterEach
    void tearDown() {
    }
    
    @Test
    void createsRideCorrectly() {
        User user = new User("id", 10);
        EBike ebike = new EBikeImpl("id", EBikeImpl.BikeState.AVAILABLE, new P2d(0, 0), new V2d(0, 0), 0, 100);
        Ride ride = new Ride("id", user, ebike);
        assertAll(
                () -> assertEquals("id", ride.getId()),
                () -> assertEquals(user, ride.getUser()),
                () -> assertEquals(ebike, ride.getBike())
        );
    }
    
    @Test
    void convertsToDTO() {
        User user = new User("id", 10);
        EBike ebike = new EBikeImpl("id", EBikeImpl.BikeState.AVAILABLE, new P2d(0, 0), new V2d(0, 0), 0, 100);
        Ride ride = new Ride("id", user, ebike);
        RideDTO rideDTO = ride.toDTO();
        assertAll(
                () -> assertEquals("id", rideDTO.id()),
                () -> assertEquals(user.toDTO(), rideDTO.user()),
                () -> assertEquals(ebike.toDTO(), rideDTO.ebike())
        );
    }
    
    @Test
    void convertsToJson() {
        User user = new User("id", 10);
        EBike ebike = new EBikeImpl("id", EBikeImpl.BikeState.AVAILABLE, new P2d(0, 0), new V2d(0, 0), 0, 100);
        Ride ride = new Ride("id", user, ebike);
        String json = ride.toJsonString();
        assertEquals("{\"ride_id\":\"id\",\"user\":{\"user_id\":\"id\",\"credit\":10,\"x_location\":0.0,\"y_location\":0.0},\"ebike\":{\"ebike_id\":\"id\",\"state\":\"AVAILABLE\",\"x_location\":0.0,\"y_location\":0.0,\"x_direction\":0.0,\"y_direction\":0.0,\"speed\":0.0,\"battery\":100},\"start_date\":\"2025-01-13\",\"ongoing\":false,\"end_date\":\"Optional.empty\"}", json);
    }
}