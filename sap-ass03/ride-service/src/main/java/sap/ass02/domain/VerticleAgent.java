package sap.ass02.domain;

import io.vertx.core.Verticle;

public interface VerticleAgent extends Verticle {
    void start();
    void stop();
    
    void startToAutonomouslyReachNearestStation();
    
    void startToAutonomouslyReachUser();
}
