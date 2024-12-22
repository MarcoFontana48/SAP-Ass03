package sap.ass02.infrastructure.presentation.controller.property;

import io.vertx.ext.web.client.WebClient;

public interface ClientRequestSender {
    void attachWebClient(final WebClient webClient, final String host, final int port);
    
    ClientRequest makeClientRequest();
}
