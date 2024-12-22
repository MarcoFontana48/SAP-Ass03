package sap.ass02.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.application.UserService;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.UserDTO;
import sap.ddd.Repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

//! COMPONENT tests
public class UserServiceComponentTest {
    private final UserDTO user1 = new UserDTO("1", 0);
    private final UserDTO user2 = new UserDTO("2", 0);
    private UserService userService;
    
    @BeforeEach
    public void setUp() {
        Repository repository = new RepositoryMock();
        this.userService = new UserService();
        this.userService.attachRepository(repository);
    }
    
    @Test
    public void getUser() {
        User actualUser = this.userService.getUser(this.user1.id());
        User expectedUser = new User(this.user1.id(), this.user1.credit());
        assertEquals(expectedUser, actualUser);
    }
    
    @Test
    public void getAllUsers() {
        Iterable<User> actualUsers = this.userService.getUsers();
        var expectedUsers = List.of(new User(this.user1.id(), this.user1.credit()), new User(this.user2.id(), this.user2.credit()));
        assertEquals(expectedUsers, actualUsers);
    }
    
    //! NOTE: This is a mock class for the Repository interface, i tried to use Mockito but unfortunately it has a
    //! dependency on 'Byte Buddy' that currently does not support JVM 23, so i had to create a mock class for the
    //! Repository interface. I leave the Mockito code commented below.
    private class RepositoryMock implements Repository {
        @Override
        public void init() {
        
        }
        
        @Override
        public boolean insertUser(UserDTO user) {
            return false;
        }
        
        @Override
        public Optional<UserDTO> getUserById(String id) {
            if (id.equals(UserServiceComponentTest.this.user1.id())) {
                return Optional.of(UserServiceComponentTest.this.user1);
            } else {
                return Optional.empty();
            }
        }
        
        @Override
        public List<UserDTO> getAllUsers() {
            return List.of(UserServiceComponentTest.this.user1, UserServiceComponentTest.this.user2);
        }
        
        @Override
        public boolean updateUserCredits(String userID, int credits) {
            return false;
        }
    }
}

/*
package sap.ass02.infrastructure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sap.ass02.application.UserService;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.UserDTO;
import sap.ddd.Repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserServiceComponentTest {
    private static final Logger LOGGER = LogManager.getLogger(UserServiceComponentTest.class);
    private final UserDTO user1 = new UserDTO("1", 0);
    private final UserDTO user2 = new UserDTO("2", 0);

    private Repository repository;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        repository = Mockito.mock(Repository.class);
        userService = new UserService();
        userService.attachRepository(repository);

        when(repository.getUserById(user1.id())).thenReturn(Optional.of(user1));
        when(repository.getAllUsers()).thenReturn(List.of(user1, user2));
    }

    @Test
    public void getUser() {
        User actualUser = userService.getUser(user1.id());
        User expectedUser = new User(user1.id(), user1.credit());
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void getAllUsers() {
        Iterable<User> actualUsers = userService.getUsers();
        var expectedUsers = List.of(new User(user1.id(), user1.credit()), new User(user2.id(), user2.credit()));
        assertEquals(expectedUsers, actualUsers);
    }
}

 */