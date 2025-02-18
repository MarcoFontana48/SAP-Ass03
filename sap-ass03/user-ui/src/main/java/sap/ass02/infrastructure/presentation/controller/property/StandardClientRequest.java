package sap.ass02.infrastructure.presentation.controller.property;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.utils.EndpointPath;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.EBike;
import sap.ass02.domain.User;

import java.util.ArrayList;
import java.util.List;

public final class StandardClientRequest implements ClientRequest {
    private static final Logger LOGGER = LogManager.getLogger(StandardClientRequest.class);
    private WebClient webClient;
    private String host;
    private int port;
    
    @Override
    public void attachWebClient(final WebClient webClient, final String host, final int port) {
        this.webClient = webClient;
        this.host = host;
        this.port = port;
        LOGGER.trace("Attached model of type '{}' to webController '{}'", webClient.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    @Override
    public Future<Boolean> addUser(String userId, int credits) {
        LOGGER.trace("about to POST add user with id '{}' and credits '{}'", userId, credits);
        Promise<Boolean> result = Promise.promise();
        JsonObject request = new JsonObject()
                .put(JsonFieldKey.USER_ID_KEY, userId)
                .put(JsonFieldKey.USER_CREDIT_KEY, credits);
        
        LOGGER.trace("Sending request to endpoint '" + EndpointPath.USER + "':\n{}", request.encodePrettily());
        this.webClient.post(this.port, this.host, EndpointPath.USER)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .sendJsonObject(request, ar -> {
                    if (ar.succeeded()) {
                        LOGGER.trace("Received response with status code {}", ar.result().statusCode());
                        LOGGER.trace("Response: {}", ar.result().body());
                        result.complete(true);
                    } else {
                        LOGGER.error("Failed to send request: {}", ar.cause().getMessage());
                        result.fail(ar.cause());
                    }
                });
        return result.future();
    }
    
    @Override
    public Future<Boolean> addEBike(String eBikeId) {
        LOGGER.trace("about to POST add ebike with id '{}'", eBikeId);
        Promise<Boolean> result = Promise.promise();
        JsonObject request = new JsonObject()
                .put(JsonFieldKey.EBIKE_ID_KEY, eBikeId);
        
        LOGGER.trace("Sending request to endpoint '" + EndpointPath.EBIKE + "':\n{}", request.encodePrettily());
        this.webClient.post(this.port, this.host, EndpointPath.EBIKE)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .sendJsonObject(request, ar -> {
                    if (ar.succeeded()) {
                        LOGGER.trace("Received response with status code {}", ar.result().statusCode());
                        LOGGER.trace("Response: {}", ar.result().body());
                        result.complete(true);
                    } else {
                        LOGGER.error("Failed to send request: {}", ar.cause().getMessage());
                        result.fail(ar.cause());
                    }
                });
        return result.future();
    }
    
    @Override
    public Future<Boolean> startRide(String userId, String eBikeId) {
        LOGGER.trace("About to start ride, checking if ride already exists");
        Promise<Boolean> result = Promise.promise();
        this.getRide(userId, eBikeId).onComplete(ar -> {
            if (ar.succeeded()) {
                LOGGER.trace("Ride already exists, checking if it is ongoing");
                JsonObject response = new JsonObject(ar.result());
                LOGGER.trace("retrieved ride: '{}'", response.toString());
                if (response.getString(JsonFieldKey.JSON_RIDE_END_DATE_KEY) != null) {
                    this.resumeRide(userId, eBikeId, result);
                    result.complete(true);
                } else {
                    LOGGER.trace("Ride is ongoing, nothing to do, returning false");
                    result.complete(false);
                }
            } else {
                LOGGER.trace("Ride does not exist, creating new ride");
                this.startNewRide(userId, eBikeId, result);
            }
        });
        return result.future();
    }
    
    private void startNewRide(String userId, String eBikeId, Promise<Boolean> result) {
        LOGGER.trace("about to POST start ride for user with id '{}' and eBike with id '{}'", userId, eBikeId);
        JsonObject request = toJsonRide(userId, eBikeId, "start");
        
        LOGGER.trace("Sending request to endpoint '" + EndpointPath.RIDE + "':\n{}", request.encodePrettily());
        this.webClient.post(this.port, this.host, EndpointPath.RIDE)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .sendJsonObject(request, asyncRes -> {
                    if (asyncRes.succeeded()) {
                        LOGGER.trace("Received response with status code {}", asyncRes.result().statusCode());
                        LOGGER.trace("Response: {}", asyncRes.result().body());
                        result.complete(true);
                    } else {
                        LOGGER.error("Failed to send request: {}", asyncRes.cause().getMessage());
                        result.fail(asyncRes.cause());
                    }
                });
    }
    
    private void resumeRide(String userId, String eBikeId, Promise<Boolean> result) {
        LOGGER.trace("Ride is not ongoing but is present, sending PUT request to resume ride");
        LOGGER.trace("about to PUT start ride for user with id '{}' and eBike with id '{}'", userId, eBikeId);
        JsonObject request = toJsonRide(userId, eBikeId, "start");
        
        LOGGER.trace("Sending request to endpoint '" + EndpointPath.RIDE + "':\n{}", request.encodePrettily());
        this.webClient.put(this.port, this.host, EndpointPath.RIDE)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .sendJsonObject(request, asyncRes -> {
                    if (asyncRes.succeeded()) {
                        LOGGER.trace("Received response with status code {}", asyncRes.result().statusCode());
                        LOGGER.trace("Response: {}", asyncRes.result().body());
                        result.complete(true);
                    } else {
                        LOGGER.error("Failed to send request: {}", asyncRes.cause().getMessage());
                        result.fail(asyncRes.cause());
                    }
                });
    }
    
    private static JsonObject toJsonRide(String userId, String eBikeId, String action) {
        return new JsonObject()
                .put(JsonFieldKey.JSON_RIDE_ID_KEY, userId + eBikeId)
                .put(JsonFieldKey.USER_ID_KEY, userId)
                .put(JsonFieldKey.EBIKE_ID_KEY, eBikeId)
                .put(JsonFieldKey.JSON_RIDE_ACTION, action);
    }
    
    @Override
    public Future<String> getRide(String userId, String eBikeId) {
        Promise<String> promise = Promise.promise();
        LOGGER.trace("about to GET ride from user id '{}' and eBike id '{}'", userId, eBikeId);
        
        this.webClient.get(this.port, this.host, EndpointPath.RIDE)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .addQueryParam(JsonFieldKey.JSON_RIDE_ID_KEY, userId + eBikeId)
                .send(ar -> {
                    if (ar.succeeded()) {
                        LOGGER.trace("Received response with status code {}", ar.result().statusCode());
                        String resultBody = ar.result().body();
                        LOGGER.trace("Response: {}", resultBody);
                        JsonArray response = new JsonArray(resultBody);
                        
                        if (!response.isEmpty()) {
                            JsonObject responseJsonObject = response.getJsonObject(0);
                            promise.complete(responseJsonObject.encode());
                        } else {
                            promise.fail("Ride not found");
                        }
                    } else {
                        LOGGER.error("Failed to send request: {}", ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }
    
    @Override
    public Future<String> getOngoingRide(String userId, String eBikeId) {
        Promise<String> promise = Promise.promise();
        LOGGER.trace("about to GET ongoing ride from user id '{}' and eBike id '{}'", userId, eBikeId);
        JsonObject request = new JsonObject()
                .put(JsonFieldKey.USER_ID_KEY, userId)
                .put(JsonFieldKey.EBIKE_ID_KEY, eBikeId);
        
        LOGGER.trace("Sending request to endpoint '" + EndpointPath.ONGOING_RIDE + "':\n{}", request.encodePrettily());
        this.webClient.get(this.port, this.host, EndpointPath.ONGOING_RIDE)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .sendJsonObject(request, ar -> {
                    if (ar.succeeded()) {
                        LOGGER.trace("Received response with status code {}", ar.result().statusCode());
                        LOGGER.trace("Response: {}", ar.result().body());
                        
                        JsonObject response = new JsonObject(ar.result().body());
                        promise.complete(response.getString(JsonFieldKey.JSON_RIDE_ID_KEY));
                    } else {
                        LOGGER.error("Failed to send request: {}", ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }
    
    @Override
    public Future<Boolean> stopRide(String rideId) {
        LOGGER.trace("about to PUT stop ride for ride with id '{}'", rideId);
        Promise<Boolean> result = Promise.promise();
        JsonObject request = new JsonObject()
                .put(JsonFieldKey.JSON_RIDE_ID_KEY, rideId)
                .put(JsonFieldKey.JSON_RIDE_ACTION, "stop");
        
        LOGGER.trace("Sending request to endpoint '" + EndpointPath.RIDE + "':\n{}", request.encodePrettily());
        this.webClient.put(this.port, this.host, EndpointPath.RIDE)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .sendJsonObject(request, ar -> {
                    if (ar.succeeded()) {
                        LOGGER.trace("Received response with status code {}", ar.result().statusCode());
                        LOGGER.trace("Response: {}", ar.result().body());
                        result.complete(true);
                    } else {
                        LOGGER.error("Failed to send request: {}", ar.cause().getMessage());
                        result.fail(ar.cause());
                    }
                });
        return result.future();
    }
    
    @Override
    public Future<Iterable<EBike>> getAllEBikes() {
        Promise<Iterable<EBike>> promise = Promise.promise();
        LOGGER.trace("about to GET all eBikes");
        
        LOGGER.trace("Sending request to endpoint '" + EndpointPath.EBIKE + "'");
        this.webClient.get(this.port, this.host, EndpointPath.EBIKE)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .send(ar -> {
                    if (ar.succeeded()) {
                        LOGGER.trace("Received response with status code {}", ar.result().statusCode());
                        LOGGER.trace("Response: {}", ar.result().body());
                        
                        JsonArray response = new JsonArray(ar.result().body());
                        List<EBike> ebikes = new ArrayList<>();
                        response.forEach(ebike -> {
                            JsonObject ebikeJson = (JsonObject) ebike;
                            EBike e = new EBike(ebikeJson.getString(JsonFieldKey.EBIKE_ID_KEY));
                            e.updateSpeed(ebikeJson.getInteger(JsonFieldKey.EBIKE_SPEED_KEY));
                            e.updateDirection(ebikeJson.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), ebikeJson.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY));
                            e.updateState(EBike.EBikeState.valueOf(ebikeJson.getString(JsonFieldKey.EBIKE_STATE_KEY)));
                            e.updateLocation(ebikeJson.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), ebikeJson.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY));
                            e.rechargeBattery();
                            e.decreaseBatteryLevel(100 - ebikeJson.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY));
                            ebikes.add(e);
                        });
                        
                        promise.complete(ebikes);
                    } else {
                        LOGGER.error("Failed to send request: {}", ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }
    
    @Override
    public Future<Iterable<User>> getAllUsers() {
        Promise<Iterable<User>> promise = Promise.promise();
        LOGGER.trace("about to GET all users");
        
        LOGGER.trace("Sending request to endpoint '" + EndpointPath.USER + "'");
        this.webClient.get(this.port, this.host, EndpointPath.USER)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .send(ar -> {
                    if (ar.succeeded()) {
                        LOGGER.trace("Received response with status code {}", ar.result().statusCode());
                        LOGGER.trace("Response: {}", ar.result().body());
                        
                        JsonArray response = new JsonArray(ar.result().body());
                        List<User> users = new ArrayList<>();
                        response.forEach(user -> {
                            JsonObject userJson = (JsonObject) user;
                            User u = new User(userJson.getString(JsonFieldKey.USER_ID_KEY));
                            u.rechargeCredit(userJson.getInteger(JsonFieldKey.USER_CREDIT_KEY));
                            users.add(u);
                        });
                        
                        promise.complete(users);
                    } else {
                        LOGGER.error("Failed to send request: {}", ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }
    
    @Override
    public Future<User> getUser(String userId) {
        Promise<User> promise = Promise.promise();
        LOGGER.trace("about to GET user with id '{}'", userId);
        JsonObject request = new JsonObject()
                .put(JsonFieldKey.USER_ID_KEY, userId);
        
        LOGGER.trace("Sending request to endpoint " + EndpointPath.USER + ":\n{}", request.encodePrettily());
        this.webClient.get(this.port, this.host, EndpointPath.USER)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .sendJsonObject(request, ar -> {
                    if (ar.succeeded()) {
                        LOGGER.trace("Received response with status code {}", ar.result().statusCode());
                        LOGGER.trace("Response: {}", ar.result().body());
                        
                        JsonObject response = new JsonObject(ar.result().body());
                        User user = new User(response.getString(JsonFieldKey.USER_ID_KEY));
                        user.rechargeCredit(response.getInteger(JsonFieldKey.USER_CREDIT_KEY));
                        
                        promise.complete(user);
                    } else {
                        LOGGER.error("Failed to send request: {}", ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }
    
    @Override
    public Future<EBike> getEBike(String eBikeId) {
        Promise<EBike> promise = Promise.promise();
        LOGGER.trace("about to GET eBike with id '{}'", eBikeId);
        JsonObject request = new JsonObject()
                .put(JsonFieldKey.EBIKE_ID_KEY, eBikeId);
        
        LOGGER.trace("Sending request to endpoint '" + EndpointPath.EBIKE + "':\n{}", request.encodePrettily());
        this.webClient.get(this.port, this.host, EndpointPath.EBIKE)
                .putHeader("content-type", "application/json")
                .as(BodyCodec.string())
                .sendJsonObject(request, ar -> {
                    if (ar.succeeded()) {
                        LOGGER.trace("Received response with status code {}", ar.result().statusCode());
                        LOGGER.trace("Response: {}", ar.result().body());
                        
                        JsonObject response = new JsonObject(ar.result().body());
                        EBike ebike = new EBike(response.getString(JsonFieldKey.EBIKE_ID_KEY));
                        ebike.updateSpeed(response.getInteger(JsonFieldKey.EBIKE_SPEED_KEY));
                        ebike.updateDirection(response.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), response.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY));
                        ebike.updateState(EBike.EBikeState.valueOf(response.getString(JsonFieldKey.EBIKE_STATE_KEY)));
                        ebike.updateLocation(response.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), response.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY));
                        ebike.rechargeBattery();
                        ebike.decreaseBatteryLevel(100 - response.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY));
                        promise.complete(ebike);
                    } else {
                        LOGGER.error("Failed to send request: {}", ar.cause().getMessage());
                        promise.fail(ar.cause());
                    }
                });
        return promise.future();
    }
}
