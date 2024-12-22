package sap.ass02.infrastructure.presentation.listener.item.ride;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.infrastructure.presentation.controller.item.AddRideWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddRideView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class AddRideStartListener implements ActionListener, ViewListener<AddRideWebController, AddRideView> {
    private static final Logger LOGGER = LogManager.getLogger(AddRideStartListener.class);
    private AddRideWebController controller;

    public AddRideStartListener() {
    }
    
    @Override
    public void attachController(final AddRideWebController controller) {
        this.controller = controller;
        LOGGER.trace("Attached webController of type '{}' to listener '{}'", controller.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Start ride button clicked");
        
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
            this.controller.makeClientRequest().startRide(userId, eBikeId).onComplete(ignoredAr -> {
                this.controller.makeClientRequest().getRide(userId, eBikeId).onComplete(ar -> {
                    if (ar.succeeded()) {
                        JsonObject newlyAddedRideJson = new JsonObject(ar.result());
                        String rideId = newlyAddedRideJson.getString(JsonFieldKey.JSON_RIDE_ID_KEY);
                        LOGGER.trace("Resumed ride with id '{}'", rideId);
                        this.controller.getView().setRideId("Ride id: " + rideId);
                    } else {
                        LOGGER.error("Error while retrieving ride: '{}'", ar.cause().getMessage());
                        this.controller.getView().setRideId("Ride id: " + ar.result());
                    }
                });
            });
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Failed to start ride: '{}'", ex.getMessage());
        }
    }
}
