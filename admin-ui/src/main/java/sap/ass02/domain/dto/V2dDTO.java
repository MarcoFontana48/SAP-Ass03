/*
 *   V2dDTO.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */
package sap.ass02.domain.dto;

import sap.ddd.ValueObject;

/**
 * 2-dimensional vector
 * objects are completely state-less
 */
public record V2dDTO(double x, double y) implements java.io.Serializable, ValueObject {
    
    /**
     * Method to get the x coordinate of a vector
     * @return the x coordinate of this vector
     */
    public double x() {
        return this.x;
    }
    
    /**
     * Method to get the y coordinate of a vector
     * @return the y coordinate of this vector
     */
    public double y() {
        return this.y;
    }
    
    /**
     * Method to sum two vectors
     * @param v the vector to sum
     * @return the sum of this vector with v
     */
    public V2dDTO sum(V2dDTO v) {
        return new V2dDTO(this.x + v.x, this.y + v.y);
    }
    
    /**
     * Method to rotate two vectors of a certain degree
     * @param degree the degree of rotation
     * @return the rotation of this vector with v
     */
    public V2dDTO rotate(double degree) {
        var rad = degree * Math.PI / 180;
        var cs = Math.cos(rad);
        var sn = Math.sin(rad);
        var x1 = this.x * cs - this.y * sn;
        var y1 = this.x * sn + this.y * cs;
        var v = new V2dDTO(x1, y1).getNormalized();
        return v;
    }
    
    /**
     * Method to calculate the absolute value of a vector
     * @return the absolute value of this vector
     */
    public double abs() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    /**
     * Method to calculate the normalized vector
     * @return the normalized vector of this vector
     */
    public V2dDTO getNormalized() {
        double module = Math.sqrt(this.x * this.x + this.y * this.y);
        return new V2dDTO(this.x / module, this.y / module);
    }
    
    /**
     * Method to calculate the scalar product of two vectors
     * @param fact the vector to multiply
     * @return the scalar product of this vector with v
     */
    public V2dDTO mul(double fact) {
        return new V2dDTO(this.x * fact, this.y * fact);
    }
    
    /**
     * Method to return the string representation of a vector
     * @return the string representation of this vector
     */
    public String toString() {
        return "V2dDTO(" + this.x + "," + this.y + ")";
    }
    
}
