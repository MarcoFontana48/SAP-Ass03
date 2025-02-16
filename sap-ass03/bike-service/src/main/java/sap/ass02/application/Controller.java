package sap.ass02.application;

import io.vertx.core.Verticle;

/**
 * Interface to implement controllers as verticles.
 */
public interface Controller extends Verticle {
    
    /**
     * Attaches a service to the controller
     * @param service the service
     */
    void attachService(Service service);
}
