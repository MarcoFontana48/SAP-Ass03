package sap.ass02.infrastructure.presentation.view.admin;

import sap.ass02.infrastructure.presentation.listener.admin.AdminAddEBikeListener;
import sap.ass02.infrastructure.presentation.listener.admin.AdminAddUserListener;
import sap.ass02.infrastructure.presentation.listener.admin.AdminStartRideListener;
import sap.ass02.infrastructure.presentation.listener.item.plugin.AddPluginListener;
import sap.ass02.infrastructure.presentation.view.AppView;
import sap.ass02.infrastructure.presentation.view.property.*;

public interface AdminView extends AppView, UserVisualizer, EBikeVisualizer, AddUserCapable<AdminAddUserListener>, AddEBikeCapable<AdminAddEBikeListener>, StartRideCapable<AdminStartRideListener>, AddPluginCapable<AddPluginListener<AdminView>> {
    
}
