package sap.ass02.domain.property;

import io.vertx.core.json.JsonObject;

public interface Jsonifyable {
    String toJsonString();
    JsonObject toJsonObject();
}
