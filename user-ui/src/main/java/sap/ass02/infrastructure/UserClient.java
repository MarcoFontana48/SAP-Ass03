package sap.ass02.infrastructure;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import sap.ass02.domain.EventManager;
import sap.ass02.infrastructure.presentation.controller.user.UserGUIWebController;
import sap.ass02.infrastructure.presentation.controller.user.UserWebController;
import sap.ass02.infrastructure.presentation.view.user.UserGUIView;
import sap.ass02.infrastructure.presentation.view.user.UserView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UserClient {
    private static final Logger LOGGER = LogManager.getLogger(UserClient.class);
    private static final int SERVER_PORT = 8080;
    private static final String SERVER_IP_ADDRESS = "localhost";
    
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        WebClient webClient = WebClient.create(vertx);
        
        /* PRESENTATION LAYER */
        
        UserView view = new UserGUIView();
        
        UserWebController controller = new UserGUIWebController();
        controller.attachView(view);
        controller.attachWebClient(webClient, SERVER_IP_ADDRESS, SERVER_PORT);

        view.setup();
        view.display();
        
        EventManager eventManager = new KafkaEBikeServiceEventManagerVerticle();
        
        vertx.deployVerticle(controller);
        vertx.deployVerticle(eventManager);
        
        LOGGER.info("user client started");
    }
}
