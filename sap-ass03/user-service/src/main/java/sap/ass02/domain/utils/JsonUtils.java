package sap.ass02.domain.utils;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.User;

/**
 * Utility class for JSON operations.
 */
public final class JsonUtils {
    public static JsonObject fromUserToJsonObject(User user) {
        return new JsonObject()
                .put(JsonFieldKey.USER_ID_KEY, user.getId())
                .put(JsonFieldKey.USER_CREDIT_KEY, user.getCredit());
    }
    
    public static User fromJsonStringToUser(String userJsonString) {
        JsonObject userJson = new JsonObject(userJsonString);
        User user = new User(userJson.getString(JsonFieldKey.USER_ID_KEY));
        user.rechargeCredit(userJson.getInteger(JsonFieldKey.USER_CREDIT_KEY));
        return user;
    }
}
