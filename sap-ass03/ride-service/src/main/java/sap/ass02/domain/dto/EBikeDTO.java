package sap.ass02.domain.dto;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.property.Jsonifyable;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.ValueObject;

public record EBikeDTO(String id, EBikeStateDTO state, P2dDTO location, V2dDTO direction, double speed, int batteryLevel) implements Jsonifyable, ValueObject {
    public enum EBikeStateDTO {
        AVAILABLE, IN_USE, MAINTENANCE
    }
    
    /**
     * @return
     */
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    /**
     * Convert the electric bike to a JSON object
     * @return the JSON object representing the electric bike
     */
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put(JsonFieldKey.EBIKE_ID_KEY, this.id)
                .put(JsonFieldKey.EBIKE_STATE_KEY, this.state)
                .put(JsonFieldKey.EBIKE_X_LOCATION_KEY, this.location.x())
                .put(JsonFieldKey.EBIKE_Y_LOCATION_KEY, this.location.y())
                .put(JsonFieldKey.EBIKE_X_DIRECTION_KEY, this.direction.x())
                .put(JsonFieldKey.EBIKE_Y_DIRECTION_KEY, this.direction.y())
                .put(JsonFieldKey.EBIKE_SPEED_KEY, this.speed)
                .put(JsonFieldKey.EBIKE_BATTERY_KEY, this.batteryLevel);
    }
    
    /**
     * Create an electric bike from a JSON object
     * @param json the JSON object representing the electric bike
     * @return the electric bike
     */
    public static EBikeDTO fromJson(JsonObject json) {
        return new EBikeDTO(
                json.getString(JsonFieldKey.EBIKE_ID_KEY),
                EBikeStateDTO.valueOf(json.getString(JsonFieldKey.EBIKE_STATE_KEY)),
                new P2dDTO(json.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), json.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY)),
                new V2dDTO(json.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), json.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY)),
                json.getDouble(JsonFieldKey.EBIKE_SPEED_KEY),
                json.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY)
        );
    }
}
