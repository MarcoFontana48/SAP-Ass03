package sap.ass02.infrastructure.presentation.controller.property;

import io.vertx.ext.web.client.WebClient;
import sap.ass02.infrastructure.presentation.controller.property.ClientRequest;

/**
 * Interface for classes that can send client requests
 */
public interface ClientRequestSender {
    /**
     * Attaches a WebClient to the sender
     * @param webClient the WebClient to attach
     * @param host the host to attach
     * @param port the port to attach
     */
    void attachWebClient(final WebClient webClient, final String host, final int port);
    
    /**
     * Makes a new client request
     * @return the new client request
     */
    ClientRequest makeClientRequest();
}
