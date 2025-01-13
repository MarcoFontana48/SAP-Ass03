package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.StationDTO;
import sap.ass02.domain.property.Jsonifyable;
import sap.ddd.Entity;

/**
 * Record class to represent a station.
 */
public record Station(P2d location) implements Place, Entity<StationDTO>, Jsonifyable {
    /**
     * Returns the location of the station.
     */
    @Override
    public P2d location() {
        return this.location;
    }
    
    /**
     * Converts the object to a JSON string.
     * @return the JSON string
     */
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    /**
     * Converts the object to a JSON object.
     * @return the JSON object
     */
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("x", this.location.getX())
                .put("y", this.location.getY());
    }
    
    /**
     * Converts the object to a DTO.
     * @return the DTO
     */
    @Override
    public StationDTO toDTO() {
        return new StationDTO(new P2dDTO(this.location.getX(), this.location.getY()));
    }
}
