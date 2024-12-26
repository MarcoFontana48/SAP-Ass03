package sap.ddd;

import sap.ass02.domain.dto.UserDTO;

import java.util.Optional;

public interface ReadOnlyRepository {
    /**
     * Retrieves the user from the repository given its id
     *
     * @param userId
     * @return Optionally found user
     */
    Optional<UserDTO> getUserById(final String userId);
    
    /**
     * Retrieves all the users stored inside the repository
     *
     * @return Iterable of found users
     */
    Iterable<UserDTO> getAllUsers();
}
