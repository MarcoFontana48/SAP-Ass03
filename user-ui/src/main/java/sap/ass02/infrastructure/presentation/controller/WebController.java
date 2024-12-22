package sap.ass02.infrastructure.presentation.controller;

import io.vertx.ext.web.client.WebClient;
import sap.ass02.domain.Controller;
import sap.ass02.infrastructure.presentation.api.CoreImpl;
import sap.ass02.infrastructure.presentation.view.AppView;

public interface WebController<V extends AppView> extends Controller {
    void attachView(final V view);
    V getView();
    void attachWebClient(final WebClient webClient, final String SERVER_IP_ADDRESS, final int SERVER_PORT);
    
    CoreImpl getAppAPI();
    void setAppAPI(final CoreImpl appAPI);
}
