package sap.ass02.domain.port;

import io.vertx.core.Verticle;

public interface EventManager extends Verticle  {
    void startMonitoring();
}
