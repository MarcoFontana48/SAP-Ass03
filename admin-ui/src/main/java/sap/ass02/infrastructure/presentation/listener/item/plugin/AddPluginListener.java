package sap.ass02.infrastructure.presentation.listener.item.plugin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.infrastructure.presentation.controller.WebController;
import sap.ass02.infrastructure.presentation.listener.ViewListener;
import sap.ass02.infrastructure.presentation.view.AppView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public abstract class AddPluginListener<V extends AppView> implements ViewListener<WebController<V>, V> {
    private static final Logger LOGGER = LogManager.getLogger(AddPluginListener.class);
    protected WebController<V> webController;
    
    public AddPluginListener() {
    }
    
    @Override
    public void attachController(WebController<V> webController) {
        this.webController = webController;
        LOGGER.trace("Attached webController of type '{}' to listener '{}'", webController.getClass().getSimpleName(), this.getClass().getSimpleName());
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.trace("Add plugin button clicked");
        
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            LOGGER.trace("Selected file: '{}'", selectedFile.getAbsolutePath());
            try {
                String pluginId = selectedFile.getName().replaceFirst(".jar", "");
                LOGGER.trace("Extracted plugin ID: '{}'", pluginId);
                this.webController.getView().addNewEffect(pluginId);
                this.webController.getAppAPI().loadNewPlugin(pluginId, selectedFile);
                this.webController.getView().getVisualizerPanel().ifPresent(v -> this.webController.getAppAPI().applyPlugin(pluginId, v));
            } catch (Exception ignored) {
            
            }
        }

    }
}
