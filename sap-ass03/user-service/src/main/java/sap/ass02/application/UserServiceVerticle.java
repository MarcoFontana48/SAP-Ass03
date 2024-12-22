package sap.ass02.application;

import io.vertx.core.AbstractVerticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.DTOUtils;
import sap.ass02.domain.dto.UserDTO;
import sap.ddd.Repository;
import sap.ddd.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Pseudo-proxy class for the UserService class to be used for CQRS.
 */
public final class UserServiceVerticle extends AbstractVerticle implements ServiceVerticle {
    private static final Logger LOGGER = LogManager.getLogger(UserServiceVerticle.class);
    private final Service userService = new UserService();
    private Repository queryOnlyRepository;
    
    @Override
    public boolean addUser(String userId, int credits) {
        LOGGER.trace("Adding user with id '{}' and credits '{}'", userId, credits);
        this.userService.addUser(userId, credits);
        UserDTO addedUser = new User(userId, credits).toDTO();
        LOGGER.trace("Publishing insert-user event '{}'", addedUser.toJsonString());
        this.vertx.eventBus().publish("insert-user", addedUser.toJsonString());
        return true;
    }
    
    @Override
    public User getUser(String userId) {
        LOGGER.trace("Getting user with id '{}'", userId);
        var user = this.queryOnlyRepository.getUserById(userId);
        return user.map(userDTO -> new User(userDTO.id(), userDTO.credit())).orElse(null);
    }
    
    @Override
    public boolean updateUserCredits(String userId, int credits) {
        LOGGER.trace("Updating user with id '{}' to credits '{}'", userId, credits);
        this.userService.updateUserCredits(userId, credits);
        UserDTO userDTO = new User(userId, credits).toDTO();
        LOGGER.trace("Publishing update-user-credits event '{}'", userDTO.toJsonString());
        this.vertx.eventBus().publish("update-user-credits", userDTO.toJsonString());
        return true;
    }
    
    @Override
    public Iterable<User> getUsers() {
        Iterable<UserDTO> allUsersDTO = this.queryOnlyRepository.getAllUsers();
        List<User> users = new ArrayList<>();
        allUsersDTO.forEach(userDTO -> users.add(DTOUtils.toUser(userDTO)));
        return users;
    }
    
    @Override
    public void attachRepository(Repository repository) {
        this.userService.attachRepository(repository);
    }
    
    /**
     * @param repository
     */
    @Override
    public void attachQueryOnlyRepository(Repository repository) {
        this.queryOnlyRepository = repository;
    }
}
