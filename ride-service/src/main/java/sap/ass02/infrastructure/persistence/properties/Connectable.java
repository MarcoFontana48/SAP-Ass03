package sap.ass02.infrastructure.persistence.properties;

public interface Connectable {
    void connect(String host, String port, String database, String username, String password);
}
