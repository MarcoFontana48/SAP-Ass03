package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.EBikeDTO;

/**
 * Represents an e-bike.
 */
public interface EBike extends Entity<EBikeDTO> {
    String getBikeId();
    
    BikeState getBikeState();
    
    void rechargeBattery();
    
    int getBatteryLevel();
    
    void decreaseBatteryLevel(int delta);
    
    boolean isAvailable();
    
    void updateState(BikeState state);
    
    void updateLocation(P2d newLoc);
    
    void updateLocation(double x, double y);
    
    void updateSpeed(double speed);
    
    void updateDirection(V2d dir);
    
    void updateDirection(double x, double y);
    
    void updateBatteryLevel(int batteryLevel);
    
    double getSpeed();
    
    V2d getDirection();
    
    P2d getLocation();
    
    String toString();
    
    @Override
    EBikeDTO toDTO();
    
    @Override
    JsonObject toJsonObject();
    
    @Override
    String toJsonString();
    
    @Override
    boolean equals(Object obj);
    
    @Override
    int hashCode();
    
    public enum BikeState {AVAILABLE, IN_USE, MOVING_TO_STATION, AT_STATION, MAINTENANCE, START_AUTONOMOUSLY_REACH_USER, MOVING_TO_USER, AT_USER, START_AUTONOMOUSLY_REACH_STATION}
}
