package sap.ass02.apigateway.base;

import io.vertx.core.Vertx;

public final class ApiGatewayApplication {
    
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ApiGatewayVerticle());
    }
    
}
