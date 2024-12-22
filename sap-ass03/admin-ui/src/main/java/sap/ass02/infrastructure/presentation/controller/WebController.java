package sap.ass02.infrastructure.presentation.controller;

import io.vertx.ext.web.client.WebClient;
import sap.ass02.domain.port.Controller;
import sap.ass02.infrastructure.presentation.api.CoreImpl;
import sap.ass02.infrastructure.presentation.view.AppView;

/**
 * Interface for web controllers
 * @param <V> the view type
 */
public interface WebController<V extends AppView> extends Controller {
    /**
     * Attach the view to the controller
     * @param view the view
     */
    void attachView(final V view);
    
    /**
     * Get the view
     * @return the view
     */
    V getView();
    
    /**
     * Attach the web client to the controller
     * @param webClient the web client
     * @param SERVER_IP_ADDRESS the server IP address
     * @param SERVER_PORT the server port
     */
    void attachWebClient(final WebClient webClient, final String SERVER_IP_ADDRESS, final int SERVER_PORT);
    
    /**
     * Get the web client
     * @return the web client
     */
    CoreImpl getAppAPI();
    
    /**
     * Set the web client
     * @param appAPI the web client
     */
    void setAppAPI(final CoreImpl appAPI);
}
