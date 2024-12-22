package sap.ass02.infrastructure.presentation.controller.admin;

import io.vertx.ext.web.client.WebClient;
import sap.ass02.infrastructure.presentation.api.CoreImpl;
import sap.ass02.infrastructure.presentation.controller.property.ViewAware;
import sap.ass02.infrastructure.presentation.controller.property.WebClientAware;
import sap.ass02.infrastructure.presentation.view.admin.AdminView;

/**
 * Admin Console Web Controller
 */
public final class AdminConsoleWebController extends AbstractAdminController implements AdminWebController, ViewAware<AdminView>, WebClientAware {
    private CoreImpl appApi = new CoreImpl();
    
    /**
     * Constructor
     */
    public AdminConsoleWebController() {
        super();
    }
    
    /**
     * attaches view to controller
     *
     * @param view
     */
    @Override
    public void attachView(AdminView view) {
        this.view = view;
    }
    
    /**
     * gets view
     *
     * @return view
     */
    @Override
    public AdminView getView() {
        return this.view;
    }
    
    /**
     * gets app api
     *
     * @return app api
     */
    @Override
    public CoreImpl getAppAPI() {
        return this.appApi;
    }
    
    /**
     * sets app api
     *
     * @param appAPI app api
     */
    @Override
    public void setAppAPI(CoreImpl appAPI) {
        this.appApi = appAPI;
    }
    
    /**
     * gets web client
     */
    @Override
    public WebClient getWebClient() {
        return this.webClient;
    }
    
    /**
     * gets the host
     */
    @Override
    public String getHost() {
        return this.host;
    }
    
    /**
     * gets the port
     */
    @Override
    public int getPort() {
        return this.port;
    }
}
