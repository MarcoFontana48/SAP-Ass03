package sap.ass02.infrastructure.presentation.controller.admin;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.utils.JsonUtils;
import sap.ddd.Entity;
import sap.ass02.domain.EBike;
import sap.ass02.domain.User;
import sap.ass02.infrastructure.presentation.controller.property.ClientRequest;
import sap.ass02.infrastructure.presentation.controller.property.ClientRequestSender;
import sap.ass02.infrastructure.presentation.controller.property.StandardClientRequest;
import sap.ass02.infrastructure.presentation.view.admin.AdminView;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract controller for the admin UI.
 */
public abstract class AbstractAdminController extends AbstractVerticle implements ClientRequestSender, AdminWebController {
    protected static final Logger LOGGER = LogManager.getLogger(AbstractAdminController.class);
    protected Map<String, String> consumerConfig = new HashMap<>();
    protected KafkaConsumer<String, String> consumer;
    protected final ClientRequest clientRequest = new StandardClientRequest();
    protected AdminView view;
    protected int port;
    protected WebClient webClient;
    protected String host;
    
    /**
     * Attaches the given view to this controller.
     *
     * @param view the view to attach
     */
    public void attachWebClient(final WebClient webClient, final String host, final int port) {
        this.port = port;
        this.webClient = webClient;
        this.host = host;
        this.clientRequest.attachWebClient(webClient, host, port);
        LOGGER.trace("Attached model of type '{}' to webController '{}'", webClient.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    /**
     * starts the controller verticle.
     */
    @Override
    public void start() {
        this.updateViewWithInitialState();
        this.startMonitoringAndUpdatingView();
    }
    
    /**
     * starts monitoring and updating the view.
     */
    private void startMonitoringAndUpdatingView() {
        this.vertx.eventBus().consumer("ebike-update", message -> {
            JsonObject ebikeJsonObject = new JsonObject(message.body().toString());
            LOGGER.trace("Received vertx ebike-update event: '{}'", ebikeJsonObject);
            this.showToView(ebikeJsonObject.encode());
        });
        
        this.vertx.eventBus().consumer("user-update", message -> {
            JsonObject userJsonObject = new JsonObject(message.body().toString());
            LOGGER.trace("Received vertx user-update event: '{}'", userJsonObject);
            this.showToView(userJsonObject.encode());
        });
    }
    
    /**
     * Updates the view with the initial state.
     */
    private void updateViewWithInitialState() {
        Future<Iterable<EBike>> allEBikes = this.makeClientRequest().getAllEBikes();
        Future<Iterable<User>> allUsers = this.makeClientRequest().getAllUsers();
        
        this.showToView(allEBikes);
        this.showToView(allUsers);
    }
    
    /**
     * Shows the given message to the view.
     *
     * @param message the message to show
     */
    private void showToView(String message) {
        JsonObject obj = new JsonObject(message);
        LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
        if (obj.containsKey(JsonFieldKey.EBIKE_ID_KEY)) {
            LOGGER.trace("Adding eBike to view");
            this.view.addEBikeToShow(JsonUtils.fromJsonStringToEBike(message));
            LOGGER.trace("Refreshing view");
            this.view.refresh();
        } else if (obj.containsKey(JsonFieldKey.USER_ID_KEY)) {
            LOGGER.trace("Adding user to view");
            this.view.addUserToShow(JsonUtils.fromJsonStringToUser(message));
            LOGGER.trace("Refreshing view");
            this.view.refresh();
        } else {
            LOGGER.error("Unknown entity type received: '{}'", obj);
        }
    }
    
    /**
     * Shows the given entities to the view.
     *
     * @param allEntities the entities to show
     */
    private <T extends Entity<?>> void showToView(Future<Iterable<T>> allEntities) {
        allEntities.onSuccess(entities -> {
            entities.forEach(entity -> {
                if (entity instanceof EBike) {
                    this.view.addEBikeToShow((EBike) entity);
                } else if (entity instanceof User) {
                    this.view.addUserToShow((User) entity);
                }
            });
            this.view.refresh();
        });
        allEntities.onFailure(throwable -> LOGGER.error("Failed to retrieve entities: {}", throwable.getMessage()));
    }
    
    /**
     * Gets the application API.
     *
     * @return the application API
     */
    @Override
    public ClientRequest makeClientRequest() {
        return this.clientRequest;
    }
}
