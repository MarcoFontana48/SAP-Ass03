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
import sap.ass02.infrastructure.presentation.listener.item.ebike.AddEBikeCancelListener;
import sap.ass02.infrastructure.presentation.listener.item.ebike.AddBikeOkListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddBikeView;

/**
 * Controller for adding an eBike
 */
public final class AddEBikeWebController implements ViewAware<AddBikeView>, ClientRequestSender, WebController<AddBikeView> {
    private static final Logger LOGGER = LogManager.getLogger(AddEBikeWebController.class);
    private AddBikeView eBikeServiceViewAddEBikeDialog;
    private final ClientRequest clientRequest = new StandardClientRequest();
    private CoreImpl appAPI;
    
    /**
     * Creates a new AddEBikeWebController
     */
    public AddEBikeWebController() {
    }
    
    /**
     * Attaches the view to the controller
     *
     * @param view the view to attach
     */
    @Override
    public void attachView(AddBikeView view) {
        LOGGER.trace("Attaching view of type '{}' to webController of type '{}'", view.getClass().getSimpleName(), this.getClass().getSimpleName());
        this.eBikeServiceViewAddEBikeDialog = view;
        
        AddBikeOkListener addBikeOkListener = new AddBikeOkListener();
        addBikeOkListener.attachController(this);
        this.eBikeServiceViewAddEBikeDialog.addOkButtonListener(addBikeOkListener);
        
        AddEBikeCancelListener addEBikeCancelListener = new AddEBikeCancelListener();
        addEBikeCancelListener.attachController(this);
        this.eBikeServiceViewAddEBikeDialog.addCancelButtonListener(addEBikeCancelListener);
        
        LOGGER.trace("Attached view of type '{}' to webController of type '{}'", view.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    /**
     * Attaches the web client to the controller
     *
     * @param webClient the web client to attach
     * @param host      the host to attach
     * @param port      the port to attach
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
    public AddBikeView getView() {
        return this.eBikeServiceViewAddEBikeDialog;
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
    
    /**
     * gets the app api
     */
    @Override
    public CoreImpl getAppAPI() {
        return this.appAPI;
    }
    
    /**
     * sets the app api
     */
    @Override
    public void setAppAPI(CoreImpl appAPI) {
        this.appAPI = appAPI;
    }
}
