package sap.ass02.infrastructure;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.controller.admin.AdminConsoleWebController;
import sap.ass02.infrastructure.presentation.controller.admin.AdminWebController;
import sap.ass02.infrastructure.presentation.controller.admin.AdminGUIWebController;
import sap.ass02.infrastructure.presentation.view.admin.AdminConsoleView;
import sap.ass02.infrastructure.presentation.view.admin.AdminGUIView;
import sap.ass02.infrastructure.presentation.view.admin.AdminView;

/**
 * The admin client
 */
public final class AdminClient {
    private static final Logger LOGGER = LogManager.getLogger(AdminClient.class);
    private static final int SERVER_PORT = 8080;
    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final int FOUR_WEEKS = 28;
    
    /**
     * Creates a new admin client
     */
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        WebClient webClient = WebClient.create(vertx);
        
        /* PRESENTATION LAYER */
        
        AdminView guiView = new AdminGUIView();
        AdminWebController controller = new AdminGUIWebController();
        controller.attachView(guiView);
        controller.attachWebClient(webClient, SERVER_IP_ADDRESS, SERVER_PORT);
        
        AdminView consoleView = new AdminConsoleView();
        AdminWebController consoleController = new AdminConsoleWebController();
        consoleController.attachView(consoleView);
        consoleController.attachWebClient(webClient, SERVER_IP_ADDRESS, SERVER_PORT);
        
        guiView.setup();
        guiView.display();
        
        consoleView.setup();
        consoleView.display();
        
        KafkaEBikeServiceEventManagerVerticle eventManager = new KafkaEBikeServiceEventManagerVerticle();
        
        vertx.deployVerticle(controller);
        vertx.deployVerticle(consoleController);
        vertx.deployVerticle(eventManager).onSuccess(ar ->
            eventManager.updateViewWithLatestEventsCountingFrom(FOUR_WEEKS)
        );
        
        LOGGER.info("admin client started");
    }
}
