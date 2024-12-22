package sap.ass02.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.application.RideService;
import sap.ass02.domain.*;
import sap.ass02.domain.dto.RideDTO;
import sap.ddd.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RideServiceComponentTest {
    private final EBike eBike1 = new EBike("1", EBike.EBikeState.AVAILABLE, new P2d(1.0, 2.0), new V2d(3.0, 4.0), 5.0, 11);
    private final User user1 = new User("1", 10);
    private final RideDTO ride1 = new RideDTO(new Date(1), Optional.of(new Date(1)), this.user1.toDTO(), this.eBike1.toDTO(), true, "1");
    private RideService rideService;
    
    @BeforeEach
    public void setUp() {
        Repository repository = mock(Repository.class);
        when(repository.getRideById("1")).thenReturn(Optional.of(this.ride1));
        when(repository.getRideById("1", "1")).thenReturn(Optional.of(this.ride1));
        when(repository.getOngoingRideById("1", "1")).thenReturn(Optional.of(this.ride1));
        when(repository.getAllRides()).thenReturn(List.of(this.ride1));
        this.rideService = new RideService();
        this.rideService.attachRepository(repository);
    }
    
    @Test
    public void testGetRides() {
        List<Ride> actual = new ArrayList<>();
        this.rideService.getRides().forEach(actual::add);
        List<Ride> expected = List.of(new Ride(this.ride1.id(), new User(this.ride1.user().id()), new EBike(this.ride1.ebike().id())));
        assertEquals(expected.getFirst().getEBike().getId(), actual.getFirst().getEBike().getId());
    }
    
    @Test
    public void testGetRideById() {
        Ride ride = this.rideService.getRide("1");
        Ride expected = new Ride(this.ride1.id(), new User(this.ride1.user().id()), new EBike(this.ride1.ebike().id()));
        assertEquals(expected.getEBike().getId(), ride.getEBike().getId());
    }
    
    @Test
    public void testGetOngoingRide() {
        Ride ride = this.rideService.getOngoingRide("1", "1");
        Ride expected = new Ride(this.ride1.id(), new User(this.ride1.user().id()), new EBike(this.ride1.ebike().id()));
        assertEquals(expected.getEBike().getId(), ride.getEBike().getId());
    }
}
