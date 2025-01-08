package sap.ass02.domain.utils;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.AbstractBike;
import sap.ass02.domain.EBike;

public final class JsonUtils {
    public static EBike fromJsonStringToEBike(String eBikeJsonString) {
        JsonObject eBikeJson = new JsonObject(eBikeJsonString);
        EBike eBike = new EBike(eBikeJson.getString(JsonFieldKey.EBIKE_ID_KEY));
        eBike.updateState(AbstractBike.BikeState.valueOf(eBikeJson.getString(JsonFieldKey.EBIKE_STATE_KEY)));
        eBike.updateSpeed(eBikeJson.getDouble(JsonFieldKey.EBIKE_SPEED_KEY));
        eBike.updateLocation(eBikeJson.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), eBikeJson.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY));
        eBike.updateDirection(eBikeJson.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), eBikeJson.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY));
        eBike.rechargeBattery();
        eBike.decreaseBatteryLevel(100 - eBikeJson.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY));
        return eBike;
    }

    public static JsonObject fromEBikeToJsonObject(EBike eBike) {
        return new JsonObject()
                .put(JsonFieldKey.EBIKE_ID_KEY, eBike.getId())
                .put(JsonFieldKey.EBIKE_SPEED_KEY, eBike.getSpeed())
                .put(JsonFieldKey.EBIKE_X_DIRECTION_KEY, eBike.getDirection().x())
                .put(JsonFieldKey.EBIKE_Y_DIRECTION_KEY, eBike.getDirection().y())
                .put(JsonFieldKey.EBIKE_STATE_KEY, eBike.getState().toString())
                .put(JsonFieldKey.EBIKE_X_LOCATION_KEY, eBike.getLocation().getX())
                .put(JsonFieldKey.EBIKE_Y_LOCATION_KEY, eBike.getLocation().getY())
                .put(JsonFieldKey.EBIKE_BATTERY_KEY, eBike.getBatteryLevel());
    }
}
