package sap.ass02.infrastructure.presentation.view.property;

import sap.ass02.infrastructure.presentation.view.dialog.AddUserView;

public interface AddUserCapable<L> {
    void addAddUserListener(final L listener);
    
    void openAddUserDialog();
    
    AddUserView getAddUserDialog();
    
}
