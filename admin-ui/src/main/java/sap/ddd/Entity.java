package sap.ddd;

import sap.ass02.domain.property.Jsonifyable;

public interface Entity<T> extends Jsonifyable {
    T toDTO();
}
