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
import sap.ass02.domain.dto.UserDTO;
import sap.ass02.infrastructure.EndpointPath;
import sap.ass02.infrastructure.persistence.AbstractVerticleRepository;
import sap.ass02.infrastructure.persistence.properties.Connectable;

import java.util.ArrayList;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public abstract class AbstractMongoRepositoryAdapter extends AbstractVerticleRepository implements Connectable {
    private static final Logger LOGGER = LogManager.getLogger(AbstractMongoRepositoryAdapter.class);
    private MongoCollection<Document> userCollection;
    private WebClient webClient;
    
    @Override
    public void init() {
        LOGGER.debug("Initializing AbstractMongoRepositoryAdapter...");
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
        this.userCollection = database1.getCollection("users");
        LOGGER.debug("{} initialized", this.getClass().getSimpleName());
    }
    
    @Override
    public boolean insertUser(final UserDTO user) {
        LOGGER.trace("Preparing document to insert user '{}' to MongoDB database", user);
        Document userDoc = new Document("id", user.id())
                .append("credit", user.credit());
        LOGGER.trace("Document prepared: '{}'", userDoc.toJson());
        this.userCollection.insertOne(userDoc);
        LOGGER.trace("User inserted: '{}'", user);
        return true;
    }
    
    @Override
    public boolean updateUserCredits(String userID, int credits) {
        LOGGER.trace("Preparing document to update user '{}' credits in MongoDB database", userID);
        this.userCollection.updateOne(eq("id", userID), new Document("$set", new Document("credit", credits)));
        LOGGER.trace("User credits updated: '{}'", userID);
        return true;
    }
    
    @Override
    public Optional<UserDTO> getUserById(final String userId) {
        LOGGER.trace("Preparing query to retrieve user with id: '{}' from MongoDB database", userId);
        Document userDoc = this.userCollection.find(eq("id", userId)).first();
        if (userDoc != null) {
            LOGGER.trace("User found: '{}'", userDoc);
            UserDTO retrievedUser = new UserDTO(userDoc.getString("id"), userDoc.getInteger("credit"));
            return Optional.of(retrievedUser);
        } else {
            LOGGER.trace("User not found");
            return Optional.empty();
        }
    }
    
    @Override
    public Iterable<UserDTO> getAllUsers() {
        LOGGER.trace("Preparing query to retrieve all users from MongoDB database");
        ArrayList<UserDTO> users = new ArrayList<>();
        for (Document userDoc : this.userCollection.find()) {
            UserDTO user = new UserDTO(userDoc.getString("id"), userDoc.getInteger("credit"));
            users.add(user);
        }
        LOGGER.trace("Users retrieved: '{}'", users);
        return users;
    }
}
