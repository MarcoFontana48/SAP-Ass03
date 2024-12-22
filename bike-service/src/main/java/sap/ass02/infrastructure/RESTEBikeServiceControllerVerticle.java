package sap.ass02.infrastructure;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.common.TextFormat;
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
import sap.ass02.domain.EBike;
import sap.ass02.domain.P2d;
import sap.ass02.domain.V2d;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.utils.JsonUtils;
import sap.ass02.domain.Controller;
import sap.ddd.Service;
import sap.ass02.infrastructure.utils.PrometheusPerformanceMeasurer;
import sap.ass02.infrastructure.utils.PrometheusRequestsCounter;
import sap.ass02.infrastructure.utils.RequestsCounter;

import java.io.IOException;
import java.io.StringWriter;

public final class RESTEBikeServiceControllerVerticle extends AbstractVerticle implements Controller {
    private static final Logger LOGGER = LogManager.getLogger(RESTEBikeServiceControllerVerticle.class);
    private static final int HTTP_PORT = 8080;
    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;
    private static final RequestsCounter REQUESTS_COUNTER = new PrometheusRequestsCounter();
    private static final PrometheusPerformanceMeasurer PERFORMANCE_MEASURER = new PrometheusPerformanceMeasurer();
    private Service service;
    private CircuitBreaker circuitBreaker;
    
    public void attachService(Service service) {
        this.service = service;
    }
    
    @Override
    public void start() {
        this.circuitBreaker = CircuitBreaker.create("bike-service-circuit-breaker", this.vertx,
                new CircuitBreakerOptions()
                        .setMaxFailures(5)
                        .setTimeout(2000)
                        .setFallbackOnFailure(true)
                        .setResetTimeout(10000)
        );
        
        Router router = Router.router(this.vertx);
        router.route().handler(BodyHandler.create());
        
        router.post(EndpointPath.EBIKE).handler(this::postEbikeHandler);
        router.put(EndpointPath.EBIKE).handler(this::putEbikeHandler);
        router.get(EndpointPath.EBIKE).handler(this::getEbikeHandler);
        router.get(EndpointPath.HEALTH_CHECK).handler(this::getHealthCheckHandler);
        router.get(EndpointPath.METRICS).handler(this::getMetricsHandler);
        
        this.vertx.createHttpServer().requestHandler(router).listen(HTTP_PORT);
    }
    
    private void postEbikeHandler(RoutingContext routingContext) {
        Histogram.Timer timer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            JsonObject jsonBody = routingContext.getBodyAsJson();
            
            LOGGER.info("Received POST request with body: {}", jsonBody);
            
            String ebikeId = jsonBody.getString(JsonFieldKey.EBIKE_ID_KEY);
            
            boolean ebikeWasAdded = this.service.addEBike(ebikeId);
            
            if (ebikeWasAdded) {
                promise.complete();
            } else {
                promise.fail("Failed to add ebike");
            }
        }).onComplete(res -> {
            PERFORMANCE_MEASURER.stopTimer(timer);
            if (res.succeeded()) {
                LOGGER.info("Ebike added successfully, returning status code '{}' to client", STATUS_CODE_OK);
                REQUESTS_COUNTER.increasePostRequestsCounter(STATUS_CODE_OK);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_OK);
            } else {
                LOGGER.error("Failed to add user, returning status code '{}' to client", STATUS_CODE_INTERNAL_SERVER_ERROR);
                REQUESTS_COUNTER.increasePostRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_INTERNAL_SERVER_ERROR);
            }
        });
    }
    
    private void putEbikeHandler(RoutingContext routingContext) {
        Histogram.Timer timer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            JsonObject jsonBody = routingContext.getBodyAsJson();
            
            LOGGER.info("Received PUT request with body: {}", jsonBody);
            
            boolean ebikeWasUpdated = this.updateEBikeHandler(jsonBody);
            if (ebikeWasUpdated) {
                promise.complete();
            } else {
                promise.fail("Failed to update ebike");
            }
        }).onComplete(res -> {
            PERFORMANCE_MEASURER.stopTimer(timer);
            if (res.succeeded()) {
                LOGGER.info("Ebike battery level updated successfully, returning status code '{}' to client", STATUS_CODE_OK);
                REQUESTS_COUNTER.increasePutRequestsCounter(STATUS_CODE_OK);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_OK);
            } else {
                LOGGER.error("Failed to update ebike battery level, returning status code '{}' to client", STATUS_CODE_INTERNAL_SERVER_ERROR);
                REQUESTS_COUNTER.increasePutRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                sendResponse(routingContext, new JsonObject(), STATUS_CODE_INTERNAL_SERVER_ERROR);
            }
        });
    }
    
    private boolean updateEBikeHandler(JsonObject jsonBody) {
        EBike ebike = new EBike(
                jsonBody.getString(JsonFieldKey.EBIKE_ID_KEY),
                EBike.EBikeState.valueOf(jsonBody.getString(JsonFieldKey.EBIKE_STATE_KEY)),
                new P2d(jsonBody.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), jsonBody.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY)),
                new V2d(jsonBody.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), jsonBody.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY)),
                jsonBody.getDouble(JsonFieldKey.EBIKE_SPEED_KEY),
                jsonBody.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY)
        );
        
        return this.service.updateEBike(ebike);
    }
    
    private void getEbikeHandler(RoutingContext routingContext) {
        Histogram.Timer latencyTimer = PERFORMANCE_MEASURER.startTimer();
        this.circuitBreaker.execute(promise -> {
            LOGGER.info("Received GET request");
            
            String ebikeId = routingContext.request().getParam(JsonFieldKey.EBIKE_ID_KEY);
            JsonArray response = new JsonArray();
            
            if (ebikeId != null) {
                // Retrieve specific eBike
                EBike eBike = this.service.getEBike(ebikeId);
                if (eBike != null) {
                    response.add(JsonUtils.fromEBikeToJsonObject(eBike));
                }
            } else {
                // Retrieve all eBikes
                Iterable<EBike> eBikes = this.service.getEBikes();
                eBikes.forEach(eBike -> response.add(JsonUtils.fromEBikeToJsonObject(eBike)));
            }
            
            promise.complete(response);
        }).onComplete(res -> {
            PERFORMANCE_MEASURER.stopTimer(latencyTimer);
            if (res.succeeded()) {
                JsonArray response = (JsonArray) res.result();
                LOGGER.info("EBike(s) retrieved successfully, returning status code '{}' to client", STATUS_CODE_OK);
                REQUESTS_COUNTER.increaseGetRequestsCounter(STATUS_CODE_OK);
                sendResponse(routingContext, response, STATUS_CODE_OK);
            } else {
                LOGGER.error("Failed to retrieve ebike(s), returning status code '{}' to client", STATUS_CODE_INTERNAL_SERVER_ERROR);
                REQUESTS_COUNTER.increaseGetRequestsCounter(STATUS_CODE_INTERNAL_SERVER_ERROR);
                sendResponse(routingContext, new JsonArray(), STATUS_CODE_INTERNAL_SERVER_ERROR);
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
}
