package sap.ass02.infrastructure.persistence.mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.dto.UserDTO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
 * NOTE: if the image is not available on docker-hub, you can build it yourself by running the following command:
 * `docker build -t user-mongo-db -f docker-db/Dockerfile .`
 * THERE'S NO NEED TO RUN THE `docker run` COMMAND, THE TESTS WILL DO IT FOR YOU
 */
//! INTEGRATION tests
class MongoRepositoryAdapterTest {
    private static final Logger LOGGER = LogManager.getLogger(MongoRepositoryAdapterTest.class);
    private static final int MINUTE = 60_000;
    private final AbstractMongoRepositoryAdapter repository = new MongoRepositoryAdapter();
    
    @BeforeAll
    static void setUpAll() throws InterruptedException, IOException {
        LOGGER.trace("Tearing down containers before testing, if they are running...");
        Process process = startProcess(new File(".."), "docker", "stop", "user-mongo-db");
        process.waitFor();
        process = startProcess(new File(".."), "docker", "rm", "user-mongo-db");
        process.waitFor();
    }
    
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        startProcess(new File(".."), "docker", "run", "-d", "-p", "27017:27017", "--name", "user-mongo-db", "user-mongo-db");
        Thread.sleep(MINUTE/2);
        this.repository.connect("localhost", "27017", "ebike", "root", "password");
    }
    
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        Process process = startProcess(new File(".."), "docker", "stop", "user-mongo-db");
        process.waitFor();
        process = startProcess(new File(".."), "docker", "rm", "user-mongo-db");
        process.waitFor();
    }
    
    //! already in use in 'setUp' method
//    @Test
//    void connectsToDatabase() {
//        assertDoesNotThrow(() -> this.repository.connect("localhost", "27017", "ebike", "root", "password"));
//    }
    
    @Test
    void insertsUser() {
        var userDTO = new UserDTO("1", 100, 0, 0);
        this.repository.insertUser(userDTO);
        Optional<UserDTO> ebikeById = this.repository.getUserById("1");
        assertAll(
                () -> assertTrue(ebikeById.isPresent()),
                () -> assertEquals(userDTO.id(), ebikeById.get().id()),
                () -> assertEquals(userDTO.credit(), ebikeById.get().credit())
        );
    }
    
    @Test
    void updatesUser() {
        var userDTO = new UserDTO("1", 100, 0, 0);
        this.repository.insertUser(userDTO);
        var updatedUser = new UserDTO("1", 50, 0, 0);
        this.repository.updateUserCredits(updatedUser.id(), updatedUser.credit());
        Optional<UserDTO> userAfterUpdate = this.repository.getUserById("1");
        assertAll(
                () -> assertTrue(userAfterUpdate.isPresent()),
                () -> assertEquals(updatedUser.id(), userAfterUpdate.get().id()),
                () -> assertEquals(updatedUser.credit(), userAfterUpdate.get().credit())
        );
    }
    
    @Test
    void retrievesAllUsers() {
        var user1 = new UserDTO("1", 100, 0, 0);
        var user2 = new UserDTO("2", 50, 0, 0);
        this.repository.insertUser(user1);
        this.repository.insertUser(user2);
        Iterable<UserDTO> users = this.repository.getAllUsers();
        assertAll(
                () -> assertNotNull(users),
                () -> assertEquals(2, users.spliterator().getExactSizeIfKnown())
        );
    }
    
    private static Process startProcess(File workDir, String... cmdLine) throws IOException {
        LOGGER.trace("Starting process on dir '{}', with command line: '{}'", workDir, Arrays.toString(cmdLine));
        var prefix = MongoRepositoryAdapter.class.getName() + "-" + Arrays.hashCode(cmdLine);
        var stdOut = File.createTempFile(prefix + "-stdout", ".txt");
        stdOut.deleteOnExit();
        var stdErr = File.createTempFile(prefix + "-stderr", ".txt");
        stdErr.deleteOnExit();
        return new ProcessBuilder(cmdLine)
                .redirectOutput(ProcessBuilder.Redirect.to(stdOut))
                .redirectError(ProcessBuilder.Redirect.to(stdErr))
                .directory(workDir)
                .start();
    }
}