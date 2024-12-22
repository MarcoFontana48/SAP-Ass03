package sap.ass02.infrastructure.presentation.view;

import sap.ass02.domain.port.View;

import java.util.Optional;

/**
 * Interface for the application view
 */
public interface AppView extends View {
    /**
     * Display the view
     */
    void display();
    
    /**
     * Sets up the view
     */
    void setup();
    
    /**
     * Refresh the view
     */
    void refresh();
    
    /**
     * Add a new effect
     *
     * @param s the effect to add
     */
    void addNewEffect(String s);
    
    /**
     * Get the visualizer panel
     *
     * @return the visualizer panel
     */
    Optional<VisualiserPanel> getVisualizerPanel();
}
