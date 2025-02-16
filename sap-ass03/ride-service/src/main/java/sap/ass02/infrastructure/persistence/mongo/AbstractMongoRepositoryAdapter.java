package sap.ass02.infrastructure.persistence.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import sap.ass02.domain.dto.*;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.infrastructure.EndpointPath;
import sap.ass02.infrastructure.persistence.properties.Connectable;
import sap.ass02.application.Repository;

import java.util.ArrayList;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

/**
 * Abstract class for MongoDB repository adapters.
 */
public abstract class AbstractMongoRepositoryAdapter extends AbstractVerticle implements Connectable, Repository {
    private static final Logger LOGGER = LogManager.getLogger(AbstractMongoRepositoryAdapter.class);
    private MongoCollection<Document> ridesCollection;
    private WebClient webClient;

    /**
     * Initializes the repository adapter.
     */
    @Override
    public void init() {
        LOGGER.debug("Initializing SQLRepositoryAdapter...");
        String configServerHostName = System.getenv("CONFIG_SERVER_HOST_NAME");
        LOGGER.trace("Retrieved config server host name: '{}'", configServerHostName);

        Vertx vertx = Vertx.vertx();
        this.webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8080).setDefaultHost(configServerHostName));

        LOGGER.trace("Fetching configuration from config server...");
        this.fetchConfigurationOnEndpoint(EndpointPath.CONFIG_MONGO).onSuccess(config -> {
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
     * Fetches the configuration from the given endpoint.
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
     * Connects to the MongoDB database.
     *
     * @param host     the host
     * @param port     the port
     * @param database the database
     * @param username the username
     * @param password the password
     */
    @Override
    public void connect(String host, String port, String database, String username, String password) {
        LOGGER.trace("Connecting to MongoDB database with arguments: host: {}, port: {}, dbName: {}, dbUsername: {}, dbPassword: {}", host, port, database, username, password);
        MongoClient mongoClient = MongoClients.create(String.format("mongodb://%s:%s@%s:%s", username, password, host, port));
        MongoDatabase database1 = mongoClient.getDatabase(database);
        this.ridesCollection = database1.getCollection("rides");
        LOGGER.debug("{} initialized", this.getClass().getSimpleName());
    }
    
    /**
     * Updates the ongoing status of a ride.
     * @param rideId the ride id
     */
    @Override
    public Optional<RideDTO> getRideById(String rideId) {
        LOGGER.trace("Retrieving ride '{}' from MongoDB database", rideId);
        Document rideDoc = this.ridesCollection.find(eq(JsonFieldKey.RIDE_ID_KEY, rideId)).first();
        
        if (rideDoc == null) {
            LOGGER.trace("Ride not found: '{}'", rideId);
            return Optional.empty();
        } else {
            LOGGER.trace("Ride found: '{}'", rideId);
            Document userDoc = rideDoc.get(JsonFieldKey.RIDE_USER_KEY, Document.class);
            Document ebikeDoc = rideDoc.get(JsonFieldKey.RIDE_EBIKE_KEY, Document.class);
            return Optional.of(new RideDTO(
                    java.sql.Date.valueOf(rideDoc.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                    Optional.ofNullable(rideDoc.getString(JsonFieldKey.RIDE_END_DATE_KEY)).map(java.sql.Date::valueOf),
                    UserDTO.fromJson(new JsonObject(userDoc.toJson())),
                    EBikeDTO.fromJson(new JsonObject(ebikeDoc.toJson())),
                    rideDoc.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                    rideDoc.getString(JsonFieldKey.RIDE_ID_KEY)
            ));
        }
    }
    
    /**
     * Updates the ongoing status of a ride.
     * @param userId the user id of the user taking the ride
     * @param ebikeId the ebike id of the ebike used in the ride
     */
    @Override
    public Optional<RideDTO> getRideById(String userId, String ebikeId) {
        LOGGER.trace("Retrieving ride by user '{}' and ebike '{}' from MongoDB database", userId, ebikeId);
        Document rideDoc = this.ridesCollection.find(eq(JsonFieldKey.RIDE_USER_ID_KEY, userId)).first();
        
        if (rideDoc == null) {
            LOGGER.trace("Ride not found for user '{}' and ebike '{}'", userId, ebikeId);
            return Optional.empty();
        } else {
            LOGGER.trace("Ride found for user '{}' and ebike '{}'", userId, ebikeId);
            Document userDoc = rideDoc.get(JsonFieldKey.RIDE_USER_KEY, Document.class);
            Document ebikeDoc = rideDoc.get(JsonFieldKey.RIDE_EBIKE_KEY, Document.class);
            return Optional.of(new RideDTO(
                    java.sql.Date.valueOf(rideDoc.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                    Optional.ofNullable(rideDoc.getString(JsonFieldKey.RIDE_END_DATE_KEY)).map(java.sql.Date::valueOf),
                    UserDTO.fromJson(new JsonObject(userDoc.toJson())),
                    EBikeDTO.fromJson(new JsonObject(ebikeDoc.toJson())),
                    rideDoc.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                    rideDoc.getString(JsonFieldKey.RIDE_ID_KEY)
            ));
        }
    }
    
    /**
     * Updates the ongoing status of a ride.
     * @param userId the user id of the user taking the ride
     * @param ebikeId the ebike id of the ebike used in the ride
     */
    @Override
    public Optional<RideDTO> getOngoingRideById(String userId, String ebikeId) {
        LOGGER.trace("Retrieving ongoing ride by user '{}' and ebike '{}' from MongoDB database", userId, ebikeId);
        Document rideDoc = this.ridesCollection.find(eq(JsonFieldKey.RIDE_USER_ID_KEY, userId)).first();
    
        if (rideDoc == null) {
            LOGGER.trace("Ongoing ride not found for user '{}' and ebike '{}'", userId, ebikeId);
            return Optional.empty();
        } else {
            LOGGER.trace("Ongoing ride found for user '{}' and ebike '{}'", userId, ebikeId);
            Document userDoc = rideDoc.get(JsonFieldKey.RIDE_USER_KEY, Document.class);
            Document ebikeDoc = rideDoc.get(JsonFieldKey.RIDE_EBIKE_KEY, Document.class);
            return Optional.of(new RideDTO(
                    java.sql.Date.valueOf(rideDoc.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                    Optional.ofNullable(rideDoc.getString(JsonFieldKey.RIDE_END_DATE_KEY)).map(java.sql.Date::valueOf),
                    UserDTO.fromJson(new JsonObject(userDoc.toJson())),
                    EBikeDTO.fromJson(new JsonObject(ebikeDoc.toJson())),
                    rideDoc.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                    rideDoc.getString(JsonFieldKey.RIDE_ID_KEY)
            ));
        }
    }
    
    /**
     * Get all rides.
     */
    @Override
    public Iterable<RideDTO> getAllRides() {
        LOGGER.trace("Retrieving all rides from MongoDB database");
        ArrayList<RideDTO> rides = new ArrayList<>();
        this.ridesCollection.find().forEach(rideDoc -> {
            Document userDoc = rideDoc.get(JsonFieldKey.RIDE_USER_KEY, Document.class);
            Document ebikeDoc = rideDoc.get(JsonFieldKey.RIDE_EBIKE_KEY, Document.class);
            rides.add(new RideDTO(
                    java.sql.Date.valueOf(rideDoc.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                    Optional.ofNullable(rideDoc.getString(JsonFieldKey.RIDE_END_DATE_KEY)).map(java.sql.Date::valueOf),
                    UserDTO.fromJson(new JsonObject(userDoc.toJson())),
                    EBikeDTO.fromJson(new JsonObject(ebikeDoc.toJson())),
                    rideDoc.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                    rideDoc.getString(JsonFieldKey.RIDE_ID_KEY)
            ));
        });
        LOGGER.trace("Retrieved {} rides", rides.size());
        return rides;
    }

    /**
     * Inserts a ride.
     * @param ride the ride
     */
    @Override
    public void insertRide(RideDTO ride) {
        LOGGER.trace("Preparing document to insert ride '{}' to MongoDB database", ride.id());
        Document rideDoc = new Document(JsonFieldKey.RIDE_ID_KEY, ride.id())
                .append(JsonFieldKey.RIDE_START_DATE_KEY, ride.startedDate().toString())
                .append(JsonFieldKey.RIDE_END_DATE_KEY, ride.endDate().map(java.sql.Date::toString).orElse(null))
                .append(JsonFieldKey.RIDE_ONGONING_KEY, ride.ongoing())
                .append(JsonFieldKey.RIDE_USER_KEY, Document.parse(ride.user().toJsonObject().encode()))
                .append(JsonFieldKey.RIDE_EBIKE_KEY, Document.parse(ride.ebike().toJsonObject().encode()));
        
        LOGGER.trace("Document prepared: '{}'", rideDoc.toJson());
        this.ridesCollection.insertOne(rideDoc);
        LOGGER.trace("Ride inserted: '{}'", ride.id());
    }
    
    /**
     * Gets the eBike with the specified id.
     * @param ebikeId The id of the eBike.
     * @return The eBike.
     */
    @Override
    public Optional<EBikeDTO> getEBikeById(String ebikeId) {
        for (RideDTO ride : this.getAllRides()) {
            if (ride.ebike().id().equals(ebikeId)) {
                return Optional.of(ride.ebike());
            }
        }
        return Optional.empty();
    }
    
    /**
     * Gets the user with the specified id.
     * @param userId The id of the user.
     * @return The user.
     */
    @Override
    public Optional<UserDTO> getUserById(String userId) {
        for (RideDTO ride : this.getAllRides()) {
            if (ride.user().id().equals(userId)) {
                return Optional.of(ride.user());
            }
        }
        return Optional.empty();
    }
    
    /**
     * Inserts a user.
     * @param user the user
     */
    @Override
    public void insertUser(UserDTO user) {
        LOGGER.trace("Preparing document to insert user '{}' to MongoDB database", user.id());
        Document userDoc = new Document(JsonFieldKey.USER_ID_KEY, user.id())
                .append(JsonFieldKey.USER_CREDIT_KEY, user.credit());
        
        LOGGER.trace("Document prepared: '{}'", userDoc.toJson());
        this.ridesCollection.insertOne(userDoc);
        LOGGER.trace("User inserted: '{}'", user.id());
    }
    
    /**
     * Inserts an eBike.
     * @param ebike the eBike
     */
    @Override
    public void insertEbike(EBikeDTO ebike) {
        LOGGER.trace("Preparing document to insert ebike '{}' to MongoDB database", ebike.id());
        Document ebikeDoc = new Document(JsonFieldKey.EBIKE_ID_KEY, ebike.id())
                .append(JsonFieldKey.EBIKE_STATE_KEY, ebike.state().toString())
                .append(JsonFieldKey.EBIKE_X_LOCATION_KEY, ebike.location().x())
                .append(JsonFieldKey.EBIKE_Y_LOCATION_KEY, ebike.location().y())
                .append(JsonFieldKey.EBIKE_X_DIRECTION_KEY, ebike.direction().x())
                .append(JsonFieldKey.EBIKE_Y_DIRECTION_KEY, ebike.direction().y())
                .append(JsonFieldKey.EBIKE_SPEED_KEY, ebike.speed())
                .append(JsonFieldKey.EBIKE_BATTERY_KEY, ebike.batteryLevel());
        
        LOGGER.trace("Document prepared: '{}'", ebikeDoc.toJson());
        this.ridesCollection.insertOne(ebikeDoc);
        LOGGER.trace("EBike inserted: '{}'", ebike.id());
    }
}
