package sap.ass02.infrastructure.presentation.view.property;

import sap.ass02.infrastructure.presentation.view.dialog.AddUserView;

/**
 * Interface for classes that can add a user
 * @param <L> the listener type
 */
public interface AddUserCapable<L> {
    /**
     * Add a listener for adding a user
     *
     * @param listener the listener
     */
    void addAddUserListener(final L listener);
    
    /**
     * Open the add user dialog
     */
    void openAddUserDialog();
    
    /**
     * Get the add user dialog
     *
     * @return the add user dialog
     */
    AddUserView getAddUserDialog();
    
}
