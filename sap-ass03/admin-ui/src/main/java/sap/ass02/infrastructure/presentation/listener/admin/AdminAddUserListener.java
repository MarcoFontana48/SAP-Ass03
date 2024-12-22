package sap.ass02.infrastructure.presentation.listener.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.controller.admin.AdminGUIWebController;
import sap.ass02.infrastructure.presentation.controller.item.AddUserWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.admin.AdminView;

import java.awt.event.ActionEvent;

/**
 * Listener for the add user button
 */
public final class AdminAddUserListener implements ViewListener<AdminGUIWebController, AdminView> {
    private static final Logger LOGGER = LogManager.getLogger(AdminAddUserListener.class);
    private AdminGUIWebController controller;

    /**
     * Creates a new listener
     */
    public AdminAddUserListener() {
    }
    
    /**
     * Attaches the controller to the listener
     *
     * @param controller the controller
     */
    @Override
    public void attachController(final AdminGUIWebController controller) {
        this.controller = controller;
        LOGGER.trace("Attached webController of type '{}' to listener '{}'", controller.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    /**
     * Handles the action event
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Add user button clicked");
        
        AddUserWebController dialogController = new AddUserWebController();
        dialogController.attachView(this.controller.getView().getAddUserDialog());
        dialogController.attachWebClient(this.controller.getWebClient(), this.controller.getHost(), this.controller.getPort());
        
        this.controller.getView().openAddUserDialog();
    }
}
