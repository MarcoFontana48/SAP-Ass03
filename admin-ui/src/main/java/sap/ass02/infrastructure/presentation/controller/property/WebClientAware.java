package sap.ass02.infrastructure.presentation.controller.property;

import io.vertx.ext.web.client.WebClient;

public interface WebClientAware {
    void attachWebClient(final WebClient webClient, final String host, final int port);
    
    WebClient getWebClient();
    
    String getHost();
    
    int getPort();
}
