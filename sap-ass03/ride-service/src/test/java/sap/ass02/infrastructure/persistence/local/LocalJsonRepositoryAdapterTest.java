package sap.ass02.infrastructure.persistence.local;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.dto.*;
import sap.ddd.ReadWriteRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//! COMPONENT tests
class LocalJsonRepositoryAdapterTest {
    private final String databaseFileName = "database";
    private final String databaseRideFileName = "rides";
    private final String testRideId = "test_ride";
    private final String testUserId = "test_user";
    private final String testEBikeId = "test_ebike";
    private ReadWriteRepository repo;
    private final UserDTO user = new UserDTO(this.testUserId, 100, 0, 0);
    private final EBikeDTO ebike = new EBikeDTO(this.testEBikeId, BikeStateDTO.AVAILABLE, new P2dDTO(0, 0), new V2dDTO(0, 0), 0, 100);
    
    @BeforeEach
    void setUp() {
        this.repo = new LocalJsonRepositoryAdapter();
        this.repo.init();
    }
    
    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(Path.of(this.databaseFileName + File.separator + this.databaseRideFileName + File.separator + this.testRideId + "_bis.json"));
            Files.deleteIfExists(Path.of(this.databaseFileName + File.separator + this.databaseRideFileName + File.separator + this.testRideId + ".json"));
            Files.deleteIfExists(Path.of(this.databaseFileName + File.separator + this.databaseRideFileName));
            Files.deleteIfExists(Path.of(this.databaseFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    void initializesRepositoryCorrectly() {
        assertAll(
                () -> assertTrue(Files.exists(Path.of(this.databaseFileName)), "Database directory should exist"),
                () -> assertTrue(Files.exists(Path.of(this.databaseFileName + File.separator + this.databaseRideFileName)), "Ride directory should exist")
        );
    }
    
    @Test
    void insertsRideCorrectly() {
        this.repo.insertRide(new RideDTO(
                Date.valueOf("2021-01-01"),
                Optional.of(Date.valueOf("2021-01-02")),
                this.user,
                this.ebike,
                false,
                this.testRideId
        ));
        
        assertAll(
                () -> assertTrue(Files.exists(Path.of(this.databaseFileName + File.separator + this.databaseRideFileName + File.separator + this.testRideId + ".json")), "Ride file should exist"
                ));
    }
    
    @Test
    void retrievesRideCorrectly() {
        this.repo.insertRide(new RideDTO(
                Date.valueOf("2021-01-01"),
                Optional.of(Date.valueOf("2021-01-02")),
                this.user,
                this.ebike,
                false,
                this.testRideId
        ));
        
        Optional<RideDTO> ride = this.repo.getRideById(this.testRideId);
        
        assertAll(
                () -> assertTrue(ride.isPresent(), "Ride should be present"),
                () -> assertEquals(this.testRideId, ride.get().id(), "Ride id should be correct"),
                () -> assertEquals(Date.valueOf("2021-01-01"), ride.get().startedDate(), "Ride start date should be correct"),
                () -> assertEquals(Date.valueOf("2021-01-02"), ride.get().endDate().get(), "Ride end date should be correct"),
                () -> assertEquals(this.user, ride.get().user(), "stored user should be correct"),
                () -> assertEquals(this.ebike, ride.get().ebike(), "stored ebike should be correct")
        );
    }
}