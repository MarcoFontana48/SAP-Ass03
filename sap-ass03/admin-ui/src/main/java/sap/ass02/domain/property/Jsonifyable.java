package sap.ass02.domain.property;

import io.vertx.core.json.JsonObject;

/**
 * Interface for classes that can be converted to JSON strings
 */
public interface Jsonifyable {
    String toJsonString();
    
    JsonObject toJsonObject();
}
