package sap.ass02.apigateway.base;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The API gateway verticle.
 */
public final class ApiGatewayVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LogManager.getLogger(ApiGatewayVerticle.class);
    private static final int HTTP_PORT = 8080;
    
    /**
     * Starts the API gateway verticle.
     */
    @Override
    public void start() {
        WebClient client = WebClient.create(this.vertx, new WebClientOptions().setDefaultPort(HTTP_PORT));
        
        Router router = Router.router(this.vertx);
        router.route().handler(BodyHandler.create());
        
        // Route for ebike-service
        this.rerouteTo(router, "/app/ebike/*", "ebike-service", client);
        
        // Route for user-service
        this.rerouteTo(router, "/app/user/*", "user-service", client);
        
        // Route for ride-service
        this.rerouteTo(router, "/app/ride/*", "ride-service", client);
        
        // WebSocket route
        router.route("/wsc/*").handler(ctx -> {
            String path = ctx.request().uri();
            LOGGER.trace("Handling WebSocket connection with path: '{}'", path);
            String targetHost = this.determineTargetHost(path);
            LOGGER.trace("Determined target host: '{}'", targetHost);
            this.proxyWebSocket(ctx, targetHost);
        });
        
        // health check
        router.get("/health").handler(ctx -> {
            LOGGER.info("Received GET request for health check");
            JsonObject response = new JsonObject().put("status", "ok");
            sendResponse(ctx, response, 200);
        });
        
        this.vertx.createHttpServer().requestHandler(router).listen(HTTP_PORT);
    }
    
    /**
     * Stops the API gateway verticle.
     */
    private void rerouteTo(Router router, String endpoint, String host, WebClient client) {
        router.route(endpoint).handler(ctx -> {
            String path = ctx.request().uri();
            LOGGER.info("Redirecting request with uri: '{}' to '{}'", path, host);
            client.request(ctx.request().method(), HTTP_PORT, host, path).sendBuffer(ctx.getBody(), ar -> {
                if (ar.succeeded()) {
                    ctx.response().setStatusCode(ar.result().statusCode())
                            .putHeader("Content-Type", ar.result().getHeader("Content-Type"))
                            .end(ar.result().body());
                } else {
                    ctx.fail(ar.cause());
                }
            });
        });
    }
    
    /**
     * Determines the target host.
     */
    private String determineTargetHost(String path) {
        if (path.startsWith("/wsc/ebike/")) {
            return "ebike-service";
        } else if (path.startsWith("/wsc/user/")) {
            return "user-service";
        } else {
            return null;
        }
    }
    
    /**
     * Proxies the WebSocket.
     */
    private void proxyWebSocket(RoutingContext ctx, String targetHost) {
        if (targetHost == null) {
            ctx.fail(404);
            return;
        }
        
        HttpServerRequest req = ctx.request();
        req.toWebSocket(ar -> {
            LOGGER.trace("Handling WebSocket connection from client");
            if (ar.succeeded()) {
                LOGGER.trace("WebSocket connection from client succeeded");
                ServerWebSocket clientWebSocket = ar.result();
                HttpClient client = this.vertx.createHttpClient();
                LOGGER.trace("Handling WebSocket connection to target host '{}'", targetHost);
                client.webSocket(HTTP_PORT, targetHost, req.uri(), clientAr -> {
                    if (clientAr.succeeded()) {
                        LOGGER.trace("WebSocket connection to target host '{}' succeeded", targetHost);
                        WebSocket targetWebSocket = clientAr.result();
                        clientWebSocket.handler(targetWebSocket::writeBinaryMessage);
                        targetWebSocket.handler(clientWebSocket::writeBinaryMessage);
                        clientWebSocket.closeHandler(v -> targetWebSocket.close());
                        targetWebSocket.closeHandler(v -> clientWebSocket.close());
                    } else {
                        LOGGER.trace("WebSocket connection to target host '{}' failed", targetHost);
                        ctx.fail(clientAr.cause());
                    }
                });
            } else {
                LOGGER.error("WebSocket connection from client failed: ", ar.cause());
                ctx.fail(ar.cause());
            }
        });
    }
    
    /**
     * Sends a response to the client.
     */
    private static void sendResponse(RoutingContext routingContext, JsonObject response, int statusCode) {
        LOGGER.trace("Sending response with status code '{}' to client:\n{}", statusCode, response.encodePrettily());
        routingContext.response()
                .setStatusCode(statusCode)
                .putHeader("content-type", "application/json")
                .end(response.encode());
    }
}
