package sap.ass02.infrastructure.presentation.api;

import java.io.File;

public interface Core {
    /**
     *
     * Load a new effect plugin
     *
     * @param pluginId effect id
     * @param pluginFile jar file name
     */
    void loadNewPlugin(String pluginId, File pluginFile);
    
    /**
     *
     * Apply the specified plugin
     *
     * @param pluginId plugin id
     */
    void applyPlugin(String pluginId, Object target);
    
}
