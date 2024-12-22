package sap.ddd;

import io.vertx.core.eventbus.EventBus;
import sap.ass02.domain.EBike;
import sap.ass02.domain.Ride;
import sap.ass02.domain.User;

public interface Service {
    boolean startRide(final Ride ride, final User user, final EBike ebike);
    Ride getRide(String rideId);
    Ride getOngoingRide(String userId, String ebikeId);
    Iterable<Ride> getRides();
    boolean stopRide(final String rideId);
    
    void attachRepository(Repository repository);
    
    void updateUserCredits(Ride ride);
    
    void updateEBike(Ride ride);
    
    void attachEventBus(EventBus eventBus);
}
