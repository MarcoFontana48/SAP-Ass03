package sap.ass02.domain.dto;

import sap.ddd.ValueObject;

public record EBikeDTO(String id, EBikeStateDTO state, P2dDTO location, V2dDTO direction, double speed, int batteryLevel) implements ValueObject {
    public enum EBikeStateDTO {
        AVAILABLE, IN_USE, MAINTENANCE
    }
}
