package sap.ass02.infrastructure.presentation.listener.item.ride;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.controller.item.AddRideWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddRideView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class AddReachUserListener implements ActionListener, ViewListener<AddRideWebController, AddRideView> {
    private static final Logger LOGGER = LogManager.getLogger(AddReachUserListener.class);
    private AddRideWebController controller;
    
    /**
     * Instantiates a new Add ride start listener.
     */
    public AddReachUserListener() {
    }
    
    /**
     * Attaches the controller to the listener
     *
     * @param controller the controller
     */
    @Override
    public void attachController(final AddRideWebController controller) {
        this.controller = controller;
        LOGGER.trace("Attached webController of type '{}' to listener '{}'", controller.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Reach user button clicked");
        
        String eBikeId = this.controller.getView().getEBikeId();
        if (eBikeId.isBlank()) {
            LOGGER.error("EBike ID cannot be empty, skipping ride creation");
            return;
        }
        
        String userId = this.controller.getView().getUserId();
        if (userId.isBlank()) {
            LOGGER.error("EBike ID cannot be empty, skipping ride creation");
            return;
        }
        
        try {
            this.controller.makeClientRequest().reachUser(userId, eBikeId).onComplete(res -> {
                if (res.succeeded()) {
                    LOGGER.info("Ride started successfully");
                } else {
                    LOGGER.error("Failed to start ride: '{}'", res.cause().getMessage());
                }
            });
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Failed to start ride: '{}'", ex.getMessage());
        }
    }
    
}
