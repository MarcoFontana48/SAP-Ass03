package sap.ass02.domain.dto;

import sap.ddd.ValueObject;

/**
 * 2-dimensional point
 * objects are completely state-less
 */
public record P2dDTO(double x, double y) implements java.io.Serializable, ValueObject {
    
    /**
     * Sum of two points
     * @param v the point to be added
     * @return the sum of the two points
     */
    public P2dDTO sum(V2dDTO v) {
        return new P2dDTO(this.x + v.x(), this.y + v.y());
    }
    
    /**
     * Subtraction of two points
     * @param v the point to be subtracted
     * @return the subtraction of the two points
     */
    public V2dDTO sub(P2dDTO v) {
        return new V2dDTO(this.x - v.x(), this.y - v.y());
    }
    
    /**
     * String representation of the point
     * @return the string representation of the point
     */
    public String toString() {
        return "P2dDTO(" + this.x + "," + this.y + ")";
    }
}
