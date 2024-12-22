package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ddd.Entity;

import java.util.Objects;

/**
 * Entity representing an electric bike.
 */
public class EBike implements Entity<EBikeDTO> {
    private final String id;
    private EBikeState state;
    private P2d location;
    private V2d direction;
    private double speed;
    private int batteryLevel;  /* 0..100 */
    
    /**
     * Creates a new electric bike with the given id.
     *
     * @param id the id of the electric bike
     */
    public EBike(String id) {
        this.id = id;
        this.state = EBikeState.AVAILABLE;
        this.location = new P2d(0, 0);
        this.direction = new V2d(1, 0);
        this.speed = 0;
    }
    
    /**
     * @return the id of the electric bike
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * @return the state of the electric bike
     */
    public EBikeState getState() {
        return this.state;
    }
    
    /**
     * Recharges the battery of the electric bike to 100%.
     */
    public void rechargeBattery() {
        this.batteryLevel = 100;
    }
    
    /**
     * @return the battery level of the electric bike
     */
    public int getBatteryLevel() {
        return this.batteryLevel;
    }
    
    /**
     * Decreases the battery level of the electric bike by the given delta.
     *
     * @param delta the amount to decrease the battery level by
     */
    public void decreaseBatteryLevel(int delta) {
        this.batteryLevel -= delta;
        if (this.batteryLevel < 0) {
            this.batteryLevel = 0;
            this.state = EBikeState.MAINTENANCE;
        }
    }
    
    /**
     * @return true if the electric bike is available, false otherwise
     */
    public boolean isAvailable() {
        return this.state.equals(EBikeState.AVAILABLE);
    }
    
    /**
     * Updated the state of the electric bike to the given state.
     * @param state the new state of the electric bike
     */
    public void updateState(EBikeState state) {
        this.state = state;
    }
    
    /**
     * Updates the location of the electric bike to the given location.
     *
     * @param newLoc the new location of the electric bike
     */
    public void updateLocation(P2d newLoc) {
        this.location = newLoc;
    }
    
    /**
     * Updates the location of the electric bike to the given location.
     *
     * @param x the new x location of the electric bike
     * @param y the new y location of the electric bike
     */
    public void updateLocation(double x, double y) {
        this.location = new P2d(x, y);
    }
    
    /**
     * Updates the speed of the electric bike to the given speed.
     *
     * @param speed the new speed of the electric bike
     */
    public void updateSpeed(double speed) {
        this.speed = speed;
    }
    
    /**
     * Updates the direction of the electric bike to the given direction.
     *
     * @param dir the new direction of the electric bike
     */
    public void updateDirection(V2d dir) {
        this.direction = dir;
    }
    
    /**
     * Updates the direction of the electric bike to the given direction.
     *
     * @param x the new x direction of the electric bike
     * @param y the new y direction of the electric bike
     */
    public void updateDirection(double x, double y) {
        this.direction = new V2d(x, y);
    }
    
    /**
     * Updates the battery level of the electric bike to the given battery level.
     *
     * @param batteryLevel the new battery level of the electric bike
     */
    public void updateBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    /**
     * @return the speed of the electric bike
     */
    public double getSpeed() {
        return this.speed;
    }
    
    /**
     * @return the direction of the electric bike
     */
    public V2d getDirection() {
        return new V2d(this.direction.x(), this.direction.y());
    }
    
    /**
     * @return the location of the electric bike
     */
    public P2d getLocation() {
        return new P2d(this.location.getX(), this.location.getY());
    }
    
    /**
     * @return a string representation of the electric bike
     */
    public String toString() {
        return "{ id: " + this.id + ", loc: " + this.location + ", batteryLevel: " + this.batteryLevel + ", state: " + this.state + " }";
    }
    
    /**
     * Enum representing the state of an electric bike.
     */
    public enum EBikeState {AVAILABLE, IN_USE, MAINTENANCE}
    
    /**
     * @return the DTO representation of the electric bike
     */
    @Override
    public EBikeDTO toDTO() {
        return new EBikeDTO(this.id, EBikeDTO.EBikeStateDTO.valueOf(this.state.toString()), new P2dDTO(this.location.getX(), this.location.getY()), new V2dDTO(this.direction.getX(), this.direction.getY()), this.speed, this.batteryLevel);
    }
    
    /**
     * @return the JSON string representation of the electric bike
     */
    @Override
    public String toJsonString() {
        JsonObject json = new JsonObject();
        json.put(JsonFieldKey.EBIKE_ID_KEY, this.id)
                .put(JsonFieldKey.EBIKE_STATE_KEY, this.state.toString())
                .put(JsonFieldKey.EBIKE_X_LOCATION_KEY, this.location.getX())
                .put(JsonFieldKey.EBIKE_Y_LOCATION_KEY, this.location.getY())
                .put(JsonFieldKey.EBIKE_X_DIRECTION_KEY, this.direction.x())
                .put(JsonFieldKey.EBIKE_Y_DIRECTION_KEY, this.direction.y())
                .put(JsonFieldKey.EBIKE_SPEED_KEY, this.speed)
                .put(JsonFieldKey.EBIKE_BATTERY_KEY, this.batteryLevel);
        return json.encode();
    }
    
    /**
     * @param o the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        EBike eBike = (EBike) o;
        return Double.compare(this.getSpeed(), eBike.getSpeed()) == 0 && this.getBatteryLevel() == eBike.getBatteryLevel() && Objects.equals(this.getId(), eBike.getId()) && this.getState() == eBike.getState() && Objects.equals(this.getLocation(), eBike.getLocation()) && Objects.equals(this.getDirection(), eBike.getDirection());
    }
    
    /**
     * @return the hash code of the electric bike
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getState(), this.getLocation(), this.getDirection(), this.getSpeed(), this.getBatteryLevel());
    }
}
