package sap.ass02.domain.dto;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.property.Jsonifyable;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.ValueObject;

/**
 * Data transfer object for users.
 */
public record UserDTO(String id, int credit, double xLocation, double yLocation) implements Jsonifyable, ValueObject {
    public static UserDTO fromJson(JsonObject jsonUser) {
        return new UserDTO(
                jsonUser.getString(JsonFieldKey.USER_ID_KEY),
                jsonUser.getInteger(JsonFieldKey.USER_CREDIT_KEY),
                jsonUser.getDouble(JsonFieldKey.USER_X_LOCATION_KEY),
                jsonUser.getDouble(JsonFieldKey.USER_Y_LOCATION_KEY)
        );
    }
    
    /**
     * Converts the object to a JSON string.
     * @return the JSON string
     */
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    /**
     * Converts the object to a JSON object.
     * @return the JSON object
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject
                .put(JsonFieldKey.USER_ID_KEY, this.id)
                .put(JsonFieldKey.USER_CREDIT_KEY, this.credit)
                .put(JsonFieldKey.USER_X_LOCATION_KEY, this.xLocation)
                .put(JsonFieldKey.USER_Y_LOCATION_KEY, this.yLocation);
        return jsonObject;
    }
}
