package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.EBikeDTO;

/**
 * Interface for an agent electric bike.
 */
public interface ABike extends Entity<EBikeDTO>, EBike {
    /**
     * Get the id of the bike.
     * @return the id of the bike
     */
    @Override
    String getBikeId();
    
    /**
     * Get the state of the bike.
     * @return the state of the bike
     */
    @Override
    BikeState getBikeState();
    
    /**
     * Recharge the battery of the bike.
     */
    @Override
    void rechargeBattery();
    
    /**
     * Get the battery level of the bike.
     * @return the battery level of the bike
     */
    @Override
    int getBatteryLevel();
    
    /**
     * Decrease the battery level of the bike.
     * @param delta the delta to decrease the battery level
     */
    @Override
    void decreaseBatteryLevel(int delta);
    
    /**
     * Check if the bike is available.
     * @return true if the bike is available, false otherwise
     */
    @Override
    boolean isAvailable();
    
    /**
     * Updates the state of the bike.
     */
    @Override
    void updateState(BikeState state);
    
    /**
     * Updates the location of the bike.
     * @param newLoc the new location of the bike
     */
    @Override
    void updateLocation(P2d newLoc);
    
    /**
     * Updates the location of the bike.
     */
    @Override
    void updateLocation(double x, double y);
    
    /**
     * Updates the speed of the bike.
     * @param speed the new speed of the bike
     */
    @Override
    void updateSpeed(double speed);
    
    /**
     * Updates the direction of the bike.
     * @param dir the new direction of the bike
     */
    @Override
    void updateDirection(V2d dir);
    
    /**
     * Updates the direction of the bike.
     */
    @Override
    void updateDirection(double x, double y);
    
    /**
     * Updates the battery level of the bike.
     * @param batteryLevel the new battery level of the bike
     */
    @Override
    void updateBatteryLevel(int batteryLevel);
    
    /**
     * Get the speed of the bike.
     * @return the speed of the bike
     */
    @Override
    double getSpeed();
    
    /**
     * Get the direction of the bike.
     * @return the direction of the bike
     */
    @Override
    V2d getDirection();
    
    /**
     * Get the location of the bike.
     * @return the location of the bike
     */
    @Override
    P2d getLocation();
    
    /**
     * Get the location of the bike.
     * @return the location of the bike
     */
    @Override
    EBikeDTO toDTO();
    
    /**
     * Converts the bike to a JSON string.
     * @return the JSON string
     */
    @Override
    String toJsonString();
    
    /**
     * Converts the bike to a JSON object.
     * @return the JSON object
     */
    @Override
    JsonObject toJsonObject();
}
