package sap.ddd;

import sap.ass02.domain.User;

public interface Service {
    /**
     * Add a user to the repository
     * @param userId the user id
     * @param credits the user credits
     */
    boolean addUser(final String userId, final int credits);
    
    /**
     * Get a user from the repository
     * @param userId the user id
     * @return the user
     */
    User getUser(String userId);
    
    /**
     * Update the user credits in the repository given the user
     * @param userId the user id
     * @param credits the user credits
     */
    boolean updateUserCredits(String userId, int credits);
    
    /**
     * Get all users from the repository
     * @return the users
     */
    Iterable<User> getUsers();
    
    /**
     * Attach a repository to the service
     * @param repository the repository
     */
    void attachRepository(Repository repository);
}
