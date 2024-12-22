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

public final class AddRideStopListener implements ActionListener, ViewListener<AddRideWebController, AddRideView> {
    private static final Logger LOGGER = LogManager.getLogger(AddRideStopListener.class);
    private AddRideWebController controller;

    public AddRideStopListener() {
    }
    
    @Override
    public void attachController(final AddRideWebController controller) {
        this.controller = controller;
        LOGGER.trace("Attached webController of type '{}' to listener '{}'", controller.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Stop ride button clicked");
        
        this.controller.makeClientRequest().getRide(this.controller.getView().getUserId(), this.controller.getView().getEBikeId()).onComplete(ar -> {
            if (ar.succeeded()) {
                LOGGER.trace("Ride found successfully: '{}'", ar.result());
                String ride = ar.result();
                JsonObject result = new JsonObject(ride);
                String rideId = result.getString(JsonFieldKey.JSON_RIDE_ID_KEY);
                LOGGER.trace("Retrieved ride id: '{}'", rideId);
                this.controller.makeClientRequest().stopRide(rideId).onComplete(r -> {
                    if (r.succeeded()) {
                        LOGGER.trace("Ride stopped successfully");
                        this.controller.getView().setRideId("Ride stopped");
                    } else {
                        LOGGER.error("Failed to stop ride: '{}'", r.cause().getMessage());
                        this.controller.getView().setRideId("Error stopping ride");
                    }
                });
            } else {
                LOGGER.error("Failed to stop ride: '{}'", ar.cause().getMessage());
                this.controller.getView().setRideId("Error stopping ride");
            }
//            this.webController.getView().closeDialog();
        });

    }
}
