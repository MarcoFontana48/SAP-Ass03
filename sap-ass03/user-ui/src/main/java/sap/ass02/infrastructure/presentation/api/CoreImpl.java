package sap.ass02.infrastructure.presentation.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;

public final class CoreImpl implements Core {
    private static final Logger LOGGER = LogManager.getLogger(CoreImpl.class);
    private final HashMap<String, Plugin> pluginRegistry = new HashMap<>();

    public CoreImpl() {

    }

    @Override
    public void loadNewPlugin(String pluginId, File pluginFile) {
        LOGGER.trace("Loading new plugin with id '{}' and file '{}'", pluginId, pluginFile);
        try {
            this.registerNewPlugin(pluginId, pluginFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void registerNewPlugin(String pluginID, File libFile) throws Exception {
        LOGGER.trace("Registering new plugin with id '{}' and file '{}'", pluginID, libFile);
        var loader = new PluginClassLoader(libFile.getAbsolutePath());
        LOGGER.trace("Created new class loader '{}'", loader);
    
        // Adjust the class name construction logic to include the correct package path
        String className = "sap.ass01.plugin.colors." + libFile.getName().replaceFirst("\\.jar$", "");
        LOGGER.trace("Constructed class name '{}'", className);
    
        try {
            Class<?> pluginClass = loader.loadClass(className);
            LOGGER.trace("Loaded class '{}'", pluginClass);
            Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
            LOGGER.trace("Created new instance of plugin '{}'", plugin);
            this.pluginRegistry.put(pluginID, plugin);
            LOGGER.trace("Added plugin-in {}", pluginID);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Class '{}' not found in file '{}'", className, libFile, e);
            throw e;
        }
    }
    
    @Override
    public void applyPlugin(String pluginId, Object target) {
        Plugin plugin = this.pluginRegistry.get(pluginId);
        LOGGER.trace("Retrieved plugin '{}' from registry '{}'", plugin, this.pluginRegistry);
        plugin.apply(target);
        LOGGER.trace("Applied plugin '{}' on target '{}'", pluginId, target);
    }
}