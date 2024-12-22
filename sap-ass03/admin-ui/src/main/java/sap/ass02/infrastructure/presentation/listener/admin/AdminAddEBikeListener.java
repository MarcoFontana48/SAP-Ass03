package sap.ass02.infrastructure.presentation.listener.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.controller.admin.AdminGUIWebController;
import sap.ass02.infrastructure.presentation.controller.item.AddEBikeWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.admin.AdminView;

import java.awt.event.ActionEvent;

/**
 * Listener for the add eBike button
 */
public final class AdminAddEBikeListener implements ViewListener<AdminGUIWebController, AdminView> {
    private static final Logger LOGGER = LogManager.getLogger(AdminAddEBikeListener.class.getName());
    private AdminGUIWebController controller;
    
    /**
     * Creates a new listener
     */
    public AdminAddEBikeListener() {
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
        LOGGER.trace("Add eBike button clicked");
        
        AddEBikeWebController dialogController = new AddEBikeWebController();
        dialogController.attachView(this.controller.getView().getAddEBikeDialog());
        dialogController.attachWebClient(this.controller.getWebClient(), this.controller.getHost(), this.controller.getPort());
        
        this.controller.getView().openAddEBikeDialog();
    }
}
