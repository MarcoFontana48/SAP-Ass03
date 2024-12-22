package sap.ass02.infrastructure.presentation.listener.item.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.controller.item.AddUserWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddUserView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class AddUserCancelListener implements ActionListener, ViewListener<AddUserWebController, AddUserView> {
    private static final Logger LOGGER = LogManager.getLogger(AddUserCancelListener.class);
    private AddUserWebController controller;

    public AddUserCancelListener() {
    }
    
    @Override
    public void attachController(final AddUserWebController controller) {
        this.controller = controller;
        LOGGER.trace("Attached webController of type '{}' to listener '{}'", controller.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Cancel button clicked");
        this.controller.getView().closeDialog();
    }
}
