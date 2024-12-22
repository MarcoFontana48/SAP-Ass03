package sap.ass02.infrastructure.presentation.view;

import sap.ass02.infrastructure.presentation.listener.item.plugin.AddPluginListener;
import sap.ass02.infrastructure.presentation.view.AppView;
import sap.ass02.infrastructure.presentation.view.property.AddPluginCapable;

import javax.swing.*;

/**
 * Abstract GUI view
 * @param <V> the view type
 */
public abstract class AbstractGUIView<V extends AppView> extends JFrame implements AddPluginCapable<AddPluginListener<V>> {
    /**
     * Creates a new abstract GUI view
     */
    protected final JButton addPluginButton = new JButton("Add plugin");
}
