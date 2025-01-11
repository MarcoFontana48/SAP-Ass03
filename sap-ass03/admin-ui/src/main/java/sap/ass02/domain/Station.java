package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.StationDTO;
import sap.ass02.domain.property.Jsonifyable;
import sap.ddd.Entity;

public record Station(P2d location) implements Place, Entity<StationDTO>, Jsonifyable {
    @Override
    public P2d location() {
        return this.location;
    }
    
    @Override
    public StationDTO toDTO() {
        return new StationDTO(new P2dDTO(this.location.getX(), this.location.getY()));
    }
    
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
            .put("x", this.location.getX())
            .put("y", this.location.getY());
    }
}
