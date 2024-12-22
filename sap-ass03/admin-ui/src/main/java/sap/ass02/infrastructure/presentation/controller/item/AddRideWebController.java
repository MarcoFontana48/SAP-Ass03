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

/**
 * Controller for adding a ride
 */
public final class AddRideWebController implements ViewAware<AddRideView>, ClientRequestSender, WebController<AddRideView> {
    private static final Logger LOGGER = LogManager.getLogger(AddRideWebController.class);
    private AddRideView rideDialogView;
    private final ClientRequest clientRequest = new StandardClientRequest();
    private CoreImpl appAPI;
    
    /**
     * Instantiates a new Add ride web controller.
     */
    public AddRideWebController() {
    }
    
    /**
     * Attaches the view to the controller
     *
     * @param view the view
     */
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
    
    /**
     * Attaches the web client to the controller
     *
     * @param webClient the web client
     * @param host      the host
     * @param port      the port
     */
    @Override
    public void attachWebClient(final WebClient webClient, final String host, final int port) {
        LOGGER.trace("Attaching model of type '{}' to webController of type '{}'", webClient.getClass().getSimpleName(), this.getClass().getSimpleName());
        this.clientRequest.attachWebClient(webClient, host, port);
    }
    
    /**
     * Gets the view
     *
     * @return the view
     */
    @Override
    public CoreImpl getAppAPI() {
        return this.appAPI;
    }
    
    /**
     * Sets the app api
     *
     * @param appAPI the view
     */
    @Override
    public void setAppAPI(CoreImpl appAPI) {
        this.appAPI = appAPI;
    }
    
    /**
     * gets the view
     */
    @Override
    public AddRideView getView() {
        return this.rideDialogView;
    }
    
    /**
     * Makes a client request
     *
     * @return the client request
     */
    @Override
    public ClientRequest makeClientRequest() {
        return this.clientRequest;
    }
}
