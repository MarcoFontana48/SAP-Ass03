package sap.ass02.infrastructure.persistence.sql;

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
class SQLRepositoryAdapterTest {
    private static final Logger LOGGER = LogManager.getLogger(SQLRepositoryAdapterTest.class);
    private static final int MINUTE = 60_000;
    private final AbstractSQLRepositoryAdapter repository = new SQLRepositoryAdapter();
    
    @BeforeAll
    static void setUpAll() throws InterruptedException, IOException {
        LOGGER.trace("Tearing down containers before testing, if they are running...");
        Process process = startProcess(new File(".."), "docker", "stop", "ride-sql-db");
        process.waitFor();
        process = startProcess(new File(".."), "docker", "rm", "ride-sql-db");
        process.waitFor();
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
    
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        startProcess(new File(".."), "docker", "run", "-d", "-p", "3306:3306", "--name", "ride-sql-db", "ride-sql-db");
        Thread.sleep(MINUTE / 2);
        this.repository.connect("localhost", "3306", "ebike", "root", "password");
    }
    
    //! already in use in 'setUp' method
//    @Test
//    void connectsToDatabase() {
//        assertDoesNotThrow(() -> this.repository.connect("localhost", "3306", "ebike", "root", "password"));
//    }
    
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        Process process = startProcess(new File(".."), "docker", "stop", "ride-sql-db");
        process.waitFor();
        process = startProcess(new File(".."), "docker", "rm", "ride-sql-db");
        process.waitFor();
    }
    
    @Test
    void insertsRide() {
        UserDTO user = new UserDTO("user_id", -1);
        EBikeDTO ebike = new EBikeDTO("ebike_id", EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(0, 0), 0, 0);
        RideDTO ride = new RideDTO(
                Date.valueOf("2021-01-01"),
                Optional.ofNullable(Date.valueOf("2021-01-02")),
                user,
                ebike,
                false,
                "1"
        );
        this.repository.insertRide(ride);
        this.repository.getRideById("1").ifPresentOrElse(
                r -> assertAll(
                        () -> assertEquals(ride.startedDate(), r.startedDate()),
                        () -> assertEquals(ride.endDate(), r.endDate()),
                        () -> assertEquals(ride.user(), r.user()),
                        () -> assertEquals(ride.ebike(), r.ebike()),
                        () -> assertEquals(ride.ongoing(), r.ongoing()),
                        () -> assertEquals(ride.id(), r.id())
                ),
                () -> fail("Ride not found")
        );
    }
}