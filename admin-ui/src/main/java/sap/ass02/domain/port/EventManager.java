package sap.ass02.domain.port;

import io.vertx.core.Verticle;

/**
 * Event Manager Interface for monitoring events
 */
public interface EventManager extends Verticle  {
    void startMonitoring();
}
