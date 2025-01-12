package sap.ass02.domain.dto;

/**
 * Enum representing the possible states of a bike
 */
public enum BikeStateDTO {
    AVAILABLE, IN_USE, MOVING_TO_STATION, AT_STATION, MAINTENANCE, START_AUTONOMOUSLY_REACH_STATION, START_AUTONOMOUSLY_REACH_USER, MOVING_TO_USER, AT_USER
}