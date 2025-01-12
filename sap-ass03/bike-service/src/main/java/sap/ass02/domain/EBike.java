package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.BikeStateDTO;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.Entity;

import java.util.Objects;

/**
 * Represents an electric bike.
 */
public final class EBike extends AbstractBike implements Entity<EBikeDTO> {
    /**
     * Creates a new electric bike with the given ID.
     *
     * @param id the ID of the bike
     */
    public EBike(String id) {
        super(id);
    }
    
    /**
     * Creates a new electric bike with the given ID, state, location, direction, speed, and battery level.
     *
     * @param id           the ID of the bike
     * @param state        the state of the bike
     * @param location     the location of the bike
     * @param direction    the direction of the bike
     * @param speed        the speed of the bike
     * @param batteryLevel the battery level of the bike
     */
    public EBike(String id, BikeState state, P2d location, V2d direction, double speed, int batteryLevel) {
        super(id, state, location, direction, speed, batteryLevel);
    }
    
    /**
     * Creates a new electric bike given a JSON object that represents the it.
     *
     * @param asJsonObject the JSON object
     */
    public EBike(JsonObject asJsonObject) {
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
     * Creates a new electric bike given a DTO.
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
     * Converts this object to a JSON object.
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
     * Converts this object to a JSON string.
     */
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    /**
     * Checks if this object is equal to another object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        EBike eBike = (EBike) o;
        return Double.compare(this.getSpeed(), eBike.getSpeed()) == 0 && this.getBatteryLevel() == eBike.getBatteryLevel() && Objects.equals(this.getId(), eBike.getId()) && this.getState() == eBike.getState() && Objects.equals(this.getLocation(), eBike.getLocation()) && Objects.equals(this.getDirection(), eBike.getDirection());
    }
    
    /**
     * Returns the hash code of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getState(), this.getLocation(), this.getDirection(), this.getSpeed(), this.getBatteryLevel());
    }
}
