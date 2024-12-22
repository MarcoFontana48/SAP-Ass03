package sap.ass02.domain.dto;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.property.Jsonifyable;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.ValueObject;

public record UserDTO(String id, int credit) implements Jsonifyable, ValueObject {
    /**
     * @return
     */
    @Override
    public String toJsonString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(JsonFieldKey.USER_ID_KEY, this.id)
                .put(JsonFieldKey.USER_CREDIT_KEY, this.credit);
        return jsonObject.encode();
    }
}
