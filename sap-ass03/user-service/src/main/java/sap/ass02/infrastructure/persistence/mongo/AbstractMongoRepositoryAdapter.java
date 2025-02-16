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
import sap.ass02.infrastructure.persistence.properties.Connectable;
import sap.ass02.application.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

/**
 * Abstract class for MongoDB repository adapters.
 */
public abstract class AbstractMongoRepositoryAdapter implements Connectable, Repository {
    private static final Logger LOGGER = LogManager.getLogger(AbstractMongoRepositoryAdapter.class);
    private MongoCollection<Document> userCollection;
    private WebClient webClient;
    
    /**
     * Initializes the repository adapter.
     */
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
    
    /**
     * Fetches the configuration from the given endpoint.
     *
     * @param endpoint the endpoint to fetch the configuration from
     * @return a future with the fetched configuration
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
     * @param host the host
     * @param port the port
     * @param database the database name
     * @param username the username
     * @param password the password
     */
    @Override
    public void connect(String host, String port, String database, String username, String password) {
        LOGGER.trace("Connecting to MongoDB database with arguments: host: {}, port: {}, dbName: {}, dbUsername: {}, dbPassword: {}", host, port, database, username, password);
        MongoClient mongoClient = MongoClients.create(String.format("mongodb://%s:%s@%s:%s", username, password, host, port));
        MongoDatabase database1 = mongoClient.getDatabase(database);
        this.userCollection = database1.getCollection("users");
        LOGGER.debug("{} initialized", this.getClass().getSimpleName());
    }
    
    /**
     * Inserts a user into the MongoDB database.
     *
     * @param user the user to insert
     * @return true if the user was inserted, false otherwise
     */
    @Override
    public boolean insertUser(final UserDTO user) {
        LOGGER.trace("Preparing document to insert user '{}' to MongoDB database", user);
        Document userDoc = new Document("id", user.id())
                .append("credit", user.credit())
                .append("x_location", user.xLocation())
                .append("y_location", user.yLocation());
        LOGGER.trace("Document prepared: '{}'", userDoc.toJson());
        this.userCollection.insertOne(userDoc);
        LOGGER.trace("User inserted: '{}'", user);
        return true;
    }
    
    /**
     * Updates a user's credits in the MongoDB database.
     *
     * @param userID the user's ID
     * @param credits the new credits
     * @return true if the user's credits were updated, false otherwise
     */
    @Override
    public boolean updateUserCredits(String userID, int credits) {
        LOGGER.trace("Preparing document to update user '{}' credits in MongoDB database", userID);
        this.userCollection.updateMany(eq("id", userID), new Document("mod", List.of(new Document("credit", credits), new Document("x_location", 0.0), new Document("y_location", 0.0))));
        LOGGER.trace("User credits updated: '{}'", userID);
        return true;
    }
    
    /**
     * Retrieves a user from the MongoDB database.
     *
     * @param userId the user's ID
     * @return the user if found, empty otherwise
     */
    @Override
    public Optional<UserDTO> getUserById(final String userId) {
        LOGGER.trace("Preparing query to retrieve user with id: '{}' from MongoDB database", userId);
        Document userDoc = this.userCollection.find(eq("id", userId)).first();
        if (userDoc != null) {
            LOGGER.trace("User found: '{}'", userDoc);
            UserDTO retrievedUser = new UserDTO(userDoc.getString("id"), userDoc.getInteger("credit"), userDoc.getDouble("x_location"), userDoc.getDouble("y_location"));
            return Optional.of(retrievedUser);
        } else {
            LOGGER.trace("User not found");
            return Optional.empty();
        }
    }
    
    /**
     * Retrieves all users from the MongoDB database.
     *
     * @return all users
     */
    @Override
    public Iterable<UserDTO> getAllUsers() {
        LOGGER.trace("Preparing query to retrieve all users from MongoDB database");
        ArrayList<UserDTO> users = new ArrayList<>();
        for (Document userDoc : this.userCollection.find()) {
            UserDTO user = new UserDTO(userDoc.getString("id"), userDoc.getInteger("credit"), userDoc.getDouble("x_location"), userDoc.getDouble("y_location"));
            users.add(user);
        }
        LOGGER.trace("Users retrieved: '{}'", users);
        return users;
    }
}
