package sap.ass02.infrastructure.presentation.view;

import sap.ass02.infrastructure.presentation.listener.item.plugin.AddPluginListener;
import sap.ass02.infrastructure.presentation.view.AppView;
import sap.ass02.infrastructure.presentation.view.property.AddPluginCapable;

import javax.swing.*;

public abstract class AbstractGUIView<V extends AppView> extends JFrame implements AddPluginCapable<AddPluginListener<V>> {
    protected final JButton addPluginButton = new JButton("Add plugin");
}
