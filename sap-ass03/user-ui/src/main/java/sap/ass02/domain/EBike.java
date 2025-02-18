package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ddd.Entity;

import java.util.Objects;

public class EBike implements Entity<EBikeDTO> {
    
    private final String id;
    private EBikeState state;
    private P2d location;
    private V2d direction;
    private double speed;
    private int batteryLevel;  /* 0..100 */
    
    public EBike(String id) {
        this.id = id;
        this.state = EBikeState.AVAILABLE;
        this.location = new P2d(0, 0);
        this.direction = new V2d(1, 0);
        this.speed = 0;
    }
    
    public String getId() {
        return this.id;
    }
    
    public EBikeState getState() {
        return this.state;
    }
    
    public void rechargeBattery() {
        this.batteryLevel = 100;
    }
    
    public int getBatteryLevel() {
        return this.batteryLevel;
    }
    
    public void decreaseBatteryLevel(int delta) {
        this.batteryLevel -= delta;
        if (this.batteryLevel < 0) {
            this.batteryLevel = 0;
            this.state = EBikeState.MAINTENANCE;
        }
    }
    
    public boolean isAvailable() {
        return this.state.equals(EBikeState.AVAILABLE);
    }
    
    public void updateState(EBikeState state) {
        this.state = state;
    }
    
    public void updateLocation(P2d newLoc) {
        this.location = newLoc;
    }
    
    public void updateLocation(double x, double y) {
        this.location = new P2d(x, y);
    }
    
    public void updateSpeed(double speed) {
        this.speed = speed;
    }
    
    public void updateDirection(V2d dir) {
        this.direction = dir;
    }
    
    public void updateDirection(double x, double y) {
        this.direction = new V2d(x, y);
    }
    
    public void updateBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    public double getSpeed() {
        return this.speed;
    }
    
    public V2d getDirection() {
        return new V2d(this.direction.x(), this.direction.y());
    }
    
    public P2d getLocation() {
        return new P2d(this.location.getX(), this.location.getY());
    }
    
    public String toString() {
        return "{ id: " + this.id + ", loc: " + this.location + ", batteryLevel: " + this.batteryLevel + ", state: " + this.state + " }";
    }
    
    public enum EBikeState {AVAILABLE, IN_USE, MOVING_TO_STATION, AT_STATION, MAINTENANCE, START_AUTONOMOUSLY_REACH_STATION, START_AUTONOMOUSLY_REACH_USER, MOVING_TO_USER, AT_USER}
    
    @Override
    public EBikeDTO toDTO() {
        return new EBikeDTO(this.id, EBikeDTO.EBikeStateDTO.valueOf(this.state.toString()), new P2dDTO(this.location.getX(), this.location.getY()), new V2dDTO(this.direction.getX(), this.direction.getY()), this.speed, this.batteryLevel);
    }
    
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        EBike eBike = (EBike) o;
        return Double.compare(this.getSpeed(), eBike.getSpeed()) == 0 && this.getBatteryLevel() == eBike.getBatteryLevel() && Objects.equals(this.getId(), eBike.getId()) && this.getState() == eBike.getState() && Objects.equals(this.getLocation(), eBike.getLocation()) && Objects.equals(this.getDirection(), eBike.getDirection());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getState(), this.getLocation(), this.getDirection(), this.getSpeed(), this.getBatteryLevel());
    }
}
