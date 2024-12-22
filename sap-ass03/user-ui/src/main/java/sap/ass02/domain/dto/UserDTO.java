package sap.ass02.domain.dto;

import sap.ddd.ValueObject;

public record UserDTO(String id, int credit) implements ValueObject {
}
