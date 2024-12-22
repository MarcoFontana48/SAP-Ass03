package sap.ass02.domain.entity;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.utils.JsonFieldKey;

import static org.junit.jupiter.api.Assertions.*;

//! UNIT tests
class MongoCredentialsTest {
    private final String dbHost = "localhost";
    private final String dbPort = "27017";
    private final String dbName = "test";
    private final String dbUsername = "user";
    private final String dbPassword = "password";
    private final MongoCredentials mongoCredentials = new MongoCredentials(this.dbHost, this.dbPort, this.dbName, this.dbUsername, this.dbPassword);
    
    @Test
    void toJsonObject() {
        JsonObject expected = new JsonObject()
                .put(JsonFieldKey.MONGO_HOST_KEY, this.dbHost)
                .put(JsonFieldKey.MONGO_PORT_KEY, this.dbPort)
                .put(JsonFieldKey.MONGO_DATABASE_KEY, this.dbName)
                .put(JsonFieldKey.MONGO_USERNAME_KEY, this.dbUsername)
                .put(JsonFieldKey.MONGO_PASSWORD_KEY, this.dbPassword);
        
        assertEquals(expected, this.mongoCredentials.toJsonObject());
    }
    
    @Test
    void storesCredentialsCorrectly() {
        assertAll(
                () -> assertEquals(this.dbHost, this.mongoCredentials.mongoDbHost()),
                () -> assertEquals(this.dbPort, this.mongoCredentials.mongoDbPort()),
                () -> assertEquals(this.dbName, this.mongoCredentials.mongoDbName()),
                () -> assertEquals(this.dbUsername, this.mongoCredentials.mongoDbUser()),
                () -> assertEquals(this.dbPassword, this.mongoCredentials.mongoDbPassword())
        );
    }
    
    @Test
    void throwsExceptionIfAnyCredentialIsWronglyFormatted() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new MongoCredentials(null, this.dbPort, this.dbName, this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MongoCredentials("", this.dbPort, this.dbName, this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MongoCredentials(this.dbHost, null, this.dbName, this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MongoCredentials(this.dbHost, "", this.dbName, this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MongoCredentials(this.dbHost, this.dbPort, null, this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MongoCredentials(this.dbHost, this.dbPort, "", this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MongoCredentials(this.dbHost, this.dbPort, this.dbName, null, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MongoCredentials(this.dbHost, this.dbPort, this.dbName, "", this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MongoCredentials(this.dbHost, this.dbPort, this.dbName, this.dbUsername, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MongoCredentials(this.dbHost, this.dbPort, this.dbName, this.dbUsername, ""))
        );
    }
}