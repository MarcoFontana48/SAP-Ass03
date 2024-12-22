package sap.ass02.infrastructure.presentation.controller.user;

import io.vertx.core.Verticle;
import sap.ass02.infrastructure.presentation.controller.WebController;
import sap.ass02.infrastructure.presentation.view.user.UserView;

public interface UserWebController extends WebController<UserView>, Verticle {

}
