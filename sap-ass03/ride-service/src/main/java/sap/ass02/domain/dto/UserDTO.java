package sap.ass02.domain.dto;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.property.Jsonifyable;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.ValueObject;

public record UserDTO(String id, int credit) implements Jsonifyable, ValueObject {
    
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put(JsonFieldKey.USER_ID_KEY, this.id)
                .put(JsonFieldKey.USER_CREDIT_KEY, this.credit);
    }
    
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    public static UserDTO fromJson(JsonObject json) {
        return new UserDTO(
                json.getString(JsonFieldKey.USER_ID_KEY),
                json.getInteger(JsonFieldKey.USER_CREDIT_KEY)
        );
    }
}
