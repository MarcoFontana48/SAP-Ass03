package sap.ass02.domain.dto;

import sap.ddd.ValueObject;

/**
 * 2-dimensional point
 * objects are completely state-less
 */
public record P2dDTO(double x, double y) implements java.io.Serializable, ValueObject {
    
    /**
     * Sum of this point and a vector
     * @param v vector to add
     * @return new point
     */
    public P2dDTO sum(V2dDTO v) {
        return new P2dDTO(this.x + v.x(), this.y + v.y());
    }
    
    /**
     * Difference of this point and another point
     * @param v point to subtract
     * @return new vector
     */
    public V2dDTO sub(P2dDTO v) {
        return new V2dDTO(this.x - v.x(), this.y - v.y());
    }
    
    /**
     * String representation of this point
     * @return string representation
     */
    public String toString() {
        return "P2dDTO(" + this.x + "," + this.y + ")";
    }
}
