package sap.ass02.domain.utils;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.*;
import sap.ass02.domain.EBike;

/**
 * Utility class for JSON operations.
 */
public final class JsonUtils {
    /**
     * Converts a user object to a JSON object.
     * @param user the user object
     * @return the JSON object
     */
    public static JsonObject fromUserToJsonObject(User user) {
        return new JsonObject()
                .put(JsonFieldKey.USER_ID_KEY, user.getId())
                .put(JsonFieldKey.USER_CREDIT_KEY, user.getCredit());
    }
    
    /**
     * Converts a JSON string to a user object.
     * @param userJsonString the JSON string
     * @return the user object
     */
    public static User fromJsonStringToUser(String userJsonString) {
        JsonObject userJson = new JsonObject(userJsonString);
        User user = new User(userJson.getString(JsonFieldKey.USER_ID_KEY));
        user.rechargeCredit(userJson.getInteger(JsonFieldKey.USER_CREDIT_KEY));
        return user;
    }
    
    /**
     * Converts a JSON string to an eBike object.
     * @param eBikeJsonString the JSON string
     * @return the eBike object
     */
    public static EBike fromJsonStringToEBike(String eBikeJsonString) {
        JsonObject eBikeJson = new JsonObject(eBikeJsonString);
        EBike eBike = new EBikeImpl(eBikeJson.getString(JsonFieldKey.EBIKE_ID_KEY));
        eBike.updateState(EBikeImpl.BikeState.valueOf(eBikeJson.getString(JsonFieldKey.EBIKE_STATE_KEY)));
        eBike.updateSpeed(eBikeJson.getDouble(JsonFieldKey.EBIKE_SPEED_KEY));
        eBike.updateLocation(eBikeJson.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), eBikeJson.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY));
        eBike.updateDirection(eBikeJson.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), eBikeJson.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY));
        eBike.rechargeBattery();
        eBike.decreaseBatteryLevel(100 - eBikeJson.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY));
        return eBike;
    }
    
    /**
     * Converts an eBike object to a JSON object.
     * @param eBike the eBike object
     * @return the JSON object
     */
    public static JsonObject fromEBikeToJsonObject(EBike eBike) {
        return new JsonObject()
                .put(JsonFieldKey.EBIKE_ID_KEY, eBike.getBikeId())
                .put(JsonFieldKey.EBIKE_SPEED_KEY, eBike.getSpeed())
                .put(JsonFieldKey.EBIKE_X_DIRECTION_KEY, eBike.getDirection().x())
                .put(JsonFieldKey.EBIKE_Y_DIRECTION_KEY, eBike.getDirection().y())
                .put(JsonFieldKey.EBIKE_STATE_KEY, eBike.getBikeState().toString())
                .put(JsonFieldKey.EBIKE_X_LOCATION_KEY, eBike.getLocation().getX())
                .put(JsonFieldKey.EBIKE_Y_LOCATION_KEY, eBike.getLocation().getY())
                .put(JsonFieldKey.EBIKE_BATTERY_KEY, eBike.getBatteryLevel());
    }
    
    /**
     * Converts a ride object to a JSON object.
     * @param ride the ride object
     * @return the JSON object
     */
    public static JsonObject fromRideToJsonObject(Ride ride) {
        return new JsonObject()
                .put(JsonFieldKey.RIDE_ID_KEY, ride.getId())
                .put(JsonFieldKey.RIDE_USER_ID_KEY, ride.getUser().getId())
                .put(JsonFieldKey.RIDE_EBIKE_ID_KEY, ride.getBike().getBikeId())
                .put(JsonFieldKey.RIDE_START_DATE_KEY, ride.getStartedDate().toString())
                .put(JsonFieldKey.RIDE_END_DATE_KEY, ride.getEndDate().toString())
                .put(JsonFieldKey.RIDE_ONGONING_KEY, ride.isOngoing());
    }
}
