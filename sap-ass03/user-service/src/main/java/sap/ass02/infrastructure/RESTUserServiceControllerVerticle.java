package sap.ass02.infrastructure;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

public final class RESTUserServiceControllerVerticle extends AbstractVerticle implements Controller {
    private static final Logger LOGGER = LogManager.getLogger(RESTUserServiceControllerVerticle.class);
    private static final int HTTP_PORT = 8080;
    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;
    private static final RequestsCounter REQUESTS_COUNTER = new PrometheusRequestsCounter();
    private static final PrometheusPerformanceMeasurer PERFORMANCE_MEASURER = new PrometheusPerformanceMeasurer();
    private Service service;
    private CircuitBreaker circuitBreaker;
    
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
    
    public void attachService(Service service) {
        this.service = service;
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
        
        router.post(EndpointPath.USER).handler(this::postUserHandler);
        router.put(EndpointPath.USER).handler(this::putUserHandler);
        router.get(EndpointPath.USER).handler(this::getUserHandler);
        router.get(EndpointPath.HEALTH_CHECK).handler(this::getHealthCheckHandler);
        router.get(EndpointPath.METRICS).handler(this::getMetricsHandler);
        
        this.vertx.createHttpServer().requestHandler(router).listen(HTTP_PORT);
    }
    
    private void postUserHandler(RoutingContext routingContext) {
        Histogram.Timer latencyTimer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            JsonObject jsonBody = routingContext.getBodyAsJson();
            
            LOGGER.info("Received POST request with body: {}", jsonBody);
            
            String userId = jsonBody.getString(JsonFieldKey.USER_ID_KEY);
            int credit = jsonBody.getInteger(JsonFieldKey.USER_CREDIT_KEY);
            Double xLocation = jsonBody.getDouble(JsonFieldKey.USER_X_LOCATION_KEY);
            Double yLocation = jsonBody.getDouble(JsonFieldKey.USER_Y_LOCATION_KEY);
            
            boolean userWasAdded;
            if (xLocation != null && yLocation != null) {
                userWasAdded = this.service.addUser(userId, credit, xLocation, yLocation);
                promise.complete();
            } else {
                userWasAdded = this.service.addUser(userId, credit);
            }
            
            if (userWasAdded) {
                promise.complete();
            } else {
                promise.fail("Failed to add user");
            }
        }).onComplete(res -> {
            PERFORMANCE_MEASURER.stopTimer(latencyTimer);
            if (res.succeeded()) {
                LOGGER.info("User added successfully, returning status code '{}' to client", STATUS_CODE_OK);
                REQUESTS_COUNTER.increasePostRequestsCounter(STATUS_CODE_OK);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_OK);
            } else {
                LOGGER.error("Failed to add user, returning status code '{}' to client", STATUS_CODE_INTERNAL_SERVER_ERROR);
                REQUESTS_COUNTER.increasePostRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_INTERNAL_SERVER_ERROR);
            }
        });
    }
    
    private void putUserHandler(RoutingContext routingContext) {
        Histogram.Timer latencyTimer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            JsonObject jsonBody = routingContext.getBodyAsJson();
            
            LOGGER.info("Received PUT request with body: {}", jsonBody);
            
            boolean userWasUpdated = this.updateUserHandler(jsonBody);
            
            if (userWasUpdated) {
                promise.complete();
            } else {
                promise.fail("Failed to update user");
            }
        }).onComplete(res -> {
            PERFORMANCE_MEASURER.stopTimer(latencyTimer);
            if (res.succeeded()) {
                LOGGER.info("User updated successfully, returning status code '{}' to client", STATUS_CODE_OK);
                REQUESTS_COUNTER.increasePutRequestsCounter(STATUS_CODE_OK);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_OK);
            } else {
                LOGGER.error("Failed to update user, returning status code '{}' to client", STATUS_CODE_INTERNAL_SERVER_ERROR);
                REQUESTS_COUNTER.increasePutRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_INTERNAL_SERVER_ERROR);
            }
        });
    }
    
    private boolean updateUserHandler(JsonObject jsonBody) {
        String userId = jsonBody.getString(JsonFieldKey.USER_ID_KEY);
        int credit = jsonBody.getInteger(JsonFieldKey.USER_CREDIT_KEY);
        
        return this.service.updateUserCredits(userId, credit);
    }
    
    private void getUserHandler(RoutingContext routingContext) {
        Histogram.Timer latencyTimer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            LOGGER.info("Received GET request");
            
            String userId = routingContext.request().getParam(JsonFieldKey.USER_ID_KEY);
            JsonArray response = new JsonArray();
            
            if (userId != null) {
                // Retrieve specific user
                User user = this.service.getUser(userId);
                if (user != null) {
                    response.add(JsonUtils.fromUserToJsonObject(user));
                }
            } else {
                // Retrieve all users
                Iterable<User> users = this.service.getUsers();
                users.forEach(user -> response.add(JsonUtils.fromUserToJsonObject(user)));
            }
            
            promise.complete(response);
        }).onComplete(res -> {
            PERFORMANCE_MEASURER.stopTimer(latencyTimer);
            if (res.succeeded()) {
                JsonArray response = (JsonArray) res.result();
                LOGGER.info("User(s) retrieved successfully, returning status code '{}' to client", STATUS_CODE_OK);
                REQUESTS_COUNTER.increaseGetRequestsCounter(STATUS_CODE_OK);
                sendResponse(routingContext, response, STATUS_CODE_OK);
            } else {
                LOGGER.error("Failed to retrieve user(s), returning status code '{}' to client", STATUS_CODE_INTERNAL_SERVER_ERROR);
                REQUESTS_COUNTER.increaseGetRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                sendResponse(routingContext, new JsonArray(), STATUS_CODE_INTERNAL_SERVER_ERROR);
            }
        });
    }
    
    private void getHealthCheckHandler(RoutingContext routingContext) {
        Histogram.Timer latencyTimer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            LOGGER.info("Received GET request");
            
            JsonObject response = new JsonObject();
            response.put("status", "ok");
            
            promise.complete(response);
        }).onComplete(res -> {
            PERFORMANCE_MEASURER.stopTimer(latencyTimer);
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
