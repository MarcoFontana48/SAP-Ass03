package sap.ass02.domain.property;

import io.vertx.core.json.JsonObject;

/**
 * Interface to convert an object to a JSON object
 */
public interface Jsonifyable {
    JsonObject toJsonObject();
}
