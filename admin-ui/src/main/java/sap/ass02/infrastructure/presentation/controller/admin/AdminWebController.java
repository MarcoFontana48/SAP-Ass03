package sap.ass02.infrastructure.presentation.controller.admin;

import io.vertx.core.Verticle;
import sap.ass02.infrastructure.presentation.controller.WebController;
import sap.ass02.domain.port.EventManager;
import sap.ass02.infrastructure.presentation.view.admin.AdminView;

/**
 * Admin Web Controller Interface
 */
public interface AdminWebController extends WebController<AdminView>, Verticle {
}
