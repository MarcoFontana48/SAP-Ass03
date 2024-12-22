package sap.ass02.infrastructure.presentation.view.property;

import sap.ass02.domain.User;

/**
 * Interface for classes that can visualize users
 */
public interface UserVisualizer {
    /**
     * Add a user to the view
     * @param user the user to add
     */
    void addUserToShow(User user);
}
