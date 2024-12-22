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
import sap.ass02.infrastructure.presentation.listener.item.user.AddUserCancelListener;
import sap.ass02.infrastructure.presentation.listener.item.user.AddUserOkListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddUserView;

/**
 * The controller for the add user view
 */
public final class AddUserWebController implements ViewAware<AddUserView>, ClientRequestSender, WebController<AddUserView> {
    private static final Logger LOGGER = LogManager.getLogger(AddUserWebController.class);
    private final ClientRequest clientRequest = new StandardClientRequest();
    private AddUserView eBikeServiceViewAddUserView;
    private CoreImpl appApi;
    
    /**
     * Instantiates a new Add user web controller.
     */
    public AddUserWebController() {
    }
    
    /**
     * Attaches the view to the controller
     *
     * @param view the view
     */
    @Override
    public void attachView(AddUserView view) {
        LOGGER.trace("Attaching view of type '{}' to webController of type '{}'", view.getClass().getSimpleName(), this.getClass().getSimpleName());
        this.eBikeServiceViewAddUserView = view;
        
        AddUserOkListener addUserOkListener = new AddUserOkListener();
        addUserOkListener.attachController(this);
        this.eBikeServiceViewAddUserView.addOkButtonListener(addUserOkListener);
        
        AddUserCancelListener addUserCancelListener = new AddUserCancelListener();
        addUserCancelListener.attachController(this);
        this.eBikeServiceViewAddUserView.addCancelButtonListener(addUserCancelListener);
        
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
     * gets the app api
     */
    @Override
    public CoreImpl getAppAPI() {
        return this.appApi;
    }
    
    /**
     * sets the app api
     *
     * @param appAPI the app api
     */
    @Override
    public void setAppAPI(CoreImpl appAPI) {
        this.appApi = appAPI;
    }
    
    /**
     * gets the view
     */
    @Override
    public AddUserView getView() {
        return this.eBikeServiceViewAddUserView;
    }
    
    /**
     * makes the client request
     */
    @Override
    public ClientRequest makeClientRequest() {
        return this.clientRequest;
    }
}
