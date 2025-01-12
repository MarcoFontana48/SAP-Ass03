package sap.ass02.domain.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.DTOUtils;
import sap.ass02.domain.dto.UserDTO;
import sap.ddd.ReadWriteRepository;
import sap.ddd.Service;

import java.util.ArrayList;
import java.util.List;

public final class UserService implements Service {
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);
    private ReadWriteRepository repository;
    
    @Override
    public boolean addUser(String userId, int credits) {
        User user = new User(userId, credits);
        LOGGER.trace("Adding user with id '{}' and credits '{}'", userId, credits);
        this.repository.insertUser(user.toDTO());
        return true;
    }
    
    @Override
    public boolean addUser(String userId, int credits, double xLocation, double yLocation) {
        User user = new User(userId, credits, xLocation, yLocation);
        LOGGER.trace("Adding user with id '{}', credits '{}', xLocation '{}' and yLocation '{}'", userId, credits, xLocation, yLocation);
        this.repository.insertUser(user.toDTO());
        return true;
    }
    
    @Override
    public User getUser(String userId) {
        LOGGER.trace("Getting user with id '{}'", userId);
        var user = this.repository.getUserById(userId);
        return user.map(userDTO -> new User(userDTO.id(), userDTO.credit())).orElse(null);
    }
    
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
    
    @Override
    public Iterable<User> getUsers() {
        Iterable<UserDTO> allUsersDTO = this.repository.getAllUsers();
        List<User> users = new ArrayList<>();
        allUsersDTO.forEach(userDTO -> users.add(DTOUtils.toUser(userDTO)));
        return users;
    }
    
    @Override
    public void attachRepository(ReadWriteRepository repository) {
        this.repository = repository;
    }
}
