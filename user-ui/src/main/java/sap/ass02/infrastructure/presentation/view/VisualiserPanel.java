package sap.ass02.infrastructure.presentation.view;

import java.awt.*;

public interface VisualiserPanel {
    void applyEffect(String pluginId);
    
    void setAvailableColorBike(Color color);
    void setIsUseColorBike(Color color);
    void setMantainanceColorBike(Color color);
    
    void setOutOfCreditsUserColor(Color color);
    void setDefaultUserColor(Color color);
}
