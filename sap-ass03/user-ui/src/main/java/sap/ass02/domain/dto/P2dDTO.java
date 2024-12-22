package sap.ass02.domain.dto;

import sap.ddd.ValueObject;

/**
 * 2-dimensional point
 * objects are completely state-less
 */
public record P2dDTO(double x, double y) implements java.io.Serializable, ValueObject {
    
    public P2dDTO sum(V2dDTO v) {
        return new P2dDTO(this.x + v.x(), this.y + v.y());
    }
    
    public V2dDTO sub(P2dDTO v) {
        return new V2dDTO(this.x - v.x(), this.y - v.y());
    }
    
    public String toString() {
        return "P2dDTO(" + this.x + "," + this.y + ")";
    }
}
