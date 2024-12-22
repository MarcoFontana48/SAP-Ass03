package sap.ass02.infrastructure.persistence.sql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
 * NOTE: if the image is not available on docker-hub, you can build it yourself by running the following command:
 * `docker build -t bike-db -f docker-db/Dockerfile .`
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
        Process process = startProcess(new File(".."), "docker", "stop", "bike-db");
        process.waitFor();
        process = startProcess(new File(".."), "docker", "rm", "bike-db");
        process.waitFor();
    }
    
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        startProcess(new File(".."), "docker", "run", "-d", "-p", "3306:3306", "--name", "bike-sql-db", "bike-sql-db");
        Thread.sleep(MINUTE);
        this.repository.connect("localhost", "3306", "ebike", "root", "password");
    }
    
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        Process process = startProcess(new File(".."), "docker", "stop", "bike-sql-db");
        process.waitFor();
        process = startProcess(new File(".."), "docker", "rm", "bike-sql-db");
        process.waitFor();
    }
    
    //! already in use in 'setUp' method
//    @Test
//    void connectsToDatabase() {
//        assertDoesNotThrow(() -> this.repository.connect("localhost", "3306", "ebike", "root", "password"));
//    }
    
    @Test
    void throwsExceptionWhenInsertingEBikeWithInvalidId() {
        var eBike = new EBikeDTO("not_a_valid_id", EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(1, 0), 1, 100);
        assertThrows(Exception.class, () -> this.repository.insertEbike(eBike));
    }
    
    @Test
    void insertsEBike() {
        var eBike = new EBikeDTO("4", EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(1, 0), 1, 100);
        this.repository.insertEbike(eBike);
        Optional<EBikeDTO> ebikeById = this.repository.getEbikeById("4");
        assertAll(
                () -> assertTrue(ebikeById.isPresent()),
                () -> assertEquals(eBike.id(), ebikeById.get().id()),
                () -> assertEquals(eBike.state(), ebikeById.get().state()),
                () -> assertEquals(eBike.location(), ebikeById.get().location()),
                () -> assertEquals(eBike.direction(), ebikeById.get().direction()),
                () -> assertEquals(eBike.speed(), ebikeById.get().speed()),
                () -> assertEquals(eBike.batteryLevel(), ebikeById.get().batteryLevel())
        );
    }
    
    @Test
    void updatesEBike() {
        var eBike = new EBikeDTO("5", EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(1, 0), 1, 100);
        this.repository.insertEbike(eBike);
        var updatedEBike = new EBikeDTO("5", EBikeDTO.EBikeStateDTO.MAINTENANCE, new P2dDTO(1, 1), new V2dDTO(0, 1), 2, 50);
        this.repository.updateEBike(updatedEBike);
        Optional<EBikeDTO> ebikeById = this.repository.getEbikeById("5");
        assertAll(
                () -> assertTrue(ebikeById.isPresent()),
                () -> assertEquals(updatedEBike.id(), ebikeById.get().id()),
                () -> assertEquals(updatedEBike.state(), ebikeById.get().state()),
                () -> assertEquals(updatedEBike.location(), ebikeById.get().location()),
                () -> assertEquals(updatedEBike.direction(), ebikeById.get().direction()),
                () -> assertEquals(updatedEBike.speed(), ebikeById.get().speed()),
                () -> assertEquals(updatedEBike.batteryLevel(), ebikeById.get().batteryLevel())
        );
    }
    
    @Test
    void retrievesAllEBikes() {
        var eBike1 = new EBikeDTO("10", EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(1, 0), 1, 100);
        var eBike2 = new EBikeDTO("9", EBikeDTO.EBikeStateDTO.MAINTENANCE, new P2dDTO(1, 1), new V2dDTO(0, 1), 2, 50);
        this.repository.insertEbike(eBike1);
        this.repository.insertEbike(eBike2);
        Iterable<EBikeDTO> ebikes = this.repository.getAllEBikes();
        assertAll(
                () -> assertNotNull(ebikes),
                () -> assertEquals(2, ebikes.spliterator().getExactSizeIfKnown())
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
