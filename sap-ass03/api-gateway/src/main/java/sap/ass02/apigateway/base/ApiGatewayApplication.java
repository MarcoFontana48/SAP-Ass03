package sap.ass02.apigateway.base;

import io.vertx.core.Vertx;

/**
 * Main class for the API Gateway application
 */
public final class ApiGatewayApplication {
    
    /**
     * Main method for the API Gateway application
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ApiGatewayVerticle());
    }
    
}
