package sap.ass02;

import io.vertx.core.Vertx;
import sap.ass02.infrastructure.RESTConfigurationServerControllerVerticle;

/**
 * Main class to start the configuration server
 */
public class Main {
    /**
     * Main method to start the configuration server
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RESTConfigurationServerControllerVerticle());
    }
}