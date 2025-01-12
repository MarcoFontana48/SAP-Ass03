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
     * @return the x coordinate
     */
    public double x() {
        return this.x;
    }
    
    /**
     * @return the y coordinate
     */
    public double y() {
        return this.y;
    }
    
    /**
     * @return the sum of this vector with the one passed as argument
     */
    public V2dDTO sum(V2dDTO v) {
        return new V2dDTO(this.x + v.x, this.y + v.y);
    }
    
    /**
     * @return the difference of this vector with the one passed as argument
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
     * @return the difference of this vector with the one passed as argument
     */
    public double abs() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    /**
     * @return the difference of this vector with the one passed as argument
     */
    public V2dDTO getNormalized() {
        double module = Math.sqrt(this.x * this.x + this.y * this.y);
        return new V2dDTO(this.x / module, this.y / module);
    }
    
    /**
     * @return the difference of this vector with the one passed as argument
     */
    public V2dDTO mul(double fact) {
        return new V2dDTO(this.x * fact, this.y * fact);
    }
    
    /**
     * @return the difference of this vector with the one passed as argument
     */
    public String toString() {
        return "V2dDTO(" + this.x + "," + this.y + ")";
    }
    
}
