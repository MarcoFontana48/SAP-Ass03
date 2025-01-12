package sap.ass02.domain;

import io.vertx.core.Verticle;
import sap.ddd.Service;

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
