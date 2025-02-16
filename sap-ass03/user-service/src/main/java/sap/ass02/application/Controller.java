package sap.ass02.application;

import io.vertx.core.Verticle;

/**
 * Interface for controllers.
 */
public interface Controller extends Verticle {
    
    /**
     * Attaches a service to the controller.
     * @param service the service to attach
     */
    void attachService(Service service);
}
