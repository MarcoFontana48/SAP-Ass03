package sap.ass02;

import io.vertx.core.Vertx;
import sap.ass02.infrastructure.RESTConfigurationServerControllerVerticle;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RESTConfigurationServerControllerVerticle());
    }
}