package sap.ass02.infrastructure.persistence.sql;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.BikeStateDTO;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ass02.infrastructure.EndpointPath;
import sap.ass02.infrastructure.persistence.AbstractVerticleRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import static sap.ass02.infrastructure.persistence.sql.SQLQueries.SELECT_ALL_EBIKES;
import static sap.ass02.infrastructure.persistence.sql.SQLQueries.SELECT_EBIKE_BY_ID;
import static sap.ass02.infrastructure.persistence.sql.SQLUpdates.INSERT_EBIKE;
import static sap.ass02.infrastructure.persistence.sql.SQLUpdates.UPDATE_EBIKE;
import static sap.ass02.infrastructure.persistence.sql.SQLUtils.mySQLConnection;
import static sap.ass02.infrastructure.persistence.sql.SQLUtils.prepareStatement;

public abstract class AbstractSQLRepositoryAdapter extends AbstractVerticleRepository implements Connectable {
    private static final Logger LOGGER = LogManager.getLogger(AbstractSQLRepositoryAdapter.class);
    private Connection connection;
    private WebClient webClient;
    
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
    
    @Override
    public void connect(String host, String port, String database, String username, String password) {
        LOGGER.trace("Connecting to MySQL database with arguments: host: {}, port: {}, dbName: {}, dbUsername: {}, dbPassword: {}", host, port, database, username, password);
        this.connection = mySQLConnection(host, port, database, username, password);
    }
    
    @Override
    public boolean insertEbike(EBikeDTO eBike) {
        LOGGER.trace("Preparing statement to insert ebike '{}' to SQL database", eBike);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                INSERT_EBIKE,
                eBike.id(),
                eBike.state().toString(),
                eBike.location().x(),
                eBike.location().y(),
                eBike.direction().x(),
                eBike.direction().y(),
                eBike.speed(),
                eBike.batteryLevel())) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to insert ebike '{}' to SQL database", eBike, e);
            throw new RuntimeException(e);
        }
        return true;
    }
    
    @Override
    public boolean updateEBike(EBikeDTO ebike) {
        LOGGER.trace("Preparing statement to update ebike: '{}' in SQL database", ebike);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                UPDATE_EBIKE,
                ebike.state().toString(),
                ebike.location().x(),
                ebike.location().y(),
                ebike.direction().x(),
                ebike.direction().y(),
                ebike.speed(),
                ebike.batteryLevel(),
                ebike.id())) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    
    @Override
    public Optional<EBikeDTO> getEbikeById(final String ebikeId) {
        LOGGER.trace("Preparing statement to retrieve ebike with id: '{}' from SQL database", ebikeId);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                SELECT_EBIKE_BY_ID,
                ebikeId)) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            ResultSet resultSet = statement.executeQuery();
            LOGGER.trace("Extracting ebike from statement's result set...");
            if (resultSet.next()) {
                LOGGER.trace("Ebike found in result set");
                EBikeDTO retrievedEbike = new EBikeDTO(
                        resultSet.getString(SQLColumnNames.TABLE_EBIKE_COLUMN_EBIKE_ID),
                        BikeStateDTO.valueOf(resultSet.getString(SQLColumnNames.TABLE_EBIKE_COLUMN_STATE)),
                        new P2dDTO(resultSet.getDouble(SQLColumnNames.TABLE_EBIKE_COLUMN_X_LOCATION), resultSet.getDouble(SQLColumnNames.TABLE_EBIKE_COLUMN_Y_LOCATION)),
                        new V2dDTO(resultSet.getDouble(SQLColumnNames.TABLE_EBIKE_COLUMN_X_DIRECTION), resultSet.getDouble(SQLColumnNames.TABLE_EBIKE_COLUMN_Y_DIRECTION)),
                        resultSet.getDouble(SQLColumnNames.TABLE_EBIKE_COLUMN_SPEED),
                        resultSet.getInt(SQLColumnNames.TABLE_EBIKE_COLUMN_BATTERY_LEVEL));
                LOGGER.trace("Extracted ebike: '{}'", retrievedEbike);
                return Optional.of(retrievedEbike);
            } else {
                LOGGER.trace("Ebike not found in result set");
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Iterable<EBikeDTO> getAllEBikes() {
        LOGGER.trace("Preparing statement to retrieve all ebikes from SQL database");
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                SELECT_ALL_EBIKES)) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<EBikeDTO> retrievedUsersArrayList = new ArrayList<>();
            while (resultSet.next()) {
                LOGGER.trace("Extracting ebike from result set: '{}'", resultSet);
                EBikeDTO retrievedEbike = new EBikeDTO(
                        resultSet.getString(SQLColumnNames.TABLE_EBIKE_COLUMN_EBIKE_ID),
                        BikeStateDTO.valueOf(resultSet.getString(SQLColumnNames.TABLE_EBIKE_COLUMN_STATE)),
                        new P2dDTO(resultSet.getDouble(SQLColumnNames.TABLE_EBIKE_COLUMN_X_LOCATION), resultSet.getDouble(SQLColumnNames.TABLE_EBIKE_COLUMN_Y_LOCATION)),
                        new V2dDTO(resultSet.getDouble(SQLColumnNames.TABLE_EBIKE_COLUMN_X_DIRECTION), resultSet.getDouble(SQLColumnNames.TABLE_EBIKE_COLUMN_Y_DIRECTION)),
                        resultSet.getDouble(SQLColumnNames.TABLE_EBIKE_COLUMN_SPEED),
                        resultSet.getInt(SQLColumnNames.TABLE_EBIKE_COLUMN_BATTERY_LEVEL));
                LOGGER.trace("Extracted ebike: '{}'", retrievedEbike);
                retrievedUsersArrayList.add(retrievedEbike);
                LOGGER.trace("Added ebike to list: '{}'", retrievedEbike);
            }
            LOGGER.trace("Extracted ebikes: '{}'", retrievedUsersArrayList);
            return retrievedUsersArrayList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
