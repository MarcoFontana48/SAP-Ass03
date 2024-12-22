package sap.ass02.infrastructure;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EBike;
import sap.ass02.domain.Ride;
import sap.ass02.domain.User;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.utils.JsonUtils;
import sap.ass02.domain.Controller;
import sap.ddd.Service;
import sap.ass02.infrastructure.utils.PrometheusPerformanceMeasurer;
import sap.ass02.infrastructure.utils.PrometheusRequestsCounter;
import sap.ass02.infrastructure.utils.RequestsCounter;

import java.io.IOException;
import java.io.StringWriter;

public final class RESTRideServiceControllerVerticle extends AbstractVerticle implements Controller {
    private static final Logger LOGGER = LogManager.getLogger(RESTRideServiceControllerVerticle.class);
    private static final PrometheusPerformanceMeasurer PERFORMANCE_MEASURER = new PrometheusPerformanceMeasurer();
    private static final RequestsCounter REQUESTS_COUNTER = new PrometheusRequestsCounter();
    private static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;
    private static final int STATUS_CODE_OK = 200;
    private static final int HTTP_PORT = 8080;
    private CircuitBreaker circuitBreaker;
    private Service service;
    private WebClient webClient;
    
    private static void sendResponse(RoutingContext routingContext, JsonArray response, int statusCode) {
        LOGGER.trace("Sending response with status code '{}' to client:\n{}", statusCode, response.encodePrettily());
        routingContext.response()
                .setStatusCode(statusCode)
                .putHeader("content-type", "application/json")
                .end(response.encode());
    }
    
    private static void sendResponse(RoutingContext routingContext, JsonObject response, int statusCode) {
        LOGGER.trace("Sending response with status code '{}' to client:\n{}", statusCode, response.encodePrettily());
        routingContext.response()
                .setStatusCode(statusCode)
                .putHeader("content-type", "application/json")
                .end(response.encode());
    }
    
    @Override
    public void attachService(Service service) {
        this.service = service;
        this.service.attachEventBus(this.vertx.eventBus());
    }
    
    @Override
    public void start() {
        this.circuitBreaker = CircuitBreaker.create("user-service-circuit-breaker", this.vertx,
                new CircuitBreakerOptions()
                        .setMaxFailures(5)
                        .setTimeout(2000)
                        .setFallbackOnFailure(true)
                        .setResetTimeout(10000)
        );
        
        JvmMetrics.builder().register();
        
        Router router = Router.router(this.vertx);
        router.route().handler(BodyHandler.create());
        
        router.post(EndpointPath.RIDE).handler(this::postRideHandler);
        router.put(EndpointPath.RIDE).handler(this::putRideHandler);
        router.get(EndpointPath.RIDE).handler(this::getRideHandler);
        router.get(EndpointPath.HEALTH_CHECK).handler(this::getHealthCheckHandler);
        router.get(EndpointPath.METRICS).handler(this::getMetricsHandler);
        
        this.webClient = WebClient.create(this.vertx);
        
        this.vertx.createHttpServer().requestHandler(router).listen(HTTP_PORT);
    }
    
    private void putRideHandler(RoutingContext routingContext) {
        Histogram.Timer latencyTimer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            JsonObject jsonBody = routingContext.getBodyAsJson();
            
            LOGGER.info("Received PUT request with body: {}", jsonBody);
            
            String rideId = jsonBody.getString(JsonFieldKey.RIDE_ID_KEY);
            String action = jsonBody.getString(JsonFieldKey.RIDE_ACTION);
            LOGGER.trace("Retrieved ride action: '{}'", action);
            
            if (action.equals("stop")) {
                this.service.stopRide(rideId);
                promise.complete();
            } else if (action.equals("start")) {
                String userId = jsonBody.getString(JsonFieldKey.USER_ID_KEY);
                String eBikeId = jsonBody.getString(JsonFieldKey.EBIKE_ID_KEY);
                
                Future<User> userFuture = this.sendGetUserByIdRequest(userId);
                Future<EBike> eBikeFuture = this.sendGetEBikeByIdRequest(eBikeId);
                
                // Wait for all futures to complete and handle the result
                Future.join(userFuture, eBikeFuture).onComplete(ar -> {
                    if (ar.succeeded()) {
                        LOGGER.trace("User and eBike futures completed successfully");
                        User user = userFuture.result();
                        EBike eBike = eBikeFuture.result();
                        
                        LOGGER.trace("Retrieved user: '{}'", user.toJsonString());
                        LOGGER.trace("Retrieved eBike: '{}'", eBike.toJsonString());
                        
                        Ride ride = new Ride(jsonBody.getString(JsonFieldKey.RIDE_ID_KEY), user, eBike);
                        LOGGER.trace("About to start ride: '{}'", ride.toJsonString());
                        
                        this.service.startRide(ride, user, eBike);
                        
                        promise.complete();
                    } else {
                        LOGGER.trace("User and eBike futures failed");
                        promise.fail(ar.cause());
                    }
                });
            } else {
                promise.fail("Invalid action");
            }
        }).onComplete(res -> {
            PERFORMANCE_MEASURER.stopTimer(latencyTimer);
            if (res.succeeded()) {
                LOGGER.info("Ride updated successfully, returning status code '{}' to client", STATUS_CODE_OK);
                REQUESTS_COUNTER.increasePutRequestsCounter(STATUS_CODE_OK);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_OK);
            } else {
                LOGGER.error("Failed to update ride, returning status code '{}' to client", STATUS_CODE_INTERNAL_SERVER_ERROR);
                REQUESTS_COUNTER.increasePutRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_INTERNAL_SERVER_ERROR);
            }
        });
    }
    
    private void getHealthCheckHandler(RoutingContext routingContext) {
        Histogram.Timer timer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            LOGGER.info("Received GET request");
            
            JsonObject response = new JsonObject();
            response.put("status", "ok");
            
            promise.complete(response);
        }).onComplete(res -> {
            PERFORMANCE_MEASURER.stopTimer(timer);
            if (res.succeeded()) {
                JsonObject response = (JsonObject) res.result();
                LOGGER.info("Health check succeeded, returning code '{}'", STATUS_CODE_OK);
                REQUESTS_COUNTER.increaseGetRequestsCounter(STATUS_CODE_OK);
                sendResponse(routingContext, response, STATUS_CODE_OK);
            } else {
                JsonObject response = new JsonObject();
                response.put("status", "fail");
                LOGGER.info("Health check did not succeed, returning code '{}'", STATUS_CODE_INTERNAL_SERVER_ERROR);
                REQUESTS_COUNTER.increaseGetRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                sendResponse(routingContext, response, STATUS_CODE_INTERNAL_SERVER_ERROR);
            }
        });
    }
    
    private void getRideHandler(RoutingContext routingContext) {
        Histogram.Timer latencyTimer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            LOGGER.info("Received GET request: '{}'", routingContext.request().uri());
            
            String rideId = routingContext.request().getParam(JsonFieldKey.RIDE_ID_KEY);
            JsonArray response = new JsonArray();
            
            if (rideId != null) {
                // Retrieve specific ride
                Ride ride = this.service.getRide(rideId);
                if (ride != null) {
                    response.add(JsonUtils.fromRideToJsonObject(ride));
                    promise.complete(response);
                }
            } else {
                // Retrieve all rides
                Iterable<Ride> rides = this.service.getRides();
                rides.forEach(ride -> response.add(JsonUtils.fromRideToJsonObject(ride)));
                promise.complete(response);
            }
        }).onComplete(res -> {
            PERFORMANCE_MEASURER.stopTimer(latencyTimer);
            if (res.succeeded()) {
                JsonArray response = (JsonArray) res.result();
                LOGGER.info("Ride(s) retrieved successfully, returning status code '{}' to client", STATUS_CODE_OK);
                REQUESTS_COUNTER.increaseGetRequestsCounter(STATUS_CODE_OK);
                sendResponse(routingContext, response, STATUS_CODE_OK);
            } else {
                LOGGER.error("Failed to retrieve ride(s), returning status code '{}' to client", STATUS_CODE_INTERNAL_SERVER_ERROR);
                REQUESTS_COUNTER.increaseGetRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                sendResponse(routingContext, new JsonArray(), STATUS_CODE_INTERNAL_SERVER_ERROR);
            }
        });
        
    }
    
    private void postRideHandler(RoutingContext routingContext) {
        Histogram.Timer latencyTimer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            JsonObject jsonBody = routingContext.getBodyAsJson();
            
            LOGGER.info("Received POST request with body: {}", jsonBody);
            
            String userId = jsonBody.getString(JsonFieldKey.USER_ID_KEY);
            String eBikeId = jsonBody.getString(JsonFieldKey.EBIKE_ID_KEY);
            
            LOGGER.trace("Retrieved IDs: user_id='{}', ebike_id='{}'", userId, eBikeId);
            
            Future<User> userFuture = this.sendGetUserByIdRequest(userId);
            Future<EBike> eBikeFuture = this.sendGetEBikeByIdRequest(eBikeId);
            
            // Wait for all futures to complete and handle the result
            Future.join(userFuture, eBikeFuture).onComplete(ar -> {
                if (ar.succeeded()) {
                    LOGGER.trace("User and eBike futures completed successfully");
                    User user = userFuture.result();
                    EBike eBike = eBikeFuture.result();
                    
                    LOGGER.trace("Retrieved user: '{}'", user.toJsonString());
                    LOGGER.trace("Retrieved eBike: '{}'", eBike.toJsonString());
                    
                    Ride ride = new Ride(jsonBody.getString(JsonFieldKey.RIDE_ID_KEY), user, eBike);
                    LOGGER.trace("About to start ride: '{}'", ride.toJsonString());
                    
                    this.service.startRide(ride, user, eBike);
                    
                    promise.complete();
                } else {
                    LOGGER.trace("User and eBike futures failed");
                    promise.fail(ar.cause());
                }
            }).onComplete(res -> {
                PERFORMANCE_MEASURER.stopTimer(latencyTimer);
                if (res.succeeded()) {
                    LOGGER.info("Ride added successfully, returning status code '{}' to client", STATUS_CODE_OK);
                    REQUESTS_COUNTER.increasePostRequestsCounter(STATUS_CODE_OK);
                    sendResponse(routingContext, new JsonObject(), STATUS_CODE_OK);
                } else {
                    LOGGER.error("Failed to add ride, returning status code '{}' to client", STATUS_CODE_INTERNAL_SERVER_ERROR);
                    REQUESTS_COUNTER.increasePostRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                    sendResponse(routingContext, new JsonObject(), STATUS_CODE_INTERNAL_SERVER_ERROR);
                }
            });
        }).onComplete(res -> {
            if (res.failed()) {
                LOGGER.error("Failed to add ride, returning status code '{}' to client", STATUS_CODE_INTERNAL_SERVER_ERROR);
                REQUESTS_COUNTER.increasePostRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_INTERNAL_SERVER_ERROR);
            } else {
                LOGGER.info("Ride added successfully, returning status code '{}' to client", STATUS_CODE_OK);
                REQUESTS_COUNTER.increasePostRequestsCounter(STATUS_CODE_OK);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_OK);
            }
        });
    }
    
    private Future<EBike> sendGetEBikeByIdRequest(String eBikeId) {
        String requestURI = EndpointPath.EBIKE + "?" + JsonFieldKey.EBIKE_ID_KEY + "=" + eBikeId;
        LOGGER.trace("Sending GET request to eBike service with URI '{}'", requestURI);
        
        Promise<EBike> promise = Promise.promise();
        
        this.webClient.get(HTTP_PORT, "bike-service", requestURI)
                .send()
                .onSuccess(response -> {
                    LOGGER.trace("Received response with status code: {}", response.statusCode());
                    if (response.statusCode() != STATUS_CODE_OK) {
                        LOGGER.warn("EBike not found, status code: {}", response.statusCode());
                        promise.fail("EBike not found");
                    } else {
                        LOGGER.trace("EBike found: '{}'", response.bodyAsJsonArray());
                        EBike eBike = JsonUtils.fromJsonStringToEBike(response.bodyAsJsonArray().getJsonObject(0).toString());
                        promise.complete(eBike);
                    }
                })
                .onFailure(err -> {
                    LOGGER.error("Failed to send request: {}", err.getMessage());
                    promise.fail(err);
                });
        
        return promise.future();
    }
    
    private Future<User> sendGetUserByIdRequest(String userId) {
        String requestURI = EndpointPath.USER + "?" + JsonFieldKey.USER_ID_KEY + "=" + userId;
        LOGGER.trace("Sending GET request to user service with URI '{}'", requestURI);
        
        Promise<User> promise = Promise.promise();
        
        this.webClient.get(HTTP_PORT, "user-service", requestURI)
                .send()
                .onSuccess(response -> {
                    LOGGER.trace("Received response with status code: {}", response.statusCode());
                    if (response.statusCode() != STATUS_CODE_OK) {
                        LOGGER.warn("User not found, status code: {}", response.statusCode());
                        promise.fail("User not found");
                    } else {
                        LOGGER.trace("User found: '{}'", response.bodyAsJsonArray());
                        User user = JsonUtils.fromJsonStringToUser(response.bodyAsJsonArray().getJsonObject(0).toString());
                        promise.complete(user);
                    }
                })
                .onFailure(err -> {
                    LOGGER.error("Failed to send request: {}", err.getMessage());
                    promise.fail(err);
                });
        
        return promise.future();
    }

    
    private void getMetricsHandler(RoutingContext routingContext) {
        routingContext.response().putHeader("Content-Type", TextFormat.CONTENT_TYPE_004);
        try (StringWriter writer = new StringWriter()) {
            TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
            String metricsOutput = writer.toString();
            LOGGER.trace("Metrics output: {}", metricsOutput);
            routingContext.response().end(metricsOutput);
        } catch (IOException e) {
            LOGGER.error("Failed to write metrics", e);
            routingContext.fail(e);
        }
    }
}
