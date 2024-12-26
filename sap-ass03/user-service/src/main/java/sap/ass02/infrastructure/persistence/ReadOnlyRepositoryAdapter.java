package sap.ass02.infrastructure.persistence;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.UserDTO;

import java.util.Optional;

public final class ReadOnlyRepositoryAdapter extends AbstractVerticleReadOnlyRepository {
    private static final Logger LOGGER = LogManager.getLogger(ReadOnlyRepositoryAdapter.class);
    private final AbstractVerticleRepository readWriteRepository;
    
    public ReadOnlyRepositoryAdapter(AbstractVerticleRepository repository) {
        super();
        this.readWriteRepository = repository;
    }
    
    @Override
    public void start() {
        this.init();
        
        this.vertx.eventBus().consumer("insert-user", message -> {
            LOGGER.trace("Received vertx insert-user event '{}'", message.body());
            JsonObject userJsonObject = new JsonObject(String.valueOf(message.body()));
            UserDTO userDTO = new User(userJsonObject).toDTO();
            this.insertEbike(userDTO);
            LOGGER.trace("Inserted user '{}'", userDTO);
        });
        
        this.vertx.eventBus().consumer("user-update", message -> {
            LOGGER.trace("Received vertx user-update event '{}'", message.body());
            JsonObject userJsonObject = new JsonObject(String.valueOf(message.body()));
            UserDTO dto = new User(userJsonObject).toDTO();
            this.readWriteRepository.updateUserCredits(dto.id(), dto.credit());
            LOGGER.trace("Updated user '{}'", userJsonObject);
        });
    }
    
    private void init() {
        this.readWriteRepository.init();
    }
    
    /**
     * Retrieves the ebike from the repository given its id
     *
     * @param userId
     * @return Optionally found user
     */
    @Override
    public Optional<UserDTO> getUserById(String userId) {
        return Optional.empty();
    }
    
    /**
     * Retrieves all the ebikes stored inside the repository
     *
     * @return Iterable of found ebikes
     */
    @Override
    public Iterable<UserDTO> getAllUsers() {
        return null;
    }
    
    private void insertEbike(UserDTO user) {
        this.readWriteRepository.insertUser(user);
    }
}
