package sap.ass02.infrastructure.presentation.controller.user;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.infrastructure.presentation.controller.property.ClientRequest;
import sap.ass02.infrastructure.presentation.controller.property.ClientRequestSender;
import sap.ass02.domain.utils.JsonUtils;
import sap.ass02.domain.EBike;
import sap.ass02.infrastructure.presentation.api.CoreImpl;
import sap.ass02.infrastructure.presentation.controller.property.*;
import sap.ass02.infrastructure.presentation.listener.item.plugin.UserAddPluginListener;
import sap.ass02.infrastructure.presentation.listener.user.UserStartRideListener;
import sap.ass02.infrastructure.presentation.view.user.UserView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UserGUIWebController extends AbstractVerticle implements UserWebController, ViewAware<UserView>, WebClientAware, ClientRequestSender {
    private static final Logger LOGGER = LogManager.getLogger(UserGUIWebController.class);
    private final ClientRequest clientRequest = new StandardClientRequest();
    private CoreImpl appApi = new CoreImpl();
    private UserView view;
    private int port;
    private WebClient webClient;
    private String host;
    
    public UserGUIWebController() {
    
    }
    
    @Override
    public void attachView(final UserView userView) {
        this.view = userView;
        
        UserStartRideListener userStartRideListener = new UserStartRideListener();
        userStartRideListener.attachController(this);
        this.view.addStartRideEBikeListener(userStartRideListener);
        
        UserAddPluginListener addPluginListener = new UserAddPluginListener();
        addPluginListener.attachController(this);
        this.view.addPluginListener(addPluginListener);
        
        LOGGER.trace("Attached view of type '{}' to webController '{}'", this.view.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    @Override
    public void attachWebClient(final WebClient webClient, final String host, final int port) {
        this.port = port;
        this.webClient = webClient;
        this.host = host;
        this.clientRequest.attachWebClient(webClient, host, port);
        LOGGER.trace("Attached model of type '{}' to webController '{}'", webClient.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    @Override
    public void start() {
        this.updateViewWithInitialState();
        this.startMonitoringAndUpdatingView();
    }
    
    private void updateViewWithInitialState() {
        LOGGER.trace("Sending request to retrieve all eBikes to update the view with initial state");
        Future<Iterable<EBike>> allEBikes = this.makeClientRequest().getAllEBikes();
        
        LOGGER.trace("Retrieved eBikes: '{}'", allEBikes);
        this.showToView(allEBikes);
    }
    
    private void startMonitoringAndUpdatingView() {
        this.vertx.eventBus().consumer("ebike-update", message -> {
            LOGGER.trace("Received message from event bus: '{}'", message.body());
            this.showToView(message.body().toString());
        });
    }
    
    private void showToView(String message) {
        JsonObject obj = new JsonObject(message);
        LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
        if (obj.containsKey(JsonFieldKey.EBIKE_ID_KEY)) {
            LOGGER.trace("Adding eBike to view");
            this.view.addEBikeToShow(JsonUtils.fromJsonStringToEBike(message));
            LOGGER.trace("Refreshing view");
            this.view.refresh();
        } else {
            LOGGER.error("Unknown entity type received: '{}'", obj);
        }
    }
    
    private void showToView(Future<Iterable<EBike>> allEBikes) {
        allEBikes.onSuccess(eBikes -> {
            LOGGER.trace("Retrieved eBikes successfully: '{}'", eBikes);
            eBikes.forEach(this.view::addEBikeToShow);
            LOGGER.trace("About to refresh view");
            this.view.refresh();
        });
        allEBikes.onFailure(throwable -> LOGGER.error("Failed to retrieve eBikes: {}", throwable.getMessage()));
    }
    
    @Override
    public CoreImpl getAppAPI() {
        return this.appApi;
    }
    
    @Override
    public void setAppAPI(CoreImpl appAPI) {
        this.appApi = appAPI;
    }
    
    @Override
    public ClientRequest makeClientRequest() {
        return this.clientRequest;
    }
    
    @Override
    public UserView getView() {
        return this.view;
    }
    
    @Override
    public WebClient getWebClient() {
        return this.webClient;
    }
    
    @Override
    public String getHost() {
        return this.host;
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
}
