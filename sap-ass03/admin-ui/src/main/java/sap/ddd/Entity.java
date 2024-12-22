package sap.ddd;

import sap.ass02.domain.property.Jsonifyable;

/**
 * Interface for entities
 * @param <T> the DTO type
 */
public interface Entity<T> extends Jsonifyable {
    /**
     * Get the DTO of the entity
     * @return the DTO
     */
    T toDTO();
}
