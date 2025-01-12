package sap.ass02.domain.application;

import io.vertx.core.AbstractVerticle;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.UserDTO;
import sap.ddd.Repository;
import sap.ddd.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Proxy class for the user service to produce events upon calling the service.
 */
public final class UserServiceVerticle extends AbstractVerticle implements ServiceVerticle {
    private static final Logger LOGGER = LogManager.getLogger(UserServiceVerticle.class);
    private final Service userService = new UserService();
    Map<String, String> producerConfig = new HashMap<>();
    private KafkaProducer<String, String> producer;
    
    /**
     * Instantiates a new User service verticle.
     */
    @Override
    public void start() {
        this.producerConfig.put("bootstrap.servers", "kafka:9092");
        this.producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerConfig.put("acks", "1");
        
        this.producer = KafkaProducer.create(this.vertx, this.producerConfig);
    }
        
        /**
         * Adds a user to the repository.
         */
    @Override
    public boolean addUser(String userId, int credits) {
        LOGGER.trace("Adding user with id '{}' and credits '{}'", userId, credits);
        this.userService.addUser(userId, credits);
        UserDTO addedUser = new User(userId, credits).toDTO();
        LOGGER.trace("Publishing insert-user event '{}'", addedUser.toJsonString());
        this.vertx.eventBus().publish("insert-user", addedUser.toJsonString());
        this.producer.write(KafkaProducerRecord.create("client", "client-insert-user", addedUser.toJsonString()));
        this.producer.write(KafkaProducerRecord.create("user-service", "insert-user", addedUser.toJsonString()));
        return true;
    }
    
    /**
     * Adds a user to the repository.
     */
    @Override
    public boolean addUser(String userId, int credits, double xLocation, double yLocation) {
        LOGGER.trace("Adding user with id '{}', credits '{}', xLocation '{}' and yLocation '{}'", userId, credits, xLocation, yLocation);
        this.userService.addUser(userId, credits, xLocation, yLocation);
        UserDTO addedUser = new User(userId, credits, xLocation, yLocation).toDTO();
        LOGGER.trace("Publishing insert-user event '{}'", addedUser.toJsonString());
        this.vertx.eventBus().publish("insert-user", addedUser.toJsonString());
        this.producer.write(KafkaProducerRecord.create("client", "client-insert-user", addedUser.toJsonString()));
        this.producer.write(KafkaProducerRecord.create("user-service", "insert-user", addedUser.toJsonString()));
        return true;
    }
    
    /**
     * Gets a user from the repository.
     */
    @Override
    public User getUser(String userId) {
        return this.userService.getUser(userId);
    }
    
    /**
     * Updates a user in the repository.
     */
    @Override
    public boolean updateUserCredits(String userId, int credits) {
        LOGGER.trace("Updating user with id '{}' to credits '{}'", userId, credits);
        this.userService.updateUserCredits(userId, credits);
        UserDTO userDTO = new User(userId, credits).toDTO();
        LOGGER.trace("Publishing update-user-credits event '{}'", userDTO.toJsonString());
        this.vertx.eventBus().publish("update-user-credits", userDTO.toJsonString());
        this.vertx.eventBus().publish("user-update", userDTO.toJsonString());
        this.producer.write(KafkaProducerRecord.create("client", "client-user-update", userDTO.toJsonString()));
        return true;
    }
    
    /**
     * Retrieves all users from the repository.
     */
    @Override
    public Iterable<User> getUsers() {
        return this.userService.getUsers();
    }
    
    /**
     * Attaches a repository to the service.
     */
    @Override
    public void attachRepository(Repository repository) {
        this.userService.attachRepository(repository);
    }
}
