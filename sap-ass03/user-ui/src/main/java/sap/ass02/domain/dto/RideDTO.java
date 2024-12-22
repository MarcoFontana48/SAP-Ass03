package sap.ass02.domain.dto;

import sap.ddd.ValueObject;

import java.util.Optional;

public record RideDTO(java.sql.Date startedDate, Optional<java.sql.Date> endDate, UserDTO user, EBikeDTO ebike, boolean ongoing, String id) implements ValueObject {
}
