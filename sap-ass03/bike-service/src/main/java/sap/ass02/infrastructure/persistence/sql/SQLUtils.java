package sap.ass02.infrastructure.persistence.sql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility class for SQL operations
 */
public final class SQLUtils {
    private static final Logger LOGGER = LogManager.getLogger(SQLUtils.class);
    
    /**
     * Connects to a MySQL database
     * @param host the host of the database
     * @param port the port of the database
     * @param database the name of the database
     * @param username the username to connect to the database
     * @param password the password to connect to the database
     * @return the connection to the database
     */
    public static Connection mySQLConnection(final String host, final String port, final String database, final String username, final String password) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        LOGGER.trace("Attempting to connect to database with URL: {}", url);
        Connection conn;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            LOGGER.error("Failed to connect to the database", e);
            throw new RuntimeException(e);
        }
        LOGGER.trace("Connection established successfully");
        return conn;

    }
    
    /**
     * Prepares a statement with the given parameters
     * @param connection the connection to the database
     * @param SQLStatement the SQL statement to prepare
     * @param params the parameters to set in the statement
     * @return the prepared statement
     * @throws SQLException if the statement could not be prepared
     */
    public static PreparedStatement prepareStatement(final Connection connection, final String SQLStatement, final Object... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQLStatement);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement;
    }
}
