package sap.ass02.domain.property;

import io.vertx.core.json.JsonObject;

/**
 * Interface for JSON-serializable objects.
 */
public interface Jsonifyable {
    String toJsonString();
    JsonObject toJsonObject();
}
