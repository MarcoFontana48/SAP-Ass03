package sap.ass02.infrastructure.presentation.view.property;

import sap.ass02.infrastructure.presentation.view.dialog.AddRideView;

/**
 * Interface for classes that can start a ride
 *
 * @param <L> type of listener
 */
public interface StartRideCapable<L> {
    /**
     * Add a listener for starting a ride
     *
     * @param listener the listener
     */
    void addStartRideEBikeListener(final L listener);
    
    /**
     * Open the start ride dialog
     */
    void openStartRideDialog();
    
    /**
     * Get the add ride dialog
     *
     * @return the add ride dialog
     */
    AddRideView getAddRideDialog();
    
}
