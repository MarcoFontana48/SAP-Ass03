package sap.ass02.infrastructure.persistence.local;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.UserDTO;
import sap.ddd.Repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;

import static sap.ass02.domain.utils.JsonFieldKey.*;

/**
 * Abstract class for local JSON repository adapters.
 */
public abstract class AbstractLocalJsonRepositoryAdapter implements Repository {
    private static final Logger LOGGER = LogManager.getLogger(AbstractLocalJsonRepositoryAdapter.class);
    private final String userFolder = "user";
    private final String databaseFolder = "./database";
    
    /**
     * Constructor.
     */
    public AbstractLocalJsonRepositoryAdapter() {
        LOGGER.trace("LocalJsonRepositoryAdapter instantiated (remember to initialize it before using it!)");
    }
    
    /**
     * Instantiates a new Abstract local json repository adapter.
     */
    @Override
    public void init() {
        this.makeDir(this.databaseFolder);
        this.makeDir(this.databaseFolder + File.separator + this.userFolder);
        LOGGER.trace("LocalJsonRepositoryAdapter initialized");
    }
    
    /**
     * Insert a new user into the repository.
     *
     * @param user the user to be inserted
     * @return true if the user was inserted successfully
     */
    @Override
    public boolean insertUser(UserDTO user) {
        LOGGER.trace("Inserting user: {}", user.toString());
        JsonObject obj = new JsonObject();
        obj.put(USER_ID_KEY, user.id());
        obj.put(USER_CREDIT_KEY, user.credit());
        obj.put(USER_X_LOCATION_KEY, user.xLocation());
        obj.put(USER_Y_LOCATION_KEY, user.yLocation());
        try {
            this.saveObj(this.userFolder, user.id(), obj);
        } catch (RuntimeException | FileAlreadyExistsException e) {
            LOGGER.warn(e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Retrieve a user from the repository.
     *
     * @param userId the id of the user to be retrieved
     * @return the user
     */
    @Override
    public Optional<UserDTO> getUserById(String userId) {
        LOGGER.trace("Retrieving user with ID: {}", userId);
        File userFile = new File(this.databaseFolder + File.separator + this.userFolder + File.separator + userId + ".json");
        if (userFile.exists()) {
            LOGGER.trace("UserDTO file found: {}", userFile.getAbsolutePath());
            try {
                String content = new String(Files.readAllBytes(userFile.toPath()));
                LOGGER.trace("UserDTO file content: {}", content);
                JsonObject obj = new JsonObject(content);
                LOGGER.trace("UserDTO file content parsed: {}", obj.encodePrettily());
                UserDTO retrievedUser = new UserDTO(obj.getString(USER_ID_KEY), obj.getInteger(USER_CREDIT_KEY), obj.getDouble(USER_X_LOCATION_KEY), obj.getDouble(USER_Y_LOCATION_KEY));
                LOGGER.trace("UserDTO retrieved: {}", retrievedUser.toString());
                return Optional.of(retrievedUser);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.trace("UserDTO file not found, returning '{}'", Optional.empty());
        }
        return Optional.empty();
    }
    
    /**
     * Retrieve all users from the repository.
     */
    @Override
    public Iterable<UserDTO> getAllUsers() {
        ArrayList<UserDTO> users = new ArrayList<>();
        File userDir = new File(this.databaseFolder + File.separator + this.userFolder);
        File[] files = userDir.listFiles((file, name) -> name.endsWith(".json"));
        
        if (files != null) {
            for (File file : files) {
                String userId = file.getName().replace(".json", "");
                this.getUserById(userId).ifPresent(users::add);
            }
        }
        
        return users;
    }
    
    /**
     * Update a user in the repository.
     *
     * @param userID the user to be updated
     * @param credits the new credits of the user
     * @return true if the user was updated successfully
     */
    @Override
    public boolean updateUserCredits(String userID, int credits) {
        LOGGER.trace("Updating user credits: {}", credits);
        File userFile = new File(this.databaseFolder + File.separator + this.userFolder + File.separator + userID + ".json");
        if (userFile.exists()) {
            LOGGER.trace("UserDTO file found: {}", userFile.getAbsolutePath());
            try {
                String content = new String(Files.readAllBytes(userFile.toPath()));
                LOGGER.trace("UserDTO file content: {}", content);
                JsonObject obj = new JsonObject(content);
                obj.put(USER_CREDIT_KEY, credits);
                LOGGER.trace("UserDTO file content updated: {}", obj.encodePrettily());
                this.saveObj(this.userFolder, userID, obj);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.error("UserDTO with ID {} does not exist", userID);
            throw new IllegalArgumentException("UserDTO with ID " + userID + " does not exist.");
        }
        return true;
    }
    
    /**
     * Update a user in the repository.
     *
     * @param name the user to be updated
     */
    private void makeDir(final String name) {
        LOGGER.trace("Creating directory: {}", name);
        File dir = new File(name);
        
        if (dir.mkdir()) {
            LOGGER.trace("Directory '{}' created", dir.getAbsolutePath());
        } else {
            LOGGER.warn("Unable to create directory '{}', something went wrong", dir.getAbsolutePath());
        }
    }
    
    /**
     * Save an object.
     *
     * @param db the database
     * @param id the id
     * @param obj the object
     */
    private void saveObj(final String db, final String id, final JsonObject obj) throws FileAlreadyExistsException {
        File file = new File(this.databaseFolder + File.separator + db + File.separator + id + ".json");
        
        try (FileWriter fw = new FileWriter(file, false); BufferedWriter wr = new BufferedWriter(fw)) {
            wr.write(obj.encodePrettily());
            wr.flush();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
