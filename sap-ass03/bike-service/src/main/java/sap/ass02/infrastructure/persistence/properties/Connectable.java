package sap.ass02.infrastructure.persistence.properties;

/**
 * Interface for objects that can connect to a database.
 */
public interface Connectable {
    void connect(String host, String port, String database, String username, String password);
}
