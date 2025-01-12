package sap.ass02.domain.property;

import io.vertx.core.json.JsonObject;

/**
 * Interface for objects that can be converted to JSON.
 */
public interface Jsonifyable {
    String toJsonString();
    JsonObject toJsonObject();
}
