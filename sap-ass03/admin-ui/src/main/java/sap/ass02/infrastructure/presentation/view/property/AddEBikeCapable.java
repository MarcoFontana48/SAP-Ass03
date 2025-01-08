package sap.ass02.infrastructure.presentation.view.property;

import sap.ass02.infrastructure.presentation.view.dialog.AddBikeView;

/**
 * Interface for classes that can add an e-bike
 * @param <L> the listener type
 */
public interface AddEBikeCapable<L> {
    /**
     * Add a listener to the add e-bike event
     * @param listener the listener to add
     */
    void addAddEBikeListener(final L listener);
    
    /**
     * Open the add e-bike dialog
     */
    void openAddEBikeDialog();
    
    /**
     * Get the add e-bike dialog
     * @return the add e-bike dialog
     */
    AddBikeView getAddEBikeDialog();
    
}
