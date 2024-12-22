package sap.ass02.domain;

/**
 * A 2D point
 */
public class P2d {
    private double x;
    private double y;
    
    /**
     * Constructor
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public P2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * sum of a point and a vector
     * @param v vector
     */
    public P2d sum(V2d v) {
        return new P2d(this.x + v.getX(), this.y + v.getY());
    }
    
    /**
     * difference of two points
     * @param v point
     */
    public V2d sub(P2d v) {
        return new V2d(this.x - v.getX(), this.y - v.getY());
    }
    
    /**
     * @return string representation of the point
     */
    public String toString() {
        return "P2dDTO(" + this.x + "," + this.y + ")";
    }
    
    /**
     * @return x-coordinate
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * sets x-coordinate
     * @param x x-coordinate
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * @return y-coordinate
     */
    public double getY() {
        return this.y;
    }
    
    /**
     * sets y-coordinate
     * @param y y-coordinate
     */
    public void setY(double y) {
        this.y = y;
    }
}
