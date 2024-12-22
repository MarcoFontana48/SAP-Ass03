package sap.ass02.domain.entity;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.property.Jsonifyable;
import sap.ass02.domain.utils.JsonFieldKey;

/**
 * record class for SQL credentials
 */
public record SQLCredentials(String host, String port, String database, String username, String password) implements Jsonifyable {
    /**
     * Check if the SQL credentials are valid
     */
    public SQLCredentials {
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Host cannot be null or empty");
        }
        if (port == null || port.isBlank()) {
            throw new IllegalArgumentException("Port cannot be null or empty");
        }
        if (database == null || database.isBlank()) {
            throw new IllegalArgumentException("Database cannot be null or empty");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }
    
    /**
     * Convert the SQL credentials to a JSON object
     * @return the JSON object representing the SQL credentials
     */
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put(JsonFieldKey.SQL_HOST_KEY, this.host)
                .put(JsonFieldKey.SQL_PORT_KEY, this.port)
                .put(JsonFieldKey.SQL_DATABASE_KEY, this.database)
                .put(JsonFieldKey.SQL_USERNAME_KEY, this.username)
                .put(JsonFieldKey.SQL_PASSWORD_KEY, this.password);
    }
}
