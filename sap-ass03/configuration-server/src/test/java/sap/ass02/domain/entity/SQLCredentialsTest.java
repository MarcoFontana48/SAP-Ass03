package sap.ass02.domain.entity;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.utils.JsonFieldKey;

import static org.junit.jupiter.api.Assertions.*;

//! UNIT tests
class SQLCredentialsTest {
    private final String dbHost = "localhost";
    private final String dbPort = "3306";
    private final String dbName = "test";
    private final String dbUsername = "user";
    private final String dbPassword = "password";
    private final SQLCredentials sqlCredentials = new SQLCredentials(this.dbHost, this.dbPort, this.dbName, this.dbUsername, this.dbPassword);
    
    @Test
    void toJsonObject() {
        JsonObject expected = new JsonObject()
                .put(JsonFieldKey.SQL_HOST_KEY, this.dbHost)
                .put(JsonFieldKey.SQL_PORT_KEY, this.dbPort)
                .put(JsonFieldKey.SQL_DATABASE_KEY, this.dbName)
                .put(JsonFieldKey.SQL_USERNAME_KEY, this.dbUsername)
                .put(JsonFieldKey.SQL_PASSWORD_KEY, this.dbPassword);
        
        assertEquals(expected, this.sqlCredentials.toJsonObject());
    }
    
    @Test
    void storesAllCredentialsCorrectly() {
        assertAll(
                () -> assertEquals(this.dbHost, this.sqlCredentials.host()),
                () -> assertEquals(this.dbPort, this.sqlCredentials.port()),
                () -> assertEquals(this.dbName, this.sqlCredentials.database()),
                () -> assertEquals(this.dbUsername, this.sqlCredentials.username()),
                () -> assertEquals(this.dbPassword, this.sqlCredentials.password())
        );
    }
    
    @Test
    void throwsExceptionIfAnyCredentialIsWronglyFormatted() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new SQLCredentials(null, this.dbPort, this.dbName, this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new SQLCredentials("", this.dbPort, this.dbName, this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new SQLCredentials(this.dbHost, null, this.dbName, this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new SQLCredentials(this.dbHost, "", this.dbName, this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new SQLCredentials(this.dbHost, this.dbPort, null, this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new SQLCredentials(this.dbHost, this.dbPort, "", this.dbUsername, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new SQLCredentials(this.dbHost, this.dbPort, this.dbName, null, this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new SQLCredentials(this.dbHost, this.dbPort, this.dbName, "", this.dbPassword)),
                () -> assertThrows(IllegalArgumentException.class, () -> new SQLCredentials(this.dbHost, this.dbPort, this.dbName, this.dbUsername, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new SQLCredentials(this.dbHost, this.dbPort, this.dbName, this.dbUsername, ""))
        );
    }
}