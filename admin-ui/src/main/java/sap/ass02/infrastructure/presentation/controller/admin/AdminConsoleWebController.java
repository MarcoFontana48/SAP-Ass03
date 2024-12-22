package sap.ass02.infrastructure.presentation.controller.admin;

import io.vertx.ext.web.client.WebClient;
import sap.ass02.infrastructure.presentation.api.CoreImpl;
import sap.ass02.infrastructure.presentation.controller.property.ViewAware;
import sap.ass02.infrastructure.presentation.controller.property.WebClientAware;
import sap.ass02.infrastructure.presentation.view.admin.AdminView;

public final class AdminConsoleWebController extends AbstractAdminController implements AdminWebController, ViewAware<AdminView>, WebClientAware {
    private CoreImpl appApi = new CoreImpl();
    
    public AdminConsoleWebController() {
        super();
    }
    
    @Override
    public void attachView(AdminView view) {
        this.view = view;
    }
    
    @Override
    public AdminView getView() {
        return this.view;
    }
    
    @Override
    public CoreImpl getAppAPI() {
        return this.appApi;
    }
    
    @Override
    public void setAppAPI(CoreImpl appAPI) {
        this.appApi = appAPI;
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
