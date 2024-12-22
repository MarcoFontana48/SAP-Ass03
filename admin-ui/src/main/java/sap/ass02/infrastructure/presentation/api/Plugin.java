package sap.ass02.infrastructure.presentation.api;

/**
 * Interface for plugins
 */
public interface Plugin {
    void apply(Object target);
}