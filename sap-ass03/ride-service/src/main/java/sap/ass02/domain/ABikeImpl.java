package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.EBikeDTO;

public final class ABikeImpl implements ABike {
    private final EBike ebike;
    
    public ABikeImpl(EBike ebike) {
        this.ebike = ebike;
    }
    
    @Override
    public String getBikeId() {
        return this.ebike.getBikeId();
    }
    
    @Override
    public BikeState getBikeState() {
        return this.ebike.getBikeState();
    }
    
    @Override
    public void rechargeBattery() {
        this.ebike.rechargeBattery();
    }
    
    @Override
    public int getBatteryLevel() {
        return this.ebike.getBatteryLevel();
    }
    
    @Override
    public void decreaseBatteryLevel(int delta) {
        this.ebike.decreaseBatteryLevel(delta);
    }
    
    @Override
    public boolean isAvailable() {
        return this.ebike.isAvailable();
    }
    
    @Override
    public void updateState(BikeState state) {
        this.ebike.updateState(state);
    }
    
    @Override
    public void updateLocation(P2d newLoc) {
        this.ebike.updateLocation(newLoc);
    }
    
    @Override
    public void updateLocation(double x, double y) {
        this.ebike.updateLocation(x, y);
    }
    
    @Override
    public void updateSpeed(double speed) {
        this.ebike.updateSpeed(speed);
    }
    
    @Override
    public void updateDirection(V2d dir) {
        this.ebike.updateDirection(dir);
    }
    
    @Override
    public void updateDirection(double x, double y) {
        this.ebike.updateDirection(x, y);
    }
    
    @Override
    public void updateBatteryLevel(int batteryLevel) {
        this.ebike.updateBatteryLevel(batteryLevel);
    }
    
    @Override
    public double getSpeed() {
        return this.ebike.getSpeed();
    }
    
    @Override
    public V2d getDirection() {
        return this.ebike.getDirection();
    }
    
    @Override
    public P2d getLocation() {
        return this.ebike.getLocation();
    }
    
    @Override
    public EBikeDTO toDTO() {
        return this.ebike.toDTO();
    }
    
    @Override
    public String toJsonString() {
        return this.ebike.toJsonString();
    }
    
    @Override
    public JsonObject toJsonObject() {
        return this.ebike.toJsonObject();
    }
}
