package sap.ass02.infrastructure.presentation.controller.property;

import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClient;
import sap.ass02.domain.EBike;
import sap.ass02.domain.User;

public interface ClientRequest {
    void attachWebClient(final WebClient webClient, final String host, final int port);
    
    Future<Boolean> addUser(final String userId, final int credits);
    Future<User> getUser(final String userId);
    Future<Iterable<User>> getAllUsers();
    
    Future<Boolean> addEBike(String eBikeId);
    Future<EBike> getEBike(String eBikeId);
    Future<Iterable<EBike>> getAllEBikes();
    
    Future<Boolean> startRide(String userId, String eBikeId);
    Future<String> getRide(String userId, String eBikeId);
    Future<Boolean> stopRide(String rideId);
    Future<String> getOngoingRide(String userId, String eBikeId);
}
