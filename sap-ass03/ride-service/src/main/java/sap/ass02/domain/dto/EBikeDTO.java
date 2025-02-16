package sap.ass02.domain.dto;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.property.Jsonifyable;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.ValueObject;

/**
 * DTO for electric bikes.
 */
public record EBikeDTO(String id, BikeStateDTO state, P2dDTO location, V2dDTO direction, double speed, int batteryLevel) implements Jsonifyable, ValueObject {
    public static EBikeDTO fromJson(JsonObject jsonEBike) {
        return new EBikeDTO(
                jsonEBike.getString(JsonFieldKey.EBIKE_ID_KEY),
                BikeStateDTO.valueOf(jsonEBike.getString(JsonFieldKey.EBIKE_STATE_KEY)),
                new P2dDTO(jsonEBike.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), jsonEBike.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY)),
                new V2dDTO(jsonEBike.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), jsonEBike.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY)),
                jsonEBike.getDouble(JsonFieldKey.EBIKE_SPEED_KEY),
                jsonEBike.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY)
        );
    }
    
    /**
     * @return
     */
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    @Override
    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(JsonFieldKey.EBIKE_ID_KEY, this.id)
                .put(JsonFieldKey.EBIKE_STATE_KEY, this.state)
                .put(JsonFieldKey.EBIKE_X_LOCATION_KEY, this.location.x())
                .put(JsonFieldKey.EBIKE_Y_LOCATION_KEY, this.location.y())
                .put(JsonFieldKey.EBIKE_X_DIRECTION_KEY, this.direction.x())
                .put(JsonFieldKey.EBIKE_Y_DIRECTION_KEY, this.direction.y())
                .put(JsonFieldKey.EBIKE_SPEED_KEY, this.speed)
                .put(JsonFieldKey.EBIKE_BATTERY_KEY, this.batteryLevel);
        return jsonObject;
    }
}
