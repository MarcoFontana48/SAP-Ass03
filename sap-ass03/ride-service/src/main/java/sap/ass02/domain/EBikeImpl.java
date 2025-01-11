package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.BikeStateDTO;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ass02.domain.utils.JsonFieldKey;

import java.util.Objects;

public final class EBikeImpl implements EBike {
    private final String id;
    private BikeState state;
    private P2d location;
    private V2d direction;
    private double speed;
    private int batteryLevel;
    
    public EBikeImpl(String id) {
        this.id = id;
        this.state = BikeState.AVAILABLE;
        this.location = new P2d(0, 0);
        this.direction = new V2d(1, 0);
        this.speed = 0;
        this.batteryLevel = 1;
    }
    
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
    
    @Override
    public String getBikeId() {
        return this.id;
    }
    
    @Override
    public BikeState getBikeState() {
        return this.state;
    }
    
    @Override
    public void rechargeBattery() {
        this.batteryLevel = 100;
    }
    
    @Override
    public int getBatteryLevel() {
        return this.batteryLevel;
    }
    
    @Override
    public void decreaseBatteryLevel(int delta) {
        this.batteryLevel -= delta;
        if (this.batteryLevel < 0) {
            this.batteryLevel = 0;
            this.state = BikeState.MAINTENANCE;
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.state.equals(BikeState.AVAILABLE);
    }
    
    @Override
    public void updateState(BikeState state) {
        this.state = state;
    }
    
    @Override
    public void updateLocation(P2d newLoc) {
        this.location = newLoc;
    }
    
    @Override
    public void updateLocation(double x, double y) {
        this.location = new P2d(x, y);
    }
    
    @Override
    public void updateSpeed(double speed) {
        this.speed = speed;
    }
    
    @Override
    public void updateDirection(V2d dir) {
        this.direction = dir;
    }
    
    @Override
    public void updateDirection(double x, double y) {
        this.direction = new V2d(x, y);
    }
    
    @Override
    public void updateBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    @Override
    public double getSpeed() {
        return this.speed;
    }
    
    @Override
    public V2d getDirection() {
        return new V2d(this.direction.x(), this.direction.y());
    }
    
    @Override
    public P2d getLocation() {
        return new P2d(this.location.getX(), this.location.getY());
    }
    
    @Override
    public String toString() {
        return "{ id: " + this.id + ", loc: " + this.location + ", batteryLevel: " + this.batteryLevel + ", state: " + this.state + " }";
    }
    
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
    
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        EBike eBike = (EBike) obj;
        return Double.compare(this.getSpeed(), eBike.getSpeed()) == 0 && this.getBatteryLevel() == eBike.getBatteryLevel() && Objects.equals(this.getBikeId(), eBike.getBikeId()) && this.getBikeState() == eBike.getBikeState() && Objects.equals(this.getLocation(), eBike.getLocation()) && Objects.equals(this.getDirection(), eBike.getDirection());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getBikeId(), this.getBikeState(), this.getLocation(), this.getDirection(), this.getSpeed(), this.getBatteryLevel());
    }
}
