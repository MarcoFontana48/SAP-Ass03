package sap.ass02.infrastructure.presentation.listener.user;

import sap.ass02.infrastructure.presentation.controller.item.AddRideWebController;
import sap.ass02.infrastructure.presentation.controller.user.UserGUIWebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.user.UserView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;

public final class UserStartRideListener implements ViewListener<UserGUIWebController, UserView> {
    private static final Logger LOGGER = LogManager.getLogger(UserStartRideListener.class);
    private UserGUIWebController controller;

    public UserStartRideListener() {
    }

    @Override
    public void attachController(final UserGUIWebController controller) {
        this.controller = controller;
        LOGGER.trace("Attached webController of type '{}' to listener of type '{}'", controller.getClass().getSimpleName(), this.getClass().getSimpleName());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Start ride button clicked");
        
        AddRideWebController addRideController = new AddRideWebController();
        addRideController.attachView(this.controller.getView().getAddRideDialog());
        addRideController.attachWebClient(this.controller.getWebClient(), this.controller.getHost(), this.controller.getPort());
        
        this.controller.getView().openStartRideDialog();
    }
}
