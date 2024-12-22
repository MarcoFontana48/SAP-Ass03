package sap.ass02.infrastructure.presentation.listener.item.ebike;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.controller.item.AddEBikeWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddEBikeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class AddEBikeOkListener implements ActionListener, ViewListener<AddEBikeWebController, AddEBikeView> {
    private static final Logger LOGGER = LogManager.getLogger(AddEBikeOkListener.class);
    private AddEBikeWebController controller;

    public AddEBikeOkListener() {
    }
    
    @Override
    public void attachController(final AddEBikeWebController controller) {
        this.controller = controller;
        LOGGER.trace("Attached webController of type '{}' to listener '{}'", controller.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Ok button clicked");
        
        String eBikeId = this.controller.getView().getEBikeId();
        if (eBikeId.isBlank()) {
            LOGGER.error("User ID cannot be empty, skipping user creation");
            return;
        }
        
        this.controller.makeClientRequest().addEBike(eBikeId).onComplete(r -> {
            if (r.succeeded()) {
                LOGGER.trace("EBike '{}' created successfully", eBikeId);
            } else {
                LOGGER.error("Failed to create EBike '{}': {}", eBikeId, r.cause().getMessage());
            }
            this.controller.getView().closeDialog();
        });
    }
}
