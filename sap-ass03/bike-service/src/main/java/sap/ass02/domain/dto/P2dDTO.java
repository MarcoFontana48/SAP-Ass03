package sap.ass02.domain.dto;

import sap.ass02.domain.ValueObject;

/**
 * 2-dimensional point
 * objects are completely state-less
 */
public record P2dDTO(double x, double y) implements java.io.Serializable, ValueObject {
    
    /**
     * Sum of two points
     * @param v
     * @return
     */
    public P2dDTO sum(V2dDTO v) {
        return new P2dDTO(this.x + v.x(), this.y + v.y());
    }
    
    /**
     * Subtraction of two points
     * @param v
     * @return
     */
    public V2dDTO sub(P2dDTO v) {
        return new V2dDTO(this.x - v.x(), this.y - v.y());
    }
    
    /**
     * converts the point to a string
     */
    public String toString() {
        return "P2dDTO(" + this.x + "," + this.y + ")";
    }
}
