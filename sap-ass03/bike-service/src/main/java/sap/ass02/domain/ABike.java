package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.*;
import sap.ass02.domain.dto.ABikeDTO;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.Entity;

import java.util.Objects;

public final class ABike extends AbstractBike implements Entity<ABikeDTO> {
    final double perceptionRadius = 101;
    
    public ABike(String id) {
        super(id);
    }

    public ABike(String id, BikeState state, P2d location, V2d direction, double speed, int batteryLevel) {
        super(id, state, location, direction, speed, batteryLevel);
    }
    
    public ABike(JsonObject asJsonObject) {
        this(
                asJsonObject.getString(JsonFieldKey.ABIKE_ID_KEY),
                BikeState.valueOf(asJsonObject.getString(JsonFieldKey.ABIKE_STATE_KEY)),
                new P2d(asJsonObject.getDouble(JsonFieldKey.ABIKE_X_LOCATION_KEY), asJsonObject.getDouble(JsonFieldKey.ABIKE_Y_LOCATION_KEY)),
                new V2d(asJsonObject.getDouble(JsonFieldKey.ABIKE_X_DIRECTION_KEY), asJsonObject.getDouble(JsonFieldKey.ABIKE_Y_DIRECTION_KEY)),
                asJsonObject.getDouble(JsonFieldKey.ABIKE_SPEED_KEY),
                asJsonObject.getInteger(JsonFieldKey.ABIKE_BATTERY_KEY)
        );
    }
    
    private Station evaluateNearestStation() {
        Iterable<Station> stations = Environment.getStations();
        Station nearestStation = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Station station : stations) {
            double distance = Math.sqrt(Math.pow(this.location.getX() - station.location().getX(), 2) + Math.pow(this.location.getY() - station.location().getY(), 2));
            if (distance < minDistance && distance <= this.perceptionRadius) {
                minDistance = distance;
                nearestStation = station;
            }
        }
        
        return nearestStation;
    }
    
    @Override
    public ABikeDTO toDTO() {
        return new ABikeDTO(
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
                .put(JsonFieldKey.ABIKE_ID_KEY, this.id)
                .put(JsonFieldKey.ABIKE_STATE_KEY, this.state.toString())
                .put(JsonFieldKey.ABIKE_X_LOCATION_KEY, this.location.getX())
                .put(JsonFieldKey.ABIKE_Y_LOCATION_KEY, this.location.getY())
                .put(JsonFieldKey.ABIKE_X_DIRECTION_KEY, this.direction.x())
                .put(JsonFieldKey.ABIKE_Y_DIRECTION_KEY, this.direction.y())
                .put(JsonFieldKey.ABIKE_SPEED_KEY, this.speed)
                .put(JsonFieldKey.ABIKE_BATTERY_KEY, this.batteryLevel);
    }
    
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ABike eBike = (ABike) o;
        return Double.compare(this.getSpeed(), eBike.getSpeed()) == 0 && this.getBatteryLevel() == eBike.getBatteryLevel() && Objects.equals(this.getId(), eBike.getId()) && this.getState() == eBike.getState() && Objects.equals(this.getLocation(), eBike.getLocation()) && Objects.equals(this.getDirection(), eBike.getDirection());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getState(), this.getLocation(), this.getDirection(), this.getSpeed(), this.getBatteryLevel());
    }
}
