package sap.ass02.infrastructure.presentation.listener.item.user;

import sap.ass02.infrastructure.presentation.controller.item.AddUserWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddUserView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class AddUserOkListener implements ActionListener, ViewListener<AddUserWebController, AddUserView> {
    private static final Logger LOGGER = LogManager.getLogger(AddUserOkListener.class);
    private AddUserWebController controller;

    public AddUserOkListener() {
    }
    
    @Override
    public void attachController(final AddUserWebController controller) {
        this.controller = controller;
        LOGGER.trace("Attached webController of type '{}' to listener '{}'", controller.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Ok button clicked");
        
        int credits = this.controller.getView().getCredits();
        if (credits < 0) {
            LOGGER.error("Credits cannot be negative, skipping user creation");
            return;
        }
        
        String userId = this.controller.getView().getUserId();
        if (userId.isBlank()) {
            LOGGER.error("User ID cannot be empty, skipping user creation");
            return;
        }
        
        this.controller.makeClientRequest().addUser(userId, credits).onComplete(r -> {
            if (r.succeeded()) {
                LOGGER.trace("User '{}' created successfully", userId);
            } else {
                LOGGER.error("Failed to create user '{}': {}", userId, r.cause().getMessage());
            }
            this.controller.getView().closeDialog();
        });
    }
}
