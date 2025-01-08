package sap.ass02.domain.dto;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.property.Jsonifyable;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.ValueObject;

public record ABikeDTO(String id, BikeStateDTO state, P2dDTO location, V2dDTO direction, double speed, int batteryLevel) implements Jsonifyable, ValueObject {
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
        jsonObject.put(JsonFieldKey.ABIKE_ID_KEY, this.id)
                .put(JsonFieldKey.ABIKE_STATE_KEY, this.state)
                .put(JsonFieldKey.ABIKE_X_LOCATION_KEY, this.location.x())
                .put(JsonFieldKey.ABIKE_Y_LOCATION_KEY, this.location.y())
                .put(JsonFieldKey.ABIKE_X_DIRECTION_KEY, this.direction.x())
                .put(JsonFieldKey.ABIKE_Y_DIRECTION_KEY, this.direction.y())
                .put(JsonFieldKey.ABIKE_SPEED_KEY, this.speed)
                .put(JsonFieldKey.ABIKE_BATTERY_KEY, this.batteryLevel);
        return jsonObject;
    }
}
