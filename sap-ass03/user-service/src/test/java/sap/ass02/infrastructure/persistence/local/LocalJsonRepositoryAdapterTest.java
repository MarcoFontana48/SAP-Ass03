package sap.ass02.infrastructure.persistence.local;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.UserDTO;
import sap.ddd.ReadWriteRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//! INTEGRATION tests
class LocalJsonRepositoryAdapterTest {
    private final String databaseFileName = "database";
    private final String databaseUserFileName = "user";
    private final String testUserId = "test_user";
    private ReadWriteRepository repo;

    @BeforeEach
    void setUp() {
        this.repo = new LocalJsonRepositoryAdapter();
        this.repo.init();
    }

    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(Path.of(this.databaseFileName + File.separator + this.databaseUserFileName + File.separator + this.testUserId + "_bis.json"));
            Files.deleteIfExists(Path.of(this.databaseFileName + File.separator + this.databaseUserFileName + File.separator + this.testUserId + ".json"));
            Files.deleteIfExists(Path.of(this.databaseFileName + File.separator + this.databaseUserFileName));
            Files.deleteIfExists(Path.of(this.databaseFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void initializesRepositoryCorrectly() {
        assertAll(
                () -> assertTrue(Files.exists(Path.of(this.databaseFileName)), "Database directory should exist"),
                () -> assertTrue(Files.exists(Path.of(this.databaseFileName + File.separator + this.databaseUserFileName)), "User directory should exist")
        );
    }

    @Test
    void insertsUsersCorrectly() {
        boolean insertUserFunctionResult;
        insertUserFunctionResult = this.repo.insertUser(new User(this.testUserId).toDTO());
        
        assertTrue(insertUserFunctionResult, "Couldn't insert user into db");
    }
    
    @Test
    void retrievesUserByIdCorrectly() {
        this.repo.insertUser(new User(this.testUserId).toDTO());
        var user = this.repo.getUserById(this.testUserId);
        assertTrue(user.isPresent(), "Couldn't retrieve user from db");
    }
    
    @Test
    void retrievesUserByIdReturnsEmptyOptionalWhenUserDoesNotExist() {
        var user = this.repo.getUserById(this.testUserId);
        assertTrue(user.isEmpty(), "Should return empty optional when user does not exist");
    }
    
    @Test
    void retrievesAllUsersCorrectly() {
        this.repo.insertUser(new User(this.testUserId).toDTO());
        this.repo.insertUser(new User(this.testUserId + "_bis").toDTO());
        List<UserDTO> users = new ArrayList<>();
        this.repo.getAllUsers().forEach(users::add);
        
        assertAll(
                () -> assertEquals(2, users.size(), "Should return 2 user"),
                () -> assertEquals(this.testUserId, users.getFirst().id(), "Should return the correct user"),
                () -> assertEquals(this.testUserId + "_bis", users.get(1).id(), "Should return the correct user")
        );
    }
    
    @Test
    void updatesUserCreditsCorrectly() {
        this.repo.insertUser(new User(this.testUserId).toDTO());
        var userBeforeUpdate = this.repo.getUserById(this.testUserId);
        this.repo.updateUserCredits(this.testUserId, 66);
        var userAfterUpdate = this.repo.getUserById(this.testUserId);
        if (userAfterUpdate.isPresent() && userBeforeUpdate.isPresent()) {
            assertAll(
                    () -> assertEquals(0, userBeforeUpdate.get().credit(), "Should update user credits correctly"),
                    () -> assertEquals(66, userAfterUpdate.get().credit(), "Should update user credits correctly")
            );
        } else {
            fail("Couldn't retrieve user from db");
        }
    }
}