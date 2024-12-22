package sap.ass02.infrastructure.presentation.listener.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.controller.admin.AdminGUIWebController;
import sap.ass02.infrastructure.presentation.controller.item.AddRideWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.admin.AdminView;

import java.awt.event.ActionEvent;

/**
 * Listener for the start ride button in the admin UI.
 */
public final class AdminStartRideListener implements ViewListener<AdminGUIWebController, AdminView> {
    private static final Logger LOGGER = LogManager.getLogger(AdminStartRideListener.class);
    private AdminGUIWebController controller;

    /**
     * Creates a new listener.
     */
    public AdminStartRideListener() {
    }

    /**
     * Attaches the given controller to this listener.
     *
     * @param controller the controller to attach
     */
    @Override
    public void attachController(final AdminGUIWebController controller) {
        this.controller = controller;
        LOGGER.trace("Attached webController of type '{}' to listener of type '{}'", controller.getClass().getSimpleName(), this.getClass().getSimpleName());
    }

    /**
     * Handles the action performed event.
     *
     * @param e the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Start ride button clicked");
        
        AddRideWebController addRideController = new AddRideWebController();
        addRideController.attachView(this.controller.getView().getAddRideDialog());
        addRideController.attachWebClient(this.controller.getWebClient(), this.controller.getHost(), this.controller.getPort());
        
        this.controller.getView().openStartRideDialog();
    }
}
