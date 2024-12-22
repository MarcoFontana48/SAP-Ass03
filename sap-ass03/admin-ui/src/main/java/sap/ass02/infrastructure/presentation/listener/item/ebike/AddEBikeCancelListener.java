package sap.ass02.infrastructure.presentation.listener.item.ebike;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.controller.item.AddEBikeWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddEBikeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener for the cancel button in the add e-bike dialog.
 */
public final class AddEBikeCancelListener implements ActionListener, ViewListener<AddEBikeWebController, AddEBikeView> {
    private static final Logger LOGGER = LogManager.getLogger(AddEBikeCancelListener.class);
    private AddEBikeWebController controller;

    /**
     * Creates a new listener.
     */
    public AddEBikeCancelListener() {
    }
    
    /**
     * Attaches the given controller to this listener.
     *
     * @param controller the controller to attach
     */
    @Override
    public void attachController(final AddEBikeWebController controller) {
        this.controller = controller;
        LOGGER.trace("Attached webController of type '{}' to listener '{}'", controller.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    /**
     * Handles the action event.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Cancel button clicked");

        this.controller.getView().closeDialog();
    }
}
