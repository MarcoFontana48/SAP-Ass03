package sap.ass02.infrastructure.persistence.sql;

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
 * `docker build -t user-sql-db -f docker-db/Dockerfile .`
 * THERE'S NO NEED TO RUN THE `docker run` COMMAND, THE TESTS WILL DO IT FOR YOU
 */
//! INTEGRATION tests
class SQLRepositoryAdapterTest {
    private static final Logger LOGGER = LogManager.getLogger(SQLRepositoryAdapterTest.class);
    private static final int MINUTE = 60_000;
    private final AbstractSQLRepositoryAdapter repository = new SQLRepositoryAdapter();
    
    @BeforeAll
    static void setUpAll() throws InterruptedException, IOException {
        LOGGER.trace("Tearing down containers before testing, if they are running...");
        Process process = startProcess(new File(".."), "docker", "stop", "user-sql-db");
        process.waitFor();
        process = startProcess(new File(".."), "docker", "rm", "user-sql-db");
        process.waitFor();
    }
    
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        startProcess(new File(".."), "docker", "run", "-d", "-p", "3306:3306", "--name", "user-sql-db", "user-sql-db");
        Thread.sleep(MINUTE / 2);
        this.repository.connect("localhost", "3306", "ebike", "root", "password");
    }
    
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        Process process = startProcess(new File(".."), "docker", "stop", "user-sql-db");
        process.waitFor();
        process = startProcess(new File(".."), "docker", "rm", "user-sql-db");
        process.waitFor();
    }
    
    //! already in use in 'setUp' method
//    @Test
//    void connectsToDatabase() {
//        assertDoesNotThrow(() -> this.repository.connect("localhost", "3306", "ebike", "root", "password"));
//    }
    
    @Test
    void throwsExceptionWhenInsertingUserWithInvalidId() {
        var user = new UserDTO("not_a_valid_id", 100);
        assertThrows(Exception.class, () -> this.repository.insertUser(user));
    }
    
    @Test
    void insertsEBike() {
        var userDTO = new UserDTO("1", 100);
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
        var userDTO = new UserDTO("1", 100);
        this.repository.insertUser(userDTO);
        var updatedUser = new UserDTO("1", 50);
        this.repository.updateUserCredits(updatedUser.id(), updatedUser.credit());
        Optional<UserDTO> ebikeById = this.repository.getUserById("1");
        assertAll(
                () -> assertTrue(ebikeById.isPresent()),
                () -> assertEquals(updatedUser.id(), ebikeById.get().id()),
                () -> assertEquals(updatedUser.credit(), ebikeById.get().credit())
        );
    }
    
    @Test
    void retrievesAllEBikes() {
        var user1 = new UserDTO("1", 100);
        var user2 = new UserDTO("2", 50);
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
        var prefix = SQLRepositoryAdapterTest.class.getName() + "-" + Arrays.hashCode(cmdLine);
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