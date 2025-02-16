package sap.ass02.application;

import io.vertx.core.Verticle;
import sap.ass02.application.Service;

/**
 * Interface to implement services as verticles.
 */
public interface ServiceVerticle extends Verticle, Service {
}
