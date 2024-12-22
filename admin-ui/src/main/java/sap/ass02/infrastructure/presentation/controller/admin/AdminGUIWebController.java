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

public final class AdminGUIWebController extends AbstractAdminController implements AdminWebController, ViewAware<AdminView>, WebClientAware {
    private static final Logger LOGGER = LogManager.getLogger(AdminGUIWebController.class);
    private CoreImpl appAPI = new CoreImpl();
    
    
    public AdminGUIWebController() {
        super();
    }
    
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
    
    @Override
    public AdminView getView() {
        return this.view;
    }
    
    @Override
    public CoreImpl getAppAPI() {
        return this.appAPI;
    }
    
    @Override
    public void setAppAPI(CoreImpl appAPI) {
        this.appAPI = appAPI;
    }
    
    @Override
    public WebClient getWebClient() {
        return this.webClient;
    }
    
    @Override
    public String getHost() {
        return this.host;
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
}
