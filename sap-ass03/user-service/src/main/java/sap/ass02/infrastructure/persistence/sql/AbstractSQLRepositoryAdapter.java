package sap.ass02.infrastructure.persistence.sql;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.UserDTO;
import sap.ass02.infrastructure.EndpointPath;
import sap.ass02.infrastructure.persistence.properties.Connectable;
import sap.ddd.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import static sap.ass02.infrastructure.persistence.sql.SQLQueries.SELECT_ALL_USERS;
import static sap.ass02.infrastructure.persistence.sql.SQLQueries.SELECT_USER_BY_ID;
import static sap.ass02.infrastructure.persistence.sql.SQLUpdates.INSERT_USER;
import static sap.ass02.infrastructure.persistence.sql.SQLUpdates.SET_CREDITS;
import static sap.ass02.infrastructure.persistence.sql.SQLUtils.mySQLConnection;
import static sap.ass02.infrastructure.persistence.sql.SQLUtils.prepareStatement;

/**
 * Abstract class for SQL repository adapters.
 */
public abstract class AbstractSQLRepositoryAdapter implements Connectable, Repository {
    private static final Logger LOGGER = LogManager.getLogger(AbstractSQLRepositoryAdapter.class);
    private Connection connection;
    private WebClient webClient;
    
    /**
     * Initializes the SQL repository adapter.
     */
    @Override
    public void init() {
        LOGGER.debug("Initializing SQLRepositoryAdapter...");
        String configServerHostName = System.getenv("CONFIG_SERVER_HOST_NAME");
        LOGGER.trace("Retrieved config server host name: '{}'", configServerHostName);
        
        Vertx vertx = Vertx.vertx();
        this.webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8080).setDefaultHost(configServerHostName));
        
        LOGGER.trace("Fetching configuration from config server...");
        this.fetchConfigurationOnEndpoint(EndpointPath.CONFIG_SQL).onSuccess(config -> {
            String host = config.getString("host");
            String port = config.getString("port");
            String dbName = config.getString("database");
            String dbUsername = config.getString("username");
            String dbPassword = config.getString("password");
            
            this.connect(host, port, dbName, dbUsername, dbPassword);
            LOGGER.debug("SQLRepositoryAdapter initialized");
        }).onFailure(err -> LOGGER.error("Failed to fetch configuration", err));
    }
    
    /**
     * Fetches the configuration on the given endpoint.
     *
     * @param endpoint the endpoint
     * @return the future
     */
    private Future<JsonObject> fetchConfigurationOnEndpoint(String endpoint) {
        return this.webClient.get(endpoint)
                .as(BodyCodec.jsonObject())
                .send()
                .map(response -> {
                    if (response.statusCode() == 200) {
                        return response.body();
                    } else {
                        throw new RuntimeException("Failed to fetch configuration: " + response.statusMessage());
                    }
                });
    }
    
    /**
     * Connects to the MySQL database.
     *
     * @param host the host
     * @param port the port
     * @param database the database
     * @param username the username
     * @param password the password
     */
    @Override
    public void connect(String host, String port, String database, String username, String password) {
        LOGGER.trace("Connecting to MySQL database with arguments: host: {}, port: {}, dbName: {}, dbUsername: {}, dbPassword: {}", host, port, database, username, password);
        this.connection = mySQLConnection(host, port, database, username, password);
    }
    
    /**
     * Inserts a user into the SQL database.
     */
    @Override
    public boolean insertUser(final UserDTO user) {
        LOGGER.trace("Preparing statement to insert user '{}' to SQL database", user);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                INSERT_USER,
                user.id(),
                user.credit(),
                user.xLocation(),
                user.yLocation())) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to insert user '{}' to SQL database", user, e);
            throw new RuntimeException(e);
        }
        return true;
    }
    
    /**
     * Updates a user's credits in the SQL database.
     */
    @Override
    public boolean updateUserCredits(String userID, int credits) {
        LOGGER.trace("Preparing statement to update user '{}' credits in SQL database", userID);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                SET_CREDITS,
                credits,
                userID)) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    
    /**
     * Retrieves a user from the SQL database by ID.
     */
    @Override
    public Optional<UserDTO> getUserById(final String userId) {
        LOGGER.trace("Preparing statement to retrieve user with id: '{}' from SQL database", userId);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                SELECT_USER_BY_ID,
                userId)) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            ResultSet resultSet = statement.executeQuery();
            LOGGER.trace("Extracting user from statement's result set...");
            if (resultSet.next()) {
                LOGGER.trace("User found in result set");
                UserDTO retrievedUser = new UserDTO(
                        resultSet.getString(SQLColumnNames.TABLE_USER_COLUMN_USER_ID),
                        resultSet.getInt(SQLColumnNames.TABLE_USER_COLUMN_CREDIT),
                        resultSet.getDouble(SQLColumnNames.TABLE_USER_COLUMN_X_LOCATION),
                        resultSet.getDouble(SQLColumnNames.TABLE_USER_COLUMN_Y_LOCATION));
                LOGGER.trace("Extracted user: '{}'", retrievedUser);
                return Optional.of(retrievedUser);
            } else {
                LOGGER.trace("User not found in result set");
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Retrieves all users from the SQL database.
     */
    @Override
    public Iterable<UserDTO> getAllUsers() {
        LOGGER.trace("Preparing statement to retrieve all users from SQL database");
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                SELECT_ALL_USERS)) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<UserDTO> retrievedUsersArrayList = new ArrayList<>();
            while (resultSet.next()) {
                LOGGER.trace("Extracting user from result set: '{}'", resultSet);
                UserDTO retrievedUser = new UserDTO(
                        resultSet.getString(SQLColumnNames.TABLE_USER_COLUMN_USER_ID),
                        resultSet.getInt(SQLColumnNames.TABLE_USER_COLUMN_CREDIT),
                        resultSet.getDouble(SQLColumnNames.TABLE_USER_COLUMN_X_LOCATION),
                        resultSet.getDouble(SQLColumnNames.TABLE_USER_COLUMN_Y_LOCATION));
                LOGGER.trace("Extracted user: '{}'", retrievedUser);
                retrievedUsersArrayList.add(retrievedUser);
                LOGGER.trace("Added user to list: '{}'", retrievedUser);
            }
            LOGGER.trace("Extracted users: '{}'", retrievedUsersArrayList);
            return retrievedUsersArrayList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
