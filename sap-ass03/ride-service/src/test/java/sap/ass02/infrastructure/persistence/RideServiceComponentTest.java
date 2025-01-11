package sap.ass02.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.application.RideServiceVerticle;
import sap.ass02.domain.*;
import sap.ass02.domain.dto.RideDTO;
import sap.ddd.ReadOnlyRepository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RideServiceComponentTest {
    private final EBike eBike1 = new EBikeImpl("1", EBikeImpl.BikeState.AVAILABLE, new P2d(1.0, 2.0), new V2d(3.0, 4.0), 5.0, 11);
    private final User user1 = new User("1", 10);
    private final RideDTO ride1 = new RideDTO(new Date(1), Optional.of(new Date(1)), this.user1.toDTO(), this.eBike1.toDTO(), true, "1");
    private RideServiceVerticle rideService;
    
    @BeforeEach
    public void setUp() {
        ReadOnlyRepository repository = mock(ReadOnlyRepository.class);
        when(repository.getRideById("1")).thenReturn(Optional.of(this.ride1));
        when(repository.getRideById("1", "1")).thenReturn(Optional.of(this.ride1));
        when(repository.getOngoingRideById("1", "1")).thenReturn(Optional.of(this.ride1));
        when(repository.getAllRides()).thenReturn(List.of(this.ride1));
        this.rideService = new RideServiceVerticle();
        this.rideService.attachRepository(repository);
    }
    
    @Test
    public void testGetRides() {
        List<Ride> actual = new ArrayList<>();
        this.rideService.getRides().forEach(actual::add);
        List<Ride> expected = List.of(new Ride(this.ride1.id(), new User(this.ride1.user().id()), new EBikeImpl(this.ride1.ebike().id())));
        assertEquals(expected.getFirst().getBike().getBikeId(), actual.getFirst().getBike().getBikeId());
    }
    
    @Test
    public void testGetRideById() {
        Ride ride = this.rideService.getRide("1");
        Ride expected = new Ride(this.ride1.id(), new User(this.ride1.user().id()), new EBikeImpl(this.ride1.ebike().id()));
        assertEquals(expected.getBike().getBikeId(), ride.getBike().getBikeId());
    }
    
    @Test
    public void testGetOngoingRide() {
        Ride ride = this.rideService.getOngoingRide("1", "1");
        Ride expected = new Ride(this.ride1.id(), new User(this.ride1.user().id()), new EBikeImpl(this.ride1.ebike().id()));
        assertEquals(expected.getBike().getBikeId(), ride.getBike().getBikeId());
    }
}
