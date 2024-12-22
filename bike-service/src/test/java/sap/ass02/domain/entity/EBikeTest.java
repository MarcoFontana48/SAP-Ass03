package sap.ass02.domain.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.EBike;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.utils.JsonFieldKey;

//! UNIT test
import static org.junit.jupiter.api.Assertions.*;
class EBikeTest {
    
    @BeforeEach
    void setUp() {
    }
    
    @AfterEach
    void tearDown() {
    }
    
    @Test
    void testConstructor() {
        EBike eBike = new EBike("id");
        assertAll(
                () -> assertEquals("id", eBike.getId()),
                () -> assertEquals(EBike.EBikeState.AVAILABLE, eBike.getState()),
                () -> assertEquals(100, eBike.getBatteryLevel()),
                () -> assertEquals(0, eBike.getSpeed()),
                () -> assertEquals(0, eBike.getSpeed())
        );
    }
    
    @Test
    void rechargesBattery() {
        EBike eBike = new EBike("id");
        eBike.rechargeBattery();
        assertEquals(100, eBike.getBatteryLevel());
    }
    
    @Test
    void decreaseBatteryLevel() {
        EBike eBike = new EBike("id");
        eBike.rechargeBattery();
        eBike.decreaseBatteryLevel(10);
        assertEquals(90, eBike.getBatteryLevel());
    }
    
    @Test
    void decreaseBatteryLevelOverMinimum() {
        EBike eBike = new EBike("id");
        eBike.decreaseBatteryLevel(110);
        assertEquals(0, eBike.getBatteryLevel());
    }
    
    @Test
    void changesStateToMaintenanceWhenBatteryLowerThanMinimum() {
        EBike eBike = new EBike("id");
        eBike.rechargeBattery();
        eBike.decreaseBatteryLevel(110);
        assertAll(
                () -> assertEquals(0, eBike.getBatteryLevel()),
                () -> assertEquals(EBike.EBikeState.MAINTENANCE, eBike.getState())
        );
    }
    
    @Test
    void convertsToDTO() {
        EBike eBike = new EBike("id");
        EBikeDTO eBikeDTO = eBike.toDTO();
        assertAll(
                () -> assertEquals("id", eBikeDTO.id()),
                () -> assertEquals(EBikeDTO.EBikeStateDTO.AVAILABLE, eBikeDTO.state()),
                () -> assertEquals(100, eBikeDTO.batteryLevel()),
                () -> assertEquals(0, eBikeDTO.speed())
        );
    }
    
    @Test
    void convertsToJson() {
        EBike eBike = new EBike("id");
        String json = eBike.toJsonString();
        assertAll(
                () -> assertTrue(json.contains("\"" + JsonFieldKey.EBIKE_ID_KEY + "\":\"id\"")),
                () -> assertTrue(json.contains("\"" + JsonFieldKey.EBIKE_STATE_KEY + "\":\"AVAILABLE\"")),
                () -> assertTrue(json.contains("\"" + JsonFieldKey.EBIKE_X_LOCATION_KEY + "\":0")),
                () -> assertTrue(json.contains("\"" + JsonFieldKey.EBIKE_Y_LOCATION_KEY + "\":0")),
                () -> assertTrue(json.contains("\"" + JsonFieldKey.EBIKE_X_DIRECTION_KEY + "\":1")),
                () -> assertTrue(json.contains("\"" + JsonFieldKey.EBIKE_Y_DIRECTION_KEY + "\":0")),
                () -> assertTrue(json.contains("\"" + JsonFieldKey.EBIKE_SPEED_KEY + "\":0")),
                () -> assertTrue(json.contains("\"" + JsonFieldKey.EBIKE_BATTERY_KEY + "\":100"))
        );
    }
}