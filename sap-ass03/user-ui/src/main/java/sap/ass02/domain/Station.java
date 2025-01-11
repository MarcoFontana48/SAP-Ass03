package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.StationDTO;
import sap.ass02.domain.property.Jsonifyable;
import sap.ddd.Entity;

public record Station(double x, double y) implements Entity<StationDTO>, Jsonifyable {
    public StationDTO toDTO() {
        return new StationDTO(this.x, this.y);
    }
    
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    private JsonObject toJsonObject() {
        return new JsonObject()
                .put("x", this.x)
                .put("y", this.y);
    }
}
