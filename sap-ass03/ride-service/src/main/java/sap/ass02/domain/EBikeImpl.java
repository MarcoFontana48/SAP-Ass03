package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.BikeStateDTO;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ass02.domain.utils.JsonFieldKey;

import java.util.Objects;

/**
 * Implementation of an electric bike.
 */
public final class EBikeImpl implements EBike {
    private final String id;
    private BikeState state;
    private P2d location;
    private V2d direction;
    private double speed;
    private int batteryLevel;
    
    /**
     * Constructor for an electric bike.
     * @param id the bike's ID
     */
    public EBikeImpl(String id) {
        this.id = id;
        this.state = BikeState.AVAILABLE;
        this.location = new P2d(0, 0);
        this.direction = new V2d(1, 0);
        this.speed = 0;
        this.batteryLevel = 1;
    }
    
    /**
     * Constructor for an electric bike.
     * @param id the bike's ID
     * @param state the bike's state
     * @param location the bike's location
     * @param direction the bike's direction
     * @param speed the bike's speed
     * @param batteryLevel the bike's battery level
     */
    public EBikeImpl(String id, BikeState state, P2d location, V2d direction, double speed, int batteryLevel) {
        this.id = id;
        this.state = state;
        this.location = location;
        this.direction = direction;
        this.speed = speed;
        if (batteryLevel < 0) {
            this.batteryLevel = 0;
        } else if (batteryLevel > 100) {
            this.batteryLevel = 100;
        } else {
            this.batteryLevel = batteryLevel;
        }
    }
    
    /**
     * Constructor for an electric bike.
     * @param asJsonObject the JSON object representing the bike
     */
    public EBikeImpl(JsonObject asJsonObject) {
        this(
            asJsonObject.getString(JsonFieldKey.EBIKE_ID_KEY),
            BikeState.valueOf(asJsonObject.getString(JsonFieldKey.EBIKE_STATE_KEY)),
            new P2d(asJsonObject.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), asJsonObject.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY)),
            new V2d(asJsonObject.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), asJsonObject.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY)),
            asJsonObject.getDouble(JsonFieldKey.EBIKE_SPEED_KEY),
            asJsonObject.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY)
        );
    }
    
    /**
     * Gets the bike's ID.
     */
    @Override
    public String getBikeId() {
        return this.id;
    }
    
    /**
     * Gets the bike's state.
     */
    @Override
    public BikeState getBikeState() {
        return this.state;
    }
    
    /**
     * Recharges the bike's battery.
     */
    @Override
    public void rechargeBattery() {
        this.batteryLevel = 100;
    }
    
    /**
     * Gets the bike's battery level.
     */
    @Override
    public int getBatteryLevel() {
        return this.batteryLevel;
    }
    
    /**
     * Decreases the bike's battery level.
     * @param delta the amount to decrease the battery level by
     */
    @Override
    public void decreaseBatteryLevel(int delta) {
        this.batteryLevel -= delta;
        if (this.batteryLevel < 0) {
            this.batteryLevel = 0;
            this.state = BikeState.MAINTENANCE;
        }
    }
    
    /**
     * Checks if the bike is available.
     */
    @Override
    public boolean isAvailable() {
        return this.state.equals(BikeState.AVAILABLE);
    }
    
    /**
     * Updates the bike's state.
     * @param state the new state
     */
    @Override
    public void updateState(BikeState state) {
        this.state = state;
    }
    
    /**
     * Updates the bike's location.
     * @param newLoc the new location
     */
    @Override
    public void updateLocation(P2d newLoc) {
        this.location = newLoc;
    }
    
    /**
     * Updates the bike's location.
     * @param x the x coordinate of the new location
     * @param y the y coordinate of the new location
     */
    @Override
    public void updateLocation(double x, double y) {
        this.location = new P2d(x, y);
    }
    
    /**
     * Updates the bike's speed.
     * @param speed the new speed
     */
    @Override
    public void updateSpeed(double speed) {
        this.speed = speed;
    }
    
    /**
     * Updates the bike's direction.
     * @param dir the new direction
     */
    @Override
    public void updateDirection(V2d dir) {
        this.direction = dir;
    }
    
    /**
     * Updates the bike's direction.
     * @param x the x coordinate of the new direction
     * @param y the y coordinate of the new direction
     */
    @Override
    public void updateDirection(double x, double y) {
        this.direction = new V2d(x, y);
    }
    
    /**
     * Updates the bike's battery level.
     * @param batteryLevel the new battery level
     */
    @Override
    public void updateBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    /**
     * Gets the bike's speed.
     */
    @Override
    public double getSpeed() {
        return this.speed;
    }
    
    /**
     * Gets the bike's direction.
     */
    @Override
    public V2d getDirection() {
        return new V2d(this.direction.x(), this.direction.y());
    }
    
    /**
     * Gets the bike's location.
     */
    @Override
    public P2d getLocation() {
        return new P2d(this.location.getX(), this.location.getY());
    }
    
    /**
     * returns a string representation of the bike.
     */
    @Override
    public String toString() {
        return "{ id: " + this.id + ", loc: " + this.location + ", batteryLevel: " + this.batteryLevel + ", state: " + this.state + " }";
    }
    
    /**
     * Converts the bike to a DTO.
     */
    @Override
    public EBikeDTO toDTO() {
        return new EBikeDTO(
                this.id,
                BikeStateDTO.valueOf(this.state.toString()),
                new P2dDTO(this.location.getX(), this.location.getY()),
                new V2dDTO(this.direction.x(), this.direction.y()),
                this.speed,
                this.batteryLevel
        );
    }
    
    /**
     * Converts the bike to a JSON object.
     */
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put(JsonFieldKey.EBIKE_ID_KEY, this.id)
                .put(JsonFieldKey.EBIKE_STATE_KEY, this.state.toString())
                .put(JsonFieldKey.EBIKE_X_LOCATION_KEY, this.location.getX())
                .put(JsonFieldKey.EBIKE_Y_LOCATION_KEY, this.location.getY())
                .put(JsonFieldKey.EBIKE_X_DIRECTION_KEY, this.direction.x())
                .put(JsonFieldKey.EBIKE_Y_DIRECTION_KEY, this.direction.y())
                .put(JsonFieldKey.EBIKE_SPEED_KEY, this.speed)
                .put(JsonFieldKey.EBIKE_BATTERY_KEY, this.batteryLevel);
    }
    
    /**
     * Converts the bike to a JSON string.
     */
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    /**
     * Checks if the bike is equal to another object.
     * @param obj the object to compare to
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        EBike eBike = (EBike) obj;
        return Double.compare(this.getSpeed(), eBike.getSpeed()) == 0 && this.getBatteryLevel() == eBike.getBatteryLevel() && Objects.equals(this.getBikeId(), eBike.getBikeId()) && this.getBikeState() == eBike.getBikeState() && Objects.equals(this.getLocation(), eBike.getLocation()) && Objects.equals(this.getDirection(), eBike.getDirection());
    }
    
    /**
     * Gets the hash code of the bike.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getBikeId(), this.getBikeState(), this.getLocation(), this.getDirection(), this.getSpeed(), this.getBatteryLevel());
    }
}
