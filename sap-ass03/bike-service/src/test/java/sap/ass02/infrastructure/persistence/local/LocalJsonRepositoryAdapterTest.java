package sap.ass02.infrastructure.persistence.local;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.AbstractBike;
import sap.ass02.domain.EBike;
import sap.ass02.domain.P2d;
import sap.ass02.domain.V2d;
import sap.ass02.domain.dto.BikeStateDTO;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ass02.application.Repository;

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
    private final String databaseEbikeFileName = "ebike";
    private final String testEbikeId = "test_ebike";
    private Repository repo;
    
    @BeforeEach
    void setUp() {
        this.repo = new LocalJsonRepositoryAdaptor();
        this.repo.init();
    }
    
    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(Path.of(this.databaseFileName + File.separator + this.databaseEbikeFileName + File.separator + this.testEbikeId + "_bis.json"));
            Files.deleteIfExists(Path.of(this.databaseFileName + File.separator + this.databaseEbikeFileName + File.separator + this.testEbikeId + ".json"));
            Files.deleteIfExists(Path.of(this.databaseFileName + File.separator + this.databaseEbikeFileName));
            Files.deleteIfExists(Path.of(this.databaseFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    void insertsEbike() {
        boolean insertUserFunctionResult;
        insertUserFunctionResult = this.repo.insertEbike(new EBike(this.testEbikeId).toDTO());
        
        assertTrue(insertUserFunctionResult, "Couldn't insert user into db");
    }
    
    @Test
    void retrievesEbikeByIdCorrectly() {
        this.repo.insertEbike(new EBike(this.testEbikeId).toDTO());
        var user = this.repo.getEbikeById(this.testEbikeId);
        assertTrue(user.isPresent(), "Couldn't retrieve user from db");
    }
    
    @Test
    void retrievesEbikeByIdReturnsEmptyOptionalWhenEbikeDoesNotExist() {
        var user = this.repo.getEbikeById(this.testEbikeId);
        assertTrue(user.isEmpty(), "Should return empty optional when user does not exist");
    }
    
    @Test
    void retrievesAllEbikesCorrectly() {
        this.repo.insertEbike(new EBike(this.testEbikeId).toDTO());
        this.repo.insertEbike(new EBike(this.testEbikeId + "_bis").toDTO());
        List<EBikeDTO> ebikes = new ArrayList<>();
        this.repo.getAllEBikes().forEach(ebikes::add);
        
        assertAll(
                () -> assertEquals(2, ebikes.size(), "Should return 2 user")
        );
    }
    
    @Test
    void updatesEbikeBatteryCorrectly() {
        this.repo.insertEbike(new EBike(this.testEbikeId).toDTO());
        var ebikeBeforeUpdate = this.repo.getEbikeById(this.testEbikeId);
        this.repo.updateEBike(new EBike(this.testEbikeId, AbstractBike.BikeState.MAINTENANCE, new P2d(1,2), new V2d(3,4), 9, 5).toDTO());
        var ebikeAfterUpdate = this.repo.getEbikeById(this.testEbikeId);
        if (ebikeAfterUpdate.isPresent() && ebikeBeforeUpdate.isPresent()) {
            assertAll(
                    () -> assertEquals(ebikeBeforeUpdate.get().id(), ebikeAfterUpdate.get().id(), "Should update user credits correctly"),
                    () -> assertEquals(BikeStateDTO.MAINTENANCE, ebikeAfterUpdate.get().state(), "Should update user credits correctly"),
                    () -> assertEquals(new P2dDTO(1,2), ebikeAfterUpdate.get().location(), "Should update user credits correctly"),
                    () -> assertEquals(new V2dDTO(3,4), ebikeAfterUpdate.get().direction(), "Should update user credits correctly"),
                    () -> assertEquals(9, ebikeAfterUpdate.get().speed(), "Should update user credits correctly"),
                    () -> assertEquals(5, ebikeAfterUpdate.get().batteryLevel(), "Should update user credits correctly")
            );
        } else {
            fail("Couldn't retrieve user from db");
        }
    }
}