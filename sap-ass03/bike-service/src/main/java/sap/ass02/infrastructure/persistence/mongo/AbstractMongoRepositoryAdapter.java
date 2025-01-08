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
import sap.ass02.domain.dto.BikeStateDTO;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ass02.infrastructure.EndpointPath;
import sap.ass02.infrastructure.persistence.AbstractVerticleRepository;
import sap.ass02.infrastructure.persistence.properties.Connectable;

import java.util.ArrayList;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public abstract class AbstractMongoRepositoryAdapter extends AbstractVerticleRepository implements Connectable {
    private static final Logger LOGGER = LogManager.getLogger(AbstractMongoRepositoryAdapter.class);
    private MongoCollection<Document> ebikeCollection;
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
        this.ebikeCollection = database1.getCollection("users");
        LOGGER.debug("{} initialized", this.getClass().getSimpleName());
    }
    
    @Override
    public boolean insertEbike(final EBikeDTO eBike) {
        LOGGER.trace("Preparing document to insert eBike '{}' to MongoDB database", eBike);
        Document ebikeDoc = new Document("id", eBike.id())
                .append("state", eBike.state())
                .append("x_location", eBike.location().x())
                .append("y_location", eBike.location().y())
                .append("x_direction", eBike.direction().x())
                .append("y_direction", eBike.direction().y())
                .append("speed", eBike.speed())
                .append("battery", eBike.batteryLevel());
        this.ebikeCollection.insertOne(ebikeDoc);
        LOGGER.trace("EBike inserted: '{}'", eBike);
        return true;
    }
    
    @Override
    public boolean updateEBike(EBikeDTO ebike) {
        LOGGER.trace("Preparing document to update ebike '{}' credits in MongoDB database", ebike);
        this.ebikeCollection.updateOne(eq("id", ebike.id()), new Document("$set",
                new Document("state", ebike.state())
                .append("x_location", ebike.location().x())
                .append("y_location", ebike.location().y())
                .append("x_direction", ebike.direction().x())
                .append("y_direction", ebike.direction().y())
                .append("speed", ebike.speed())
                .append("battery", ebike.batteryLevel())));
        LOGGER.trace("EBike updated: '{}'", ebike);
        return true;
    }
    
    @Override
    public Optional<EBikeDTO> getEbikeById(final String ebikeId) {
        LOGGER.trace("Preparing query to retrieve ebike with id: '{}' from MongoDB database", ebikeId);
        Document ebikeDoc = this.ebikeCollection.find(eq("id", ebikeId)).first();
        if (ebikeDoc != null) {
            LOGGER.trace("EBike found: '{}'", ebikeDoc);
            EBikeDTO retrievedEBike = new EBikeDTO(
                    ebikeDoc.getString("id"),
                    BikeStateDTO.valueOf(ebikeDoc.getString("state")),
                    new P2dDTO(ebikeDoc.getDouble("x_location"), ebikeDoc.getDouble("y_location")),
                    new V2dDTO(ebikeDoc.getDouble("x_direction"), ebikeDoc.getDouble("y_direction")),
                    ebikeDoc.getDouble("speed"),
                    ebikeDoc.getInteger("battery"));
            return Optional.of(retrievedEBike);
        } else {
            LOGGER.trace("EBike not found");
            return Optional.empty();
        }
    }
    
    @Override
    public Iterable<EBikeDTO> getAllEBikes() {
        LOGGER.trace("Preparing query to retrieve all ebikes from MongoDB database");
        ArrayList<EBikeDTO> ebikes = new ArrayList<>();
        for (Document ebikeDoc : this.ebikeCollection.find()) {
            EBikeDTO eBikeDTO = new EBikeDTO(
                    ebikeDoc.getString("id"),
                    BikeStateDTO.valueOf(ebikeDoc.getString("state")),
                    new P2dDTO(ebikeDoc.getDouble("x_location"), ebikeDoc.getDouble("y_location")),
                    new V2dDTO(ebikeDoc.getDouble("x_direction"), ebikeDoc.getDouble("y_direction")),
                    ebikeDoc.getDouble("speed"),
                    ebikeDoc.getInteger("battery"));
            ebikes.add(eBikeDTO);
        }
        LOGGER.trace("EBikes retrieved: '{}'", ebikes);
        return ebikes;
    }
}
