package sap.ass02.infrastructure.presentation.controller.property;

import sap.ass02.infrastructure.presentation.view.AppView;

public interface ViewAware<V extends AppView> {
    void attachView(final V view);
    V getView();
}
