package sap.ass02.infrastructure.persistence.mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.dto.*;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//! COMPONENT tests
class MongoRepositoryAdapterTest {
    private static final Logger LOGGER = LogManager.getLogger(MongoRepositoryAdapterTest.class);
    private static final int MINUTE = 60_000;
    private final String testRideId = "test_ride";
    private final String testUserId = "test_user";
    private final String testEBikeId = "test_ebike";
    private final AbstractMongoRepositoryAdapter repository = new MongoRepositoryAdapter();
    
    @BeforeAll
    static void setUpAll() throws InterruptedException, IOException {
        LOGGER.trace("Tearing down containers before testing, if they are running...");
        Process process = startProcess(new File(".."), "docker", "stop", "ride-mongo-db");
        process.waitFor();
        process = startProcess(new File(".."), "docker", "rm", "ride-mongo-db");
        process.waitFor();
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
    
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        startProcess(new File(".."), "docker", "run", "-d", "-p", "27017:27017", "--name", "user-mongo-db", "user-mongo-db");
        Thread.sleep(MINUTE / 2);
        this.repository.connect("localhost", "27017", "ebike", "root", "password");
    }
    
    //! already in use in 'setUp' method
//    @Test
//    void connectsToDatabase() {
//        assertDoesNotThrow(() -> this.repository.connect("localhost", "27017", "ebike", "root", "password"));
//    }
    
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        Process process = startProcess(new File(".."), "docker", "stop", "user-mongo-db");
        process.waitFor();
        process = startProcess(new File(".."), "docker", "rm", "user-mongo-db");
        process.waitFor();
    }
    
    @Test
    void retrievesRideCorrectly() {
        this.repository.insertRide(new RideDTO(
                Date.valueOf("2021-01-01"),
                Optional.of(Date.valueOf("2021-01-02")),
                new UserDTO(this.testUserId, 100),
                new EBikeDTO(this.testEBikeId, EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(0, 0), 0, 100),
                false,
                this.testRideId
        ));
        
        Optional<RideDTO> ride = this.repository.getRideById(this.testRideId);
        
        assertAll(
                () -> assertTrue(ride.isPresent(), "Ride should be present"),
                () -> assertEquals(this.testRideId, ride.get().id(), "Ride id should be correct"),
                () -> assertEquals(Date.valueOf("2021-01-01"), ride.get().startedDate(), "Ride start date should be correct"),
                () -> assertEquals(Date.valueOf("2021-01-02"), ride.get().endDate().get(), "Ride end date should be correct"),
                () -> assertEquals(this.testUserId, ride.get().user().id(), "Ride user id should be correct"),
                () -> assertEquals(this.testEBikeId, ride.get().ebike().id(), "Ride ebike id should be correct")
        );
    }
}