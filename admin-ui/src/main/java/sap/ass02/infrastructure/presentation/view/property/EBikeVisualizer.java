package sap.ass02.infrastructure.presentation.view.property;

import sap.ass02.domain.EBike;

/**
 * Interface for classes that can show EBikes
 */
public interface EBikeVisualizer {
    /**
     * Add an EBike to the view
     *
     * @param ebike EBike to add
     */
    void addEBikeToShow(EBike ebike);
}
