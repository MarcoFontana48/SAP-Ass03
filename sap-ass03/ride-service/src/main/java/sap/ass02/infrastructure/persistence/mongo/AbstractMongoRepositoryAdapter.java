package sap.ass02.infrastructure.persistence.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
import sap.ass02.infrastructure.persistence.AbstractVerticleRepository;
import sap.ass02.infrastructure.persistence.properties.Connectable;

import java.util.ArrayList;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public abstract class AbstractMongoRepositoryAdapter extends AbstractVerticleRepository implements Connectable {
    private static final Logger LOGGER = LogManager.getLogger(AbstractMongoRepositoryAdapter.class);
    private MongoCollection<Document> ridesCollection;
    private WebClient webClient;

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
        LOGGER.trace("Connecting to MongoDB database with arguments: host: {}, port: {}, dbName: {}, dbUsername: {}, dbPassword: {}", host, port, database, username, password);
        MongoClient mongoClient = MongoClients.create(String.format("mongodb://%s:%s@%s:%s", username, password, host, port));
        MongoDatabase database1 = mongoClient.getDatabase(database);
        this.ridesCollection = database1.getCollection("rides");
        LOGGER.debug("{} initialized", this.getClass().getSimpleName());
    }

    @Override
    public void updateRideEnd(RideDTO ride) {
        LOGGER.trace("Updating ride '{}' in MongoDB database", ride.id());
        this.ridesCollection.updateOne(eq(JsonFieldKey.RIDE_ID_KEY, ride.id()), new Document("$set", new Document(JsonFieldKey.RIDE_END_DATE_KEY, ride.endDate().map(java.sql.Date::toString).orElse(null))));
        LOGGER.trace("Ride updated: '{}'", ride.id());
    }

    @Override
    public Optional<RideDTO> getRideById(String rideId) {
        LOGGER.trace("Retrieving ride '{}' from MongoDB database", rideId);
        Document rideDoc = this.ridesCollection.find(eq(JsonFieldKey.RIDE_ID_KEY, rideId)).first();

        if (rideDoc == null) {
            LOGGER.trace("Ride not found: '{}'", rideId);
            return Optional.empty();
        } else {
            LOGGER.trace("Ride found: '{}'", rideId);
            return Optional.of(new RideDTO(
                    java.sql.Date.valueOf(rideDoc.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                    Optional.ofNullable(rideDoc.getString(JsonFieldKey.RIDE_END_DATE_KEY)).map(java.sql.Date::valueOf),
                    new UserDTO(rideDoc.getString(JsonFieldKey.RIDE_USER_ID_KEY), -1),
                    new EBikeDTO(rideDoc.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY), EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0,0), new V2dDTO(0,0), 0, 0),
                    rideDoc.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                    rideDoc.getString(JsonFieldKey.RIDE_ID_KEY)
            ));
        }
    }

    @Override
    public Optional<RideDTO> getRideById(String userId, String ebikeId) {
        LOGGER.trace("Retrieving ride by user '{}' and ebike '{}' from MongoDB database", userId, ebikeId);
        Document rideDoc = this.ridesCollection.find(eq(JsonFieldKey.RIDE_USER_ID_KEY, userId)).first();

        if (rideDoc == null) {
            LOGGER.trace("Ride not found for user '{}' and ebike '{}'", userId, ebikeId);
            return Optional.empty();
        } else {
            LOGGER.trace("Ride found for user '{}' and ebike '{}'", userId, ebikeId);
            return Optional.of(new RideDTO(
                    java.sql.Date.valueOf(rideDoc.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                    Optional.ofNullable(rideDoc.getString(JsonFieldKey.RIDE_END_DATE_KEY)).map(java.sql.Date::valueOf),
                    new UserDTO(rideDoc.getString(JsonFieldKey.RIDE_USER_ID_KEY), -1),
                    new EBikeDTO(rideDoc.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY), EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0,0), new V2dDTO(0,0), 0, 0),
                    rideDoc.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                    rideDoc.getString(JsonFieldKey.RIDE_ID_KEY)
            ));
        }
    }

    @Override
    public Optional<RideDTO> getOngoingRideById(String userId, String ebikeId) {
        LOGGER.trace("Retrieving ongoing ride by user '{}' and ebike '{}' from MongoDB database", userId, ebikeId);
        Document rideDoc = this.ridesCollection.find(eq(JsonFieldKey.RIDE_USER_ID_KEY, userId)).first();

        if (rideDoc == null) {
            LOGGER.trace("Ongoing ride not found for user '{}' and ebike '{}'", userId, ebikeId);
            return Optional.empty();
        } else {
            LOGGER.trace("Ongoing ride found for user '{}' and ebike '{}'", userId, ebikeId);
            return Optional.of(new RideDTO(
                    java.sql.Date.valueOf(rideDoc.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                    Optional.ofNullable(rideDoc.getString(JsonFieldKey.RIDE_END_DATE_KEY)).map(java.sql.Date::valueOf),
                    new UserDTO(rideDoc.getString(JsonFieldKey.RIDE_USER_ID_KEY), -1),
                    new EBikeDTO(rideDoc.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY), EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0,0), new V2dDTO(0,0), 0, 0),
                    rideDoc.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                    rideDoc.getString(JsonFieldKey.RIDE_ID_KEY)
            ));
        }
    }

    @Override
    public Iterable<RideDTO> getAllRides() {
        LOGGER.trace("Retrieving all rides from MongoDB database");
        ArrayList<RideDTO> rides = new ArrayList<>();
        this.ridesCollection.find().forEach(rideDoc -> rides.add(new RideDTO(
                java.sql.Date.valueOf(rideDoc.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                Optional.ofNullable(rideDoc.getString(JsonFieldKey.RIDE_END_DATE_KEY)).map(java.sql.Date::valueOf),
                new UserDTO(rideDoc.getString(JsonFieldKey.RIDE_USER_ID_KEY), -1),
                new EBikeDTO(rideDoc.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY), EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0,0), new V2dDTO(0,0), 0, 0),
                rideDoc.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                rideDoc.getString(JsonFieldKey.RIDE_ID_KEY)
        )));
        LOGGER.trace("Retrieved {} rides", rides.size());
        return rides;
    }

    @Override
    public void insertRide(RideDTO ride) {
        LOGGER.trace("Preparing document to insert ride '{}' to MongoDB database", ride.id());
        Document rideDoc = new Document(JsonFieldKey.RIDE_ID_KEY, ride.id())
                .append(JsonFieldKey.RIDE_START_DATE_KEY, ride.startedDate().toString())
                .append(JsonFieldKey.RIDE_END_DATE_KEY, ride.endDate().map(java.sql.Date::toString).orElse(null))
                .append(JsonFieldKey.RIDE_ONGONING_KEY, ride.ongoing())
                .append(JsonFieldKey.RIDE_USER_ID_KEY, ride.user().id())
                .append(JsonFieldKey.RIDE_EBIKE_ID_KEY, ride.ebike().id());
    
        LOGGER.trace("Document prepared: '{}'", rideDoc.toJson());
        this.ridesCollection.insertOne(rideDoc);
        LOGGER.trace("Ride inserted: '{}'", ride.id());
    }
}
