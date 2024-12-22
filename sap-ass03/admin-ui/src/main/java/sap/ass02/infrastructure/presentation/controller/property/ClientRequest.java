package sap.ass02.infrastructure.presentation.controller.property;

import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClient;
import sap.ass02.domain.EBike;
import sap.ass02.domain.User;

public interface ClientRequest {
    /**
     * Attach a web client to the controller.
     *
     * @param webClient the web client to attach
     * @param host the host of the web client
     * @param port the port of the web client
     */
    void attachWebClient(final WebClient webClient, final String host, final int port);
    
    /**
     * Add a user to the system.
     *
     * @param userId the user id
     * @param credits the user credits
     * @return a future that will be completed with the result of the operation
     */
    Future<Boolean> addUser(final String userId, final int credits);
    
    /**
     * Get a user from the system.
     *
     * @param userId the user id
     * @return a future that will be completed with the user
     */
    Future<User> getUser(final String userId);
    
    /**
     * Get all users from the system.
     *
     * @return a future that will be completed with the users
     */
    Future<Iterable<User>> getAllUsers();
    
    /**
     * Add an eBike to the system.
     *
     * @param eBikeId the eBike id
     * @return a future that will be completed with the result of the operation
     */
    Future<Boolean> addEBike(String eBikeId);
    
    /**
     * Get an eBike from the system.
     *
     * @param eBikeId the eBike id
     * @return a future that will be completed with the eBike
     */
    Future<EBike> getEBike(String eBikeId);
    
    /**
     * Get all eBikes from the system.
     *
     * @return a future that will be completed with the eBikes
     */
    Future<Iterable<EBike>> getAllEBikes();
    
    /**
     * Start a ride.
     *
     * @param userId the user id
     * @param eBikeId the eBike id
     * @return a future that will be completed with the result of the operation
     */
    Future<Boolean> startRide(String userId, String eBikeId);
    
    /**
     * Get a ride.
     *
     * @param userId the user id
     * @param eBikeId the eBike id
     * @return a future that will be completed with the ride
     */
    Future<String> getRide(String userId, String eBikeId);
    
    /**
     * Stop a ride.
     *
     * @param rideId the ride id
     * @return a future that will be completed with the result of the operation
     */
    Future<Boolean> stopRide(String rideId);
    
    /**
     * Get an ongoing ride.
     *
     * @param userId the user id
     * @param eBikeId the eBike id
     * @return a future that will be completed with the ride
     */
    Future<String> getOngoingRide(String userId, String eBikeId);
}
