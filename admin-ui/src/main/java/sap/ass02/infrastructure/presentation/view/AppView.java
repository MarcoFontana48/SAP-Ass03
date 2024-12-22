package sap.ass02.infrastructure.presentation.view;

import sap.ass02.domain.port.View;

import java.util.Optional;

public interface AppView extends View {
    void display();
    
    void setup();
    
    void refresh();
    
    void addNewEffect(String s);
    
    Optional<VisualiserPanel> getVisualizerPanel();
}
