package sap.ass02.infrastructure.presentation.listener;

import sap.ass02.infrastructure.presentation.controller.WebController;
import sap.ass02.infrastructure.presentation.view.AppView;

import java.awt.event.ActionListener;

public interface ViewListener<C extends WebController<V>, V extends AppView> extends ActionListener {
    void attachController(C controller);
}
