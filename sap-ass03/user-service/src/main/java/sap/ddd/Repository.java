package sap.ddd;

import sap.ass02.domain.dto.UserDTO;

import java.util.Optional;

public interface Repository {
    /**
     * Initializes the repository
     */
    void init();

    /**
     * Inserts the given user inside the repository
     *
     * @param user
     */
    boolean insertUser(UserDTO user);

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

    /**
     * Sets user credit to the values passed as argument
     *
     * @param userID
     * @param credits
     * @return
     */
    boolean updateUserCredits(String userID, int credits);
}
