package sap.ass02.domain;

/**
 * Represents a point in 2D space
 */
public class P2d {
    private double x;
    private double y;
    
    /**
     * Constructor
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public P2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Sum a vector to the point
     * @param v the vector
     * @return the new point
     */
    public P2d sum(V2d v) {
        return new P2d(this.x + v.getX(), this.y + v.getY());
    }
    
    /**
     * Subtract a vector from the point
     * @param v the vector
     * @return the new point
     */
    public V2d sub(P2d v) {
        return new V2d(this.x - v.getX(), this.y - v.getY());
    }
    
    /**
     * Get the x coordinate
     * @return the x coordinate
     */
    public String toString() {
        return "P2dDTO(" + this.x + "," + this.y + ")";
    }
    
    /**
     * Get the x coordinate
     * @return the x coordinate
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * Set the x coordinate
     * @param x the new x coordinate
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * Get the y coordinate
     * @return the y coordinate
     */
    public double getY() {
        return this.y;
    }
    
    /**
     * Set the y coordinate
     * @param y the new y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }
}
