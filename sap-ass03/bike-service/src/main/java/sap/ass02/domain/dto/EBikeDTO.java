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
