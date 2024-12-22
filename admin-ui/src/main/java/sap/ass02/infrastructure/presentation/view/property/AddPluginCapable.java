package sap.ass02.infrastructure.presentation.view.property;

/**
 * Interface for classes that can add plugin listeners
 * @param <L> the listener type
 */
public interface AddPluginCapable<L> {
    /**
     * Adds a plugin listener
     * @param listener the listener to add
     */
    void addPluginListener(final L listener);
}
