package sap.ass02.domain;

import io.vertx.core.Verticle;
import sap.ddd.Service;

/**
 * Interface for controllers.
 */
public interface Controller extends Verticle {
   
    /**
     * Attaches a service to the controller.
     *
     * @param service the service to attach
     */
    void attachService(Service service);
}
