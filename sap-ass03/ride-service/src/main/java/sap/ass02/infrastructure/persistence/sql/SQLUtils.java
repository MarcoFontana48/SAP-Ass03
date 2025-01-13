package sap.ass02.infrastructure.persistence.sql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility class for SQL operations.
 */
public final class SQLUtils {
    private static final Logger LOGGER = LogManager.getLogger(SQLUtils.class);
    
    /**
     * Connects to a MySQL database.
     * @param host The host.
     * @param port The port.
     * @param database The database.
     * @param username The username.
     * @param password The password.
     * @return The connection.
     */
    public static Connection mySQLConnection(final String host, final String port, final String database, final String username, final String password) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        LOGGER.trace("Attempting to connect to database with URL: {}", url);
        Connection conn;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        LOGGER.trace("Connection established successfully");
        return conn;

    }
    
    /**
     * Prepares a statement.
     * @param connection The connection.
     * @param SQLStatement The SQL statement.
     * @param params The parameters.
     * @return The prepared statement.
     * @throws SQLException If an SQL exception occurs.
     */
    public static PreparedStatement prepareStatement(final Connection connection, final String SQLStatement, final Object... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQLStatement);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement;
    }
}
