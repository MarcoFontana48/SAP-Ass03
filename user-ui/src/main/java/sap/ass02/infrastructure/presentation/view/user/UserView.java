package sap.ass02.infrastructure.presentation.view.user;

import sap.ass02.infrastructure.presentation.listener.item.plugin.AddPluginListener;
import sap.ass02.infrastructure.presentation.listener.user.UserStartRideListener;
import sap.ass02.infrastructure.presentation.view.AppView;
import sap.ass02.infrastructure.presentation.view.property.EBikeVisualizer;
import sap.ass02.infrastructure.presentation.view.property.AddPluginCapable;
import sap.ass02.infrastructure.presentation.view.property.StartRideCapable;

public interface UserView extends AppView, EBikeVisualizer, StartRideCapable<UserStartRideListener>, AddPluginCapable<AddPluginListener<UserView>> {

}
