package sap.ass02.domain.utils;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.ABike;
import sap.ass02.domain.AbstractBike;
import sap.ass02.domain.EBike;
import sap.ass02.domain.User;

/**
 * Utility class for converting domain objects to JSON objects and vice versa.
 */
public final class JsonUtils {
    /**
     * Converts a JSON string to an EBike object.
     * @param eBikeJsonString the JSON string to convert
     * @return the EBike object
     */
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
    
    /**
     * Converts a JSON string to a User object.
     * @param eBike the JSON string to convert
     * @return the User object
     */
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
    
    /**
     * Converts a User object to a JSON object.
     * @param user the User object to convert
     * @return the JSON object
     */
    public static JsonObject fromUserToJsonObject(User user) {
        return new JsonObject()
                .put(JsonFieldKey.USER_ID_KEY, user.getId())
                .put(JsonFieldKey.USER_CREDIT_KEY, user.getCredit());
    }

    /**
     * Converts a JSON string to a User object.
     * @param userJsonString the JSON string to convert
     * @return the User object
     */
    public static User fromJsonStringToUser(String userJsonString) {
        JsonObject userJson = new JsonObject(userJsonString);
        return new User(
                userJson.getString(JsonFieldKey.USER_ID_KEY),
                userJson.getInteger(JsonFieldKey.USER_CREDIT_KEY),
                userJson.getDouble(JsonFieldKey.USER_X_LOCATION_KEY),
                userJson.getDouble(JsonFieldKey.USER_Y_LOCATION_KEY)
        );
    }
    
    public static ABike fromJsonStringToABike(String abikeJsonString) {
        JsonObject bikeJson = new JsonObject(abikeJsonString);
        ABike bike = new ABike(bikeJson.getString(JsonFieldKey.ABIKE_ID_KEY));
        bike.updateState(AbstractBike.BikeState.valueOf(bikeJson.getString(JsonFieldKey.ABIKE_STATE_KEY)));
        bike.updateSpeed(bikeJson.getDouble(JsonFieldKey.ABIKE_SPEED_KEY));
        bike.updateLocation(bikeJson.getDouble(JsonFieldKey.ABIKE_X_LOCATION_KEY), bikeJson.getDouble(JsonFieldKey.ABIKE_Y_LOCATION_KEY));
        bike.updateDirection(bikeJson.getDouble(JsonFieldKey.ABIKE_X_DIRECTION_KEY), bikeJson.getDouble(JsonFieldKey.ABIKE_Y_DIRECTION_KEY));
        bike.rechargeBattery();
        bike.decreaseBatteryLevel(100 - bikeJson.getInteger(JsonFieldKey.ABIKE_BATTERY_KEY));
        return bike;
    }
}
