package sap.ass02.infrastructure.presentation.controller.property;

import sap.ass02.infrastructure.presentation.view.AppView;

/**
 * Interface for view aware classes
 * @param <V> the view type
 */
public interface ViewAware<V extends AppView> {
    void attachView(final V view);
    V getView();
}
