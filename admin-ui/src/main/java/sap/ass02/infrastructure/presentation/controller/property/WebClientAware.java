package sap.ass02.infrastructure.presentation.controller.property;

import io.vertx.ext.web.client.WebClient;

/**
 * Interface for classes that can be attached to a WebClient
 */
public interface WebClientAware {
    /**
     * Attach a WebClient to the implementing class
     *
     * @param webClient WebClient to attach
     * @param host      host of the WebClient
     * @param port      port of the WebClient
     */
    void attachWebClient(final WebClient webClient, final String host, final int port);
    
    /**
     * Get the WebClient
     *
     * @return the WebClient
     */
    WebClient getWebClient();
    
    /**
     * Get the host of the WebClient
     *
     * @return the host of the WebClient
     */
    String getHost();
    
    /**
     * Get the port of the WebClient
     *
     * @return the port of the WebClient
     */
    int getPort();
}
