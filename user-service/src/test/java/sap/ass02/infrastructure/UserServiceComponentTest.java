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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//! COMPONENT tests
public class UserServiceComponentTest {
    private final UserDTO user1 = new UserDTO("1", 0);
    private final UserDTO user2 = new UserDTO("2", 0);
    private UserService userService;
    
    @BeforeEach
    public void setUp() {
        Repository repository = mock(Repository.class);
        when(repository.getUserById(this.user1.id())).thenReturn(Optional.of(this.user1));
        when(repository.getAllUsers()).thenReturn(List.of(this.user1, this.user2));
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
}