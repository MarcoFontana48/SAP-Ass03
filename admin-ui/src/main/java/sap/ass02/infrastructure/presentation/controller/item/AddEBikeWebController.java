package sap.ass02.infrastructure.presentation.controller.item;

import io.vertx.ext.web.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.api.CoreImpl;
import sap.ass02.infrastructure.presentation.controller.WebController;
import sap.ass02.infrastructure.presentation.controller.property.ClientRequest;
import sap.ass02.infrastructure.presentation.controller.property.ClientRequestSender;
import sap.ass02.infrastructure.presentation.controller.property.StandardClientRequest;
import sap.ass02.infrastructure.presentation.controller.property.ViewAware;
import sap.ass02.infrastructure.presentation.listener.item.ebike.AddEBikeCancelListener;
import sap.ass02.infrastructure.presentation.listener.item.ebike.AddEBikeOkListener;
import sap.ass02.infrastructure.presentation.view.dialog.AddEBikeView;

public final class AddEBikeWebController implements ViewAware<AddEBikeView>, ClientRequestSender, WebController<AddEBikeView> {
    private static final Logger LOGGER = LogManager.getLogger(AddEBikeWebController.class);
    private AddEBikeView eBikeServiceViewAddEBikeDialog;
    private final ClientRequest clientRequest = new StandardClientRequest();
    private CoreImpl appAPI;
    
    public AddEBikeWebController() {
    }
    
    @Override
    public void attachView(AddEBikeView view) {
        LOGGER.trace("Attaching view of type '{}' to webController of type '{}'", view.getClass().getSimpleName(), this.getClass().getSimpleName());
        this.eBikeServiceViewAddEBikeDialog = view;
        
        AddEBikeOkListener addEBikeOkListener = new AddEBikeOkListener();
        addEBikeOkListener.attachController(this);
        this.eBikeServiceViewAddEBikeDialog.addOkButtonListener(addEBikeOkListener);
        
        AddEBikeCancelListener addEBikeCancelListener = new AddEBikeCancelListener();
        addEBikeCancelListener.attachController(this);
        this.eBikeServiceViewAddEBikeDialog.addCancelButtonListener(addEBikeCancelListener);
        
        LOGGER.trace("Attached view of type '{}' to webController of type '{}'", view.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    @Override
    public void attachWebClient(final WebClient webClient, final String host, final int port) {
        LOGGER.trace("Attaching model of type '{}' to webController of type '{}'", webClient.getClass().getSimpleName(), this.getClass().getSimpleName());
        this.clientRequest.attachWebClient(webClient, host, port);
    }
    
    @Override
    public AddEBikeView getView() {
        return this.eBikeServiceViewAddEBikeDialog;
    }
    
    @Override
    public ClientRequest makeClientRequest() {
        return this.clientRequest;
    }
    
    @Override
    public CoreImpl getAppAPI() {
        return this.appAPI;
    }
    
    @Override
    public void setAppAPI(CoreImpl appAPI) {
        this.appAPI = appAPI;
    }
}
