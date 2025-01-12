package sap.ddd;

import sap.ass02.domain.property.Jsonifyable;

/**
 * Interface for entities.
 * @param <T> the DTO type
 */
public interface Entity<T> extends Jsonifyable {
    T toDTO();
}
