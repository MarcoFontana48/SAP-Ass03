package sap.ass02.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.DTOUtils;
import sap.ass02.domain.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for the user micro-service.
 */
public final class UserService implements Service {
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);
    private Repository repository;
    
    /**
     * Adds a user to the repository.
     * @param userId the id of the user to be added
     * @param credits the credits of the user to be added
     * @return true if the user was added successfully
     */
    @Override
    public boolean addUser(String userId, int credits) {
        User user = new User(userId, credits);
        LOGGER.trace("Adding user with id '{}' and credits '{}'", userId, credits);
        this.repository.insertUser(user.toDTO());
        return true;
    }
    
    /**
     * Adds a user to the repository.
     * @param userId the id of the user to be added
     * @param credits the credits of the user to be added
     * @param xLocation the x location of the user to be added
     * @param yLocation the y location of the user to be added
     * @return true if the user was added successfully
     */
    @Override
    public boolean addUser(String userId, int credits, double xLocation, double yLocation) {
        User user = new User(userId, credits, xLocation, yLocation);
        LOGGER.trace("Adding user with id '{}', credits '{}', xLocation '{}' and yLocation '{}'", userId, credits, xLocation, yLocation);
        this.repository.insertUser(user.toDTO());
        return true;
    }
    
    /**
     * Gets a user from the repository.
     * @param userId the id of the user to be retrieved
     * @return the user
     */
    @Override
    public User getUser(String userId) {
        LOGGER.trace("Getting user with id '{}'", userId);
        var user = this.repository.getUserById(userId);
        return user.map(userDTO -> new User(userDTO.id(), userDTO.credit())).orElse(null);
    }
    
    /**
     * Updates a user in the repository.
     * @param userId the user to be updated
     * @return true if the user was updated successfully
     */
    @Override
    public boolean updateUserCredits(String userId, int credits) {
        LOGGER.trace("Updating user with id '{}' to credits '{}'", userId, credits);
        var user = this.repository.getUserById(userId);
        if (user.isEmpty()) {
            return false;
        }
        this.repository.updateUserCredits(userId, credits);
        return true;
    }
    
    /**
     * Gets all users from the repository.
     * @return all users as an iterable
     */
    @Override
    public Iterable<User> getUsers() {
        Iterable<UserDTO> allUsersDTO = this.repository.getAllUsers();
        List<User> users = new ArrayList<>();
        allUsersDTO.forEach(userDTO -> users.add(DTOUtils.toUser(userDTO)));
        return users;
    }
    
    /**
     * Attaches a repository to the service.
     * @param repository the repository
     */
    @Override
    public void attachRepository(Repository repository) {
        this.repository = repository;
    }
}
