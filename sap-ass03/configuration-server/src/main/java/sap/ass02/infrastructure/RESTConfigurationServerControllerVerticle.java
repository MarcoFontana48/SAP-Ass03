package sap.ass02.infrastructure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.port.Service;
import sap.ass02.application.ServiceImpl;
import sap.ass02.infrastructure.adapter.Controller;

/**
 * REST controller for the configuration server
 */
public final class RESTConfigurationServerControllerVerticle extends AbstractVerticle implements Controller {
    private static final Logger LOGGER = LogManager.getLogger(RESTConfigurationServerControllerVerticle.class);
    private static final int HTTP_PORT = 8080;
    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;
    private final Service service = new ServiceImpl();
    private CircuitBreaker circuitBreaker;
    
    /**
     * Start the REST controller
     */
    @Override
    public void start() {
        this.circuitBreaker = new CircuitBreaker.Builder()
                .setMaxFailures(5)
                .setTimeout(1000)
                .setResetTimeout(5000)
                .setState(CircuitBreaker.State.CLOSED)
                .build();
        
        Router router = Router.router(this.vertx);
        router.route().handler(BodyHandler.create());
        
        router.get(EndpointPath.USER_SERVICE_SQL_CONFIG).handler(this::getUserServiceSqlConfigHandler);
        router.get(EndpointPath.USER_SERVICE_MONGO_CONFIG).handler(this::getUserServiceMongoConfigHandler);
        router.get(EndpointPath.EBIKE_SERVICE_SQL_CONFIG).handler(this::getEbikeServiceSqlConfigHandler);
        router.get(EndpointPath.EBIKE_SERVICE_MONGO_CONFIG).handler(this::getEbikeServiceMongoConfigHandler);
        router.get(EndpointPath.HEALTH_CHECK).handler(this::getHealthCheckHandler);
        
        this.vertx.createHttpServer().requestHandler(router).listen(HTTP_PORT);
    }
    
    /**
     * Get the SQL credentials for the user service
     * @param routingContext the routing context
     */
    private void getEbikeServiceMongoConfigHandler(RoutingContext routingContext) {
        this.circuitBreaker.execute(() -> {
            LOGGER.trace("Received GET request for mongo config");
            JsonObject mongoCredentials = this.service.getEBikeServiceMongoCredentials().toJsonObject();
            LOGGER.trace("Retrieved mongo credentials:\n{}", mongoCredentials.encodePrettily());
            return mongoCredentials;
        }).thenAccept(mongoCredentials -> {
            LOGGER.trace("Sending response with status code '{}'", STATUS_CODE_OK);
            sendResponse(routingContext, mongoCredentials, STATUS_CODE_OK);
        }).exceptionally(ex -> {
            LOGGER.trace("Sending response with status code '{}'", STATUS_CODE_INTERNAL_SERVER_ERROR);
            sendResponse(routingContext, new JsonObject().put("error", ex.getMessage()), STATUS_CODE_INTERNAL_SERVER_ERROR);
            return null;
        });
    }
    
    /**
     * Get the SQL credentials for the user service
     * @param routingContext the routing context
     */
    private void getEbikeServiceSqlConfigHandler(RoutingContext routingContext) {
        this.circuitBreaker.execute(() -> {
            LOGGER.trace("Received GET request for sql config");
            JsonObject sqlCredentials = this.service.getEBikeServiceSqlCredentials().toJsonObject();
            LOGGER.trace("Retrieved SQL credentials: '{}'", sqlCredentials.encodePrettily());
            return sqlCredentials;
        }).thenAccept(sqlCredentials -> {
            LOGGER.trace("Sending response with status code '{}'", STATUS_CODE_OK);
            sendResponse(routingContext, sqlCredentials, STATUS_CODE_OK);
        }).exceptionally(ex -> {
            LOGGER.trace("Sending response with status code '{}'", STATUS_CODE_INTERNAL_SERVER_ERROR);
            sendResponse(routingContext, new JsonObject().put("error", ex.getMessage()), STATUS_CODE_INTERNAL_SERVER_ERROR);
            return null;
        });
    }
    
    /**
     * Get the health check
     * @param routingContext the routing context
     */
    private void getHealthCheckHandler(RoutingContext routingContext) {
        this.circuitBreaker.execute(() -> {
            LOGGER.info("Received GET request");
            
            JsonObject response = new JsonObject();
            response.put("status", "ok");
            
            return response;
        }).thenAccept(res -> {
            if (res.getString("status").equals("ok")) {
                sendResponse(routingContext, res, STATUS_CODE_OK);
            } else {
                sendResponse(routingContext, new JsonObject().put("status", "fail"), STATUS_CODE_INTERNAL_SERVER_ERROR);
            }
        });
    }
    
    /**
     * Get the SQL credentials for the user service
     * @param routingContext the routing context
     */
    private void getUserServiceMongoConfigHandler(RoutingContext routingContext) {
        this.circuitBreaker.execute(() -> {
            LOGGER.trace("Received GET request for mongo config");
            JsonObject mongoCredentials = this.service.getUserServiceMongoCredentials().toJsonObject();
            LOGGER.trace("Retrieved mongo credentials:\n{}", mongoCredentials.encodePrettily());
            return mongoCredentials;
        }).thenAccept(mongoCredentials -> {
            LOGGER.trace("Sending response with status code '{}'", STATUS_CODE_OK);
            sendResponse(routingContext, mongoCredentials, STATUS_CODE_OK);
        }).exceptionally(ex -> {
            LOGGER.trace("Sending response with status code '{}'", STATUS_CODE_INTERNAL_SERVER_ERROR);
            sendResponse(routingContext, new JsonObject().put("error", ex.getMessage()), STATUS_CODE_INTERNAL_SERVER_ERROR);
            return null;
        });
    }
    
    /**
     * Get the SQL credentials for the user service
     * @param routingContext the routing context
     */
    private void getUserServiceSqlConfigHandler(RoutingContext routingContext) {
        this.circuitBreaker.execute(() -> {
            LOGGER.trace("Received GET request for sql config");
            JsonObject sqlCredentials = this.service.getUserServiceSqlCredentials().toJsonObject();
            LOGGER.trace("Retrieved SQL credentials: '{}'", sqlCredentials.encodePrettily());
            return sqlCredentials;
        }).thenAccept(sqlCredentials -> {
            LOGGER.trace("Sending response with status code '{}'", STATUS_CODE_OK);
            sendResponse(routingContext, sqlCredentials, STATUS_CODE_OK);
        }).exceptionally(ex -> {
            LOGGER.trace("Sending response with status code '{}'", STATUS_CODE_INTERNAL_SERVER_ERROR);
            sendResponse(routingContext, new JsonObject().put("error", ex.getMessage()), STATUS_CODE_INTERNAL_SERVER_ERROR);
            return null;
        });
    }
    
    /**
     * Send a response to the client
     * @param routingContext the routing context
     * @param response the response
     * @param statusCode the status code
     */
    private static void sendResponse(RoutingContext routingContext, JsonObject response, int statusCode) {
        LOGGER.trace("Sending response with status code '{}' to client:\n{}", statusCode, response.encodePrettily());
        routingContext.response()
                .setStatusCode(statusCode)
                .putHeader("content-type", "application/json")
                .end(response.encode());
    }
}
