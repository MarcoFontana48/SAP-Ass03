package sap.ass02.domain.dto;

import sap.ddd.ValueObject;

/**
 * Data Transfer Object for User
 */
public record UserDTO(String id, int credit) implements ValueObject {
}
