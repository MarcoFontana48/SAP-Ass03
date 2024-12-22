package sap.ass02.infrastructure.presentation.view;

import java.awt.*;

/**
 * Interface for the visualiser panel
 */
public interface VisualiserPanel {
    /**
     * Apply an effect to the visualiser panel
     *
     * @param pluginId the id of the plugin
     */
    void applyEffect(String pluginId);
    
    /**
     * Set the available color of the bike
     *
     * @param color the color
     */
    void setAvailableColorBike(Color color);
    
    /**
     * Set the in use color of the bike
     *
     * @param color the color
     */
    void setIsUseColorBike(Color color);
    
    /**
     * Set the mantainance color of the bike
     *
     * @param color the color
     */
    void setMantainanceColorBike(Color color);
    
    /**
     * Set the out of order color of the bike
     *
     * @param color the color
     */
    void setOutOfCreditsUserColor(Color color);
    
    /**
     * Set the out of credits color of the user
     *
     * @param color the color
     */
    void setDefaultUserColor(Color color);
}
