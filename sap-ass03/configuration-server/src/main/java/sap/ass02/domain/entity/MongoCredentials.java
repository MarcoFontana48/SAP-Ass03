package sap.ass02.domain.entity;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.property.Jsonifyable;
import sap.ass02.domain.utils.JsonFieldKey;

/**
 * record class for MongoDB credentials
 */
public record MongoCredentials(String mongoDbHost, String mongoDbPort, String mongoDbName, String mongoDbUser, String mongoDbPassword) implements Jsonifyable {
    /**
     * Check if the MongoDB credentials are valid
     */
    public MongoCredentials {
        if (mongoDbHost == null || mongoDbHost.isBlank()) {
            throw new IllegalArgumentException("MongoDB host cannot be null or empty");
        }
        if (mongoDbPort == null || mongoDbPort.isBlank()) {
            throw new IllegalArgumentException("MongoDB port cannot be null or empty");
        }
        if (mongoDbName == null || mongoDbName.isBlank()) {
            throw new IllegalArgumentException("MongoDB name cannot be null or empty");
        }
        if (mongoDbUser == null || mongoDbUser.isBlank()) {
            throw new IllegalArgumentException("MongoDB user cannot be null or empty");
        }
        if (mongoDbPassword == null || mongoDbPassword.isBlank()) {
            throw new IllegalArgumentException("MongoDB password cannot be null or empty");
        }
    }
    
    /**
     * Convert the MongoDB credentials to a JSON object
     * @return the JSON object representing the MongoDB credentials
     */
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put(JsonFieldKey.MONGO_HOST_KEY, this.mongoDbHost)
                .put(JsonFieldKey.MONGO_PORT_KEY, this.mongoDbPort)
                .put(JsonFieldKey.MONGO_DATABASE_KEY, this.mongoDbName)
                .put(JsonFieldKey.MONGO_USERNAME_KEY, this.mongoDbUser)
                .put(JsonFieldKey.MONGO_PASSWORD_KEY, this.mongoDbPassword);
    }
}
