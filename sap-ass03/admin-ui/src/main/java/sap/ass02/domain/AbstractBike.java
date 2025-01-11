package sap.ass02.domain;

import io.vertx.core.json.JsonObject;

public abstract class AbstractBike {
    protected final String id;
    protected BikeState state;
    protected P2d location;
    protected V2d direction;
    protected double speed;
    protected int batteryLevel;
    
    public AbstractBike(String id) {
        this.id = id;
        this.state = BikeState.AVAILABLE;
        this.location = new P2d(0, 0);
        this.direction = new V2d(1, 0);
        this.speed = 0;
        this.batteryLevel = 1;
    }
    
    public AbstractBike(String id, BikeState state, P2d location, V2d direction, double speed, int batteryLevel) {
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
    
    public abstract JsonObject toJsonObject();
    
    public enum BikeState {AVAILABLE, IN_USE, MOVING_TO_STATION, AT_STATION, MAINTENANCE, START_AUTONOMOUSLY_REACH_STATION}
    
    public String getId() {
        return this.id;
    }
    
    public BikeState getState() {
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
            this.state = BikeState.MAINTENANCE;
        }
    }
    
    public boolean isAvailable() {
        return this.state.equals(BikeState.AVAILABLE);
    }
    
    public void updateState(BikeState state) {
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
    
}
