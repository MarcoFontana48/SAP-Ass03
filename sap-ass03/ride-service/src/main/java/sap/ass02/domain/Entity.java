package sap.ass02.domain;

import sap.ass02.domain.property.Jsonifyable;

public interface Entity<T> extends Jsonifyable {
    T toDTO();
}
