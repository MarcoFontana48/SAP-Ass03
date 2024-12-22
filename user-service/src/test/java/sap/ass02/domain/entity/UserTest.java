package sap.ass02.domain.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.User;
import sap.ass02.domain.dto.UserDTO;
import sap.ass02.domain.utils.JsonFieldKey;

import static org.junit.jupiter.api.Assertions.*;

//! UNIT test
class UserTest {
    private static final int USER_MAX_CREDIT = 100;
    
    @BeforeEach
    void setUp() {
    }
    
    @AfterEach
    void tearDown() {
    }
    
    @Test
    void createsUser() {
        User user = new User("id", 10);
        assertAll(
                () -> assertEquals("id", user.getId()),
                () -> assertEquals(10, user.getCredit())
        );
    }
    
    @Test
    void createsUserWithNegativeCredit() {
        User user = new User("id", -10);
        assertAll(
                () -> assertEquals("id", user.getId()),
                () -> assertEquals(0, user.getCredit())
        );
    }
    
    @Test
    void createsUserWithExceedingCredit() {
        User user = new User("id", Integer.MAX_VALUE);
        assertAll(
                () -> assertEquals("id", user.getId()),
                () -> assertEquals(USER_MAX_CREDIT, user.getCredit())
        );
    }
    
    @Test
    void convertsToDTO() {
        User user = new User("id", 10);
        UserDTO userDTO = user.toDTO();
        assertAll(
                () -> assertEquals("id", userDTO.id()),
                () -> assertEquals(10, userDTO.credit())
        );
    }
    
    @Test
    void convertsToJSON() {
        User user = new User("id", 10);
        String json = user.toJsonString();
        assertEquals("{\"" + JsonFieldKey.USER_ID_KEY + "\":\"id\",\"" + JsonFieldKey.USER_CREDIT_KEY + "\":10}", json);
    }
    
    @Test
    void rechargesCredit() {
        User user = new User("id", 10);
        user.rechargeCredit(10);
        assertEquals(20, user.getCredit());
    }
    
    @Test
    void rechargesCreditWithNegativeAmount() {
        User user = new User("id", 10);
        user.rechargeCredit(-10);
        assertEquals(10, user.getCredit());
    }
    
    @Test
    void rechargesCreditWithExceedingAmount() {
        User user = new User("id", 10);
        user.rechargeCredit(USER_MAX_CREDIT + 10);
        assertEquals(USER_MAX_CREDIT, user.getCredit());
    }
    
    @Test
    void decreasesCredit() {
        User user = new User("id", 10);
        user.decreaseCredit(5);
        assertEquals(5, user.getCredit());
    }
    
    @Test
    void decreasesCreditWithNegativeAmount() {
        User user = new User("id", 10);
        user.decreaseCredit(-5);
        assertEquals(10, user.getCredit());
    }
    
    @Test
    void decreasesCreditWithExceedingAmount() {
        User user = new User("id", 10);
        user.decreaseCredit(Integer.MAX_VALUE);
        assertEquals(0, user.getCredit());
    }
}