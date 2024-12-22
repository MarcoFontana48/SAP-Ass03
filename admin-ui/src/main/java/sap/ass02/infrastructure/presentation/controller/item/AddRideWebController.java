package sap.ass02.infrastructure.presentation.controller.item;

import io.vertx.ext.web.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.api.CoreImpl;
import sap.ass02.infrastructure.presentation.controller.WebController;
import sap.ass02.infrastructure.presentation.controller.property.ClientRequest;
import sap.ass02.infrastructure.presentation.controller.property.ClientRequestSender;
import sap.ass02.infrastructure.presentation.controller.property.StandardClientRequest;
import sap.ass02.infrastructure.presentation.controller.property.ViewAware;
import sap.ass02.infrastructure.presentation.listener.item.ride.AddRideStartListener;
import sap.ass02.infrastructure.presentation.listener.item.ride.AddRideStopListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddRideView;

public final class AddRideWebController implements ViewAware<AddRideView>, ClientRequestSender, WebController<AddRideView> {
    private static final Logger LOGGER = LogManager.getLogger(AddRideWebController.class);
    private AddRideView rideDialogView;
    private final ClientRequest clientRequest = new StandardClientRequest();
    private CoreImpl appAPI;
    
    public AddRideWebController() {
    }
    
    @Override
    public void attachView(AddRideView view) {
        LOGGER.trace("Attaching view of type '{}' to webController of type '{}'", view.getClass().getSimpleName(), this.getClass().getSimpleName());
        this.rideDialogView = view;
        
        AddRideStartListener addRideStartListener = new AddRideStartListener();
        addRideStartListener.attachController(this);
        this.rideDialogView.addRideStartButtonListener(addRideStartListener);
        
        AddRideStopListener addRideStopListener = new AddRideStopListener();
        addRideStopListener.attachController(this);
        this.rideDialogView.addRideStopButtonListener(addRideStopListener);
        
        LOGGER.trace("Attached view of type '{}' to webController of type '{}'", view.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    @Override
    public void attachWebClient(final WebClient webClient, final String host, final int port) {
        LOGGER.trace("Attaching model of type '{}' to webController of type '{}'", webClient.getClass().getSimpleName(), this.getClass().getSimpleName());
        this.clientRequest.attachWebClient(webClient, host, port);
    }
    
    @Override
    public CoreImpl getAppAPI() {
        return this.appAPI;
    }
    
    @Override
    public void setAppAPI(CoreImpl appAPI) {
        this.appAPI = appAPI;
    }
    
    @Override
    public AddRideView getView() {
        return this.rideDialogView;
    }
    
    @Override
    public ClientRequest makeClientRequest() {
        return this.clientRequest;
    }
}
