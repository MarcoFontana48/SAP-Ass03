package sap.ass02.domain.dto;

import sap.ass02.domain.User;

public class DTOUtils {
    public static User toUser(UserDTO userDTO) {
        User user = new User(userDTO.id());
        user.rechargeCredit(userDTO.credit());
        return user;
    }
}
