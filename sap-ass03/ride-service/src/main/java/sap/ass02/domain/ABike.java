package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ddd.Entity;

public interface ABike extends Entity<EBikeDTO>, EBike {
    @Override
    String getBikeId();
    
    @Override
    BikeState getBikeState();
    
    @Override
    void rechargeBattery();
    
    @Override
    int getBatteryLevel();
    
    @Override
    void decreaseBatteryLevel(int delta);
    
    @Override
    boolean isAvailable();
    
    @Override
    void updateState(BikeState state);
    
    @Override
    void updateLocation(P2d newLoc);
    
    @Override
    void updateLocation(double x, double y);
    
    @Override
    void updateSpeed(double speed);
    
    @Override
    void updateDirection(V2d dir);
    
    @Override
    void updateDirection(double x, double y);
    
    @Override
    void updateBatteryLevel(int batteryLevel);
    
    @Override
    double getSpeed();
    
    @Override
    V2d getDirection();
    
    @Override
    P2d getLocation();
    
    @Override
    EBikeDTO toDTO();
    
    @Override
    String toJsonString();
    
    @Override
    JsonObject toJsonObject();
}
