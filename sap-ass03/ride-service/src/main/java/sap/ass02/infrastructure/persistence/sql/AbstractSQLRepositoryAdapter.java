package sap.ass02.infrastructure.persistence.sql;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.*;
import sap.ass02.infrastructure.EndpointPath;
import sap.ass02.infrastructure.persistence.AbstractVerticleRepository;
import sap.ass02.infrastructure.persistence.properties.Connectable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

import static sap.ass02.infrastructure.persistence.sql.SQLUpdates.*;
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
    public void insertRide(RideDTO ride) {
        LOGGER.trace("Preparing statement to insert ride '{}' to SQL database", ride);
        
        this.insertUser(ride.user());
        this.insertEbike(ride.ebike());
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                INSERT_RIDE,
                ride.id(),
                ride.user().id(),
                ride.ebike().id(),
                ride.startedDate(),
                ride.endDate().orElse(null),
                ride.ongoing())) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void insertUser(UserDTO user) {
        LOGGER.trace("Preparing statement to insert user '{}' to SQL database", user);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                INSERT_USER,
                user.id(),
                user.credit())) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void insertEbike(EBikeDTO ebike) {
        LOGGER.trace("Preparing statement to insert ebike '{}' to SQL database", ebike);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                INSERT_EBIKE,
                ebike.id(),
                ebike.state().toString(),
                ebike.location().x(),
                ebike.location().y(),
                ebike.direction().x(),
                ebike.direction().y(),
                ebike.speed(),
                ebike.batteryLevel())) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void updateRideEnd(RideDTO ride) {
        LOGGER.trace("Preparing statement to update ride '{}' end in SQL database", ride);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                UPDATE_RIDE_END,
                ride.endDate().orElse(null),
                ride.ongoing(),
                ride.id())) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Optional<RideDTO> getRideById(String rideId) {
        LOGGER.trace("Preparing statement to retrieve ride with id: '{}' from SQL database", rideId);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                SELECT_RIDE_BY_ID,
                rideId)) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            ResultSet resultSet = statement.executeQuery();
            LOGGER.trace("Extracting ride from statement's result set...");
            if (resultSet.next()) {
                LOGGER.trace("Ride found in result set");
                UserDTO user = new UserDTO(resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_USER_ID), -1);
                EBikeDTO ebike = new EBikeDTO(resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_EBIKE_ID), EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(0, 0), 0, 0);
                Date startDate = resultSet.getDate(SQLColumnNames.TABLE_RIDE_COLUMN_START_DATE);
                Optional<Date> endDate;
                if (resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_END_DATE) == null) {
                    endDate = Optional.empty();
                } else {
                    endDate = Optional.of(resultSet.getDate(SQLColumnNames.TABLE_RIDE_COLUMN_END_DATE));
                }
                RideDTO retrievedRide = new RideDTO(
                        startDate,
                        endDate,
                        user,
                        ebike,
                        resultSet.getBoolean(SQLColumnNames.TABLE_RIDE_COLUMN_ONGOING),
                        resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_RIDE_ID)
                );
                LOGGER.trace("Extracted ride: '{}'", retrievedRide);
                return Optional.of(retrievedRide);
            } else {
                LOGGER.trace("Ride not found in result set");
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Optional<RideDTO> getRideById(String userId, String ebikeId) {
        LOGGER.trace("Preparing statement to retrieve ride with user id: '{}' and ebike id: '{}' from SQL database", userId, ebikeId);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                SELECT_RIDE_BY_USER_EBIKE_ID,
                userId,
                ebikeId)) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            ResultSet resultSet = statement.executeQuery();
            LOGGER.trace("Extracting ride from statement's result set...");
            if (resultSet.next()) {
                LOGGER.trace("Ride found in result set");
                UserDTO user = new UserDTO(userId, -1);
                EBikeDTO ebike = new EBikeDTO(ebikeId, EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(0, 0), 0, 0);
                Date startDate = resultSet.getDate(SQLColumnNames.TABLE_RIDE_COLUMN_START_DATE);
                Optional<Date> endDate;
                if (resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_END_DATE) == null) {
                    endDate = Optional.empty();
                } else {
                    endDate = Optional.of(resultSet.getDate(SQLColumnNames.TABLE_RIDE_COLUMN_END_DATE));
                }
                RideDTO retrievedRide = new RideDTO(
                        startDate,
                        endDate,
                        user,
                        ebike,
                        resultSet.getBoolean(SQLColumnNames.TABLE_RIDE_COLUMN_ONGOING),
                        resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_RIDE_ID)
                );
                LOGGER.trace("Extracted ride: '{}'", retrievedRide);
                return Optional.of(retrievedRide);
            } else {
                LOGGER.trace("Ride not found in result set");
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Optional<RideDTO> getOngoingRideById(String userId, String ebikeId) {
        LOGGER.trace("Preparing statement to retrieve ongoing ride with user id: '{}' and ebike id: '{}' from SQL database", userId, ebikeId);
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                SELECT_ONGOING_RIDE_BY_USER_EBIKE_ID,
                userId,
                ebikeId)) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            ResultSet resultSet = statement.executeQuery();
            LOGGER.trace("Extracting ride from statement's result set...");
            if (resultSet.next()) {
                LOGGER.trace("Ride found in result set");
                UserDTO user = new UserDTO(userId, -1);
                EBikeDTO ebike = new EBikeDTO(ebikeId, EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(0, 0), 0, 0);
                Date startDate = resultSet.getDate(SQLColumnNames.TABLE_RIDE_COLUMN_START_DATE);
                Optional<Date> endDate;
                if (resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_END_DATE) == null) {
                    endDate = Optional.empty();
                } else {
                    endDate = Optional.of(resultSet.getDate(SQLColumnNames.TABLE_RIDE_COLUMN_END_DATE));
                }
                RideDTO retrievedRide = new RideDTO(
                        startDate,
                        endDate,
                        user,
                        ebike,
                        resultSet.getBoolean(SQLColumnNames.TABLE_RIDE_COLUMN_ONGOING),
                        resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_RIDE_ID)
                );
                LOGGER.trace("Extracted ride: '{}'", retrievedRide);
                return Optional.of(retrievedRide);
            } else {
                LOGGER.trace("Ride not found in result set");
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Iterable<RideDTO> getAllRides() {
        LOGGER.trace("Preparing statement to retrieve all rides from SQL database");
        try (PreparedStatement statement = prepareStatement(
                this.connection,
                SELECT_ALL_RIDES)) {
            LOGGER.trace("Executing statement:\n'{}'", statement);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<RideDTO> retrievedRidesArrayList = new ArrayList<>();
            while (resultSet.next()) {
                LOGGER.trace("Extracting ride from result set: '{}'", resultSet);
                UserDTO user = new UserDTO(resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_USER_ID), -1);
                EBikeDTO ebike = new EBikeDTO(resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_EBIKE_ID), EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(0, 0), 0, 0);
                Date startDate = resultSet.getDate(SQLColumnNames.TABLE_RIDE_COLUMN_START_DATE);
                Optional<Date> endDate;
                if (resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_END_DATE) == null) {
                    endDate = Optional.empty();
                } else {
                    endDate = Optional.of(resultSet.getDate(SQLColumnNames.TABLE_RIDE_COLUMN_END_DATE));
                }
                RideDTO retrievedRide = new RideDTO(
                        startDate,
                        endDate,
                        user,
                        ebike,
                        resultSet.getBoolean(SQLColumnNames.TABLE_RIDE_COLUMN_ONGOING),
                        resultSet.getString(SQLColumnNames.TABLE_RIDE_COLUMN_RIDE_ID)
                );
                LOGGER.trace("Extracted ride: '{}'", retrievedRide);
                retrievedRidesArrayList.add(retrievedRide);
                LOGGER.trace("Added ride to list: '{}'", retrievedRide);
            }
            LOGGER.trace("Extracted rides: '{}'", retrievedRidesArrayList);
            return retrievedRidesArrayList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    //TODO
    @Override
    public Optional<EBikeDTO> getEBikeById(String ebikeId) {
        return Optional.empty();
    }
    
    @Override
    public Optional<UserDTO> getUserById(String userId) {
        return Optional.empty();
    }
    
    @Override
    public void updateEBike(EBikeDTO dto) {
    
    }
    
    @Override
    public void updateUser(UserDTO dto) {
    
    }
}
