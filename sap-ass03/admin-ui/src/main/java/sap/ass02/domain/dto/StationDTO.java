package sap.ass02.domain.dto;

import sap.ddd.ValueObject;

public record StationDTO(P2dDTO location) implements ValueObject {
}
