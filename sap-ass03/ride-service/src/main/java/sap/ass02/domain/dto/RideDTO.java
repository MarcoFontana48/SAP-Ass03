package sap.ass02.domain.dto;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.property.Jsonifyable;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.ValueObject;

import java.util.Optional;

/**
 * Data transfer object for rides.
 */
public record RideDTO(java.sql.Date startedDate, Optional<java.sql.Date> endDate, UserDTO user, EBikeDTO ebike, boolean ongoing, String id) implements Jsonifyable, ValueObject {
    
    /**
     * Converts the object to a JSON object.
     * @return JSON object
     */
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put(JsonFieldKey.RIDE_START_DATE_KEY, this.startedDate.toString())
                .put(JsonFieldKey.RIDE_END_DATE_KEY, this.endDate.map(java.sql.Date::toString).orElse(null))
                .put(JsonFieldKey.RIDE_USER_ID_KEY, this.user.id())
                .put(JsonFieldKey.RIDE_EBIKE_ID_KEY, this.ebike.id())
                .put(JsonFieldKey.RIDE_ONGONING_KEY, this.ongoing)
                .put(JsonFieldKey.RIDE_ID_KEY, this.id)
                .put(JsonFieldKey.RIDE_USER_KEY, this.user.toJsonObject())
                .put(JsonFieldKey.RIDE_EBIKE_KEY, this.ebike.toJsonObject());
    }
    
    /**
     * Converts the object to a JSON string.
     * @return JSON string
     */
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    /**
     * Converts a JSON object to a ride DTO.
     * @param json the JSON object
     * @return the ride DTO
     */
    public static RideDTO fromJson(JsonObject json) {
        JsonObject userJsonObject = json.getJsonObject(JsonFieldKey.RIDE_USER_KEY);
        JsonObject ebikeJsonObject = json.getJsonObject(JsonFieldKey.RIDE_EBIKE_KEY);
        return new RideDTO(
                java.sql.Date.valueOf(json.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                Optional.ofNullable(json.getString(JsonFieldKey.RIDE_END_DATE_KEY)).map(java.sql.Date::valueOf),
                new UserDTO(userJsonObject.getString(JsonFieldKey.USER_ID_KEY), userJsonObject.getInteger(JsonFieldKey.USER_CREDIT_KEY), userJsonObject.getDouble(JsonFieldKey.USER_X_LOCATION_KEY), userJsonObject.getDouble(JsonFieldKey.USER_Y_LOCATION_KEY)),
                new EBikeDTO(ebikeJsonObject.getString(JsonFieldKey.EBIKE_ID_KEY), BikeStateDTO.valueOf(ebikeJsonObject.getString(JsonFieldKey.EBIKE_STATE_KEY)), new P2dDTO(ebikeJsonObject.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), ebikeJsonObject.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY)), new V2dDTO(ebikeJsonObject.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), ebikeJsonObject.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY)), ebikeJsonObject.getDouble(JsonFieldKey.EBIKE_SPEED_KEY), ebikeJsonObject.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY)),
                json.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                json.getString(JsonFieldKey.RIDE_ID_KEY)
        );
    }
}
