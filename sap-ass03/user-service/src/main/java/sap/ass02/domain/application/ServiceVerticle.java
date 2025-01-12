package sap.ass02.domain.application;

import io.vertx.core.Verticle;
import sap.ddd.Service;

/**
 * Interface to implement services as verticles.
 */
public interface ServiceVerticle extends Verticle, Service {
}
