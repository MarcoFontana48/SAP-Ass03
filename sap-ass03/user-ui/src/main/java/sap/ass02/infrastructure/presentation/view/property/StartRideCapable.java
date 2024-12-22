package sap.ass02.infrastructure.presentation.view.property;

import sap.ass02.infrastructure.presentation.view.dialog.AddRideView;

public interface StartRideCapable<L> {
    void addStartRideEBikeListener(final L listener);
    
    void openStartRideDialog();
    
    AddRideView getAddRideDialog();
    
}
