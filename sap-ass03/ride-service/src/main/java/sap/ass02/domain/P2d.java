package sap.ass02.domain;

/**
 * A point in 2D space.
 */
public final class P2d {
    private double x;
    private double y;
    
    /**
     * Constructs a new point.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public P2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Adds a vector to the point.
     *
     * @param v the vector to add
     * @return the sum of the point and the vector
     */
    public P2d sum(V2d v) {
        return new P2d(this.x + v.getX(), this.y + v.getY());
    }
    
    /**
     * Subtracts a point from the point.
     *
     * @param v the point to subtract
     * @return the subtraction of the two points
     */
    public V2d sub(P2d v) {
        return new V2d(this.x - v.getX(), this.y - v.getY());
    }
    
    /**
     * String representation of the point.
     *
     * @return the string representation of the point
     */
    public String toString() {
        return "P2dDTO(" + this.x + "," + this.y + ")";
    }
    
    /**
     * gets the x-coordinate.
     * @return the x-coordinate
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * sets the x-coordinate.
     * @param x the new x-coordinate
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * gets the y-coordinate.
     * @return the y-coordinate
     */
    public double getY() {
        return this.y;
    }
    
    /**
     * sets the y-coordinate.
     * @param y the new y-coordinate
     */
    public void setY(double y) {
        this.y = y;
    }
}
