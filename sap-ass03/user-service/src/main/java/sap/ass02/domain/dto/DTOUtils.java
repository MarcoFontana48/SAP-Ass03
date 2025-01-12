package sap.ass02.domain.dto;

import sap.ass02.domain.User;

/**
 * Utility class for DTOs.
 */
public class DTOUtils {
    /**
     * Converts a userDTO to a user.
     * @param userDTO the user DTO
     * @return the user
     */
    public static User toUser(UserDTO userDTO) {
        User user = new User(userDTO.id());
        user.rechargeCredit(userDTO.credit());
        return user;
    }
}
