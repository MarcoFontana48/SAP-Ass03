package sap.ass02.infrastructure.presentation.controller.admin;

import io.vertx.ext.web.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.api.CoreImpl;
import sap.ass02.infrastructure.presentation.controller.property.ViewAware;
import sap.ass02.infrastructure.presentation.controller.property.WebClientAware;
import sap.ass02.infrastructure.presentation.listener.admin.AdminAddEBikeListener;
import sap.ass02.infrastructure.presentation.listener.admin.AdminAddUserListener;
import sap.ass02.infrastructure.presentation.listener.admin.AdminStartRideListener;
import sap.ass02.infrastructure.presentation.listener.item.plugin.AdminAddPluginListener;
import sap.ass02.infrastructure.presentation.view.admin.AdminView;

/**
 * Web controller for the admin GUI
 */
public final class AdminGUIWebController extends AbstractAdminController implements AdminWebController, ViewAware<AdminView>, WebClientAware {
    private static final Logger LOGGER = LogManager.getLogger(AdminGUIWebController.class);
    private CoreImpl appAPI = new CoreImpl();
    
    /**
     * Creates a new admin GUI web controller
     */
    public AdminGUIWebController() {
        super();
    }
    
    /**
     * Attaches the view to the controller
     *
     * @param appAdminView the view
     */
    @Override
    public void attachView(final AdminView appAdminView) {
        this.view = appAdminView;
        
        AdminAddEBikeListener adminAddEBikeListener = new AdminAddEBikeListener();
        adminAddEBikeListener.attachController(this);
        this.view.addAddEBikeListener(adminAddEBikeListener);
        
        AdminAddUserListener adminAddUserListener = new AdminAddUserListener();
        adminAddUserListener.attachController(this);
        this.view.addAddUserListener(adminAddUserListener);
        
        AdminStartRideListener adminStartRideEBikeButtonListener = new AdminStartRideListener();
        adminStartRideEBikeButtonListener.attachController(this);
        this.view.addStartRideEBikeListener(adminStartRideEBikeButtonListener);
        
        AdminAddPluginListener addPluginListener = new AdminAddPluginListener();
        addPluginListener.attachController(this);
        this.view.addPluginListener(addPluginListener);
        
        LOGGER.trace("Attached view of type '{}' to webController '{}'", this.view.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
    
    /**
     * gets the view
     * @return the view
     */
    @Override
    public AdminView getView() {
        return this.view;
    }
    
    /**
     * gets the app api
     */
    @Override
    public CoreImpl getAppAPI() {
        return this.appAPI;
    }
    
    /**
     * sets the app api
     *
     * @param appAPI the app api
     */
    @Override
    public void setAppAPI(CoreImpl appAPI) {
        this.appAPI = appAPI;
    }
    
    /**
     * gets the web client
     */
    @Override
    public WebClient getWebClient() {
        return this.webClient;
    }
    
    /**
     * get the host
     */
    @Override
    public String getHost() {
        return this.host;
    }
    
    /**
     * get the port
     */
    @Override
    public int getPort() {
        return this.port;
    }
}
