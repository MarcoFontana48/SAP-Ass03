package sap.ass02.infrastructure.presentation.view.property;

import sap.ass02.infrastructure.presentation.view.dialog.AddEBikeView;

public interface AddEBikeCapable<L> {
    void addAddEBikeListener(final L listener);
    
    void openAddEBikeDialog();
    
    AddEBikeView getAddEBikeDialog();
    
}
