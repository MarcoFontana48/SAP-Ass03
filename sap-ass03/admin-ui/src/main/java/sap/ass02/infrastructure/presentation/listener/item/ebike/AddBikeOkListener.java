package sap.ass02.infrastructure.presentation.listener.item.ebike;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.controller.item.AddEBikeWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddBikeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener for the ok button in the add eBike dialog.
 */
public final class AddBikeOkListener implements ActionListener, ViewListener<AddEBikeWebController, AddBikeView> {
    private static final Logger LOGGER = LogManager.getLogger(AddBikeOkListener.class);
    private AddEBikeWebController controller;

    /**
     * Creates a new listener
     */
    public AddBikeOkListener() {
    }
    
    /**
     * Attaches the controller to the listener
     *
     * @param controller the controller
     */
    @Override
    public void attachController(final AddEBikeWebController controller) {
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
        LOGGER.trace("Ok button clicked");
        
        String bikeId = this.controller.getView().getEBikeId();
        if (bikeId.isBlank()) {
            LOGGER.error("User ID cannot be empty, skipping user creation");
            return;
        }
        
        if (this.controller.getView().isAgentBike()) {
            this.controller.makeClientRequest().addAgentBike(bikeId).onComplete(r -> {
                if (r.succeeded()) {
                    LOGGER.trace("ABike '{}' created successfully", bikeId);
                } else {
                    LOGGER.error("Failed to create ABike '{}': {}", bikeId, r.cause().getMessage());
                }
                this.controller.getView().closeDialog();
            });
        } else {
            this.controller.makeClientRequest().addEBike(bikeId).onComplete(r -> {
                if (r.succeeded()) {
                    LOGGER.trace("EBike '{}' created successfully", bikeId);
                } else {
                    LOGGER.error("Failed to create EBike '{}': {}", bikeId, r.cause().getMessage());
                }
                this.controller.getView().closeDialog();
            });
        }
    }
}
