package sap.ass02.domain;

/**
 * Value object for 2D vectors
 */
public class V2d implements java.io.Serializable {
    private double x;
    private double y;
    
    /**
     * Constructor
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public V2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return x-coordinate
     */
    public double x() {
        return this.x;
    }
    
    /**
     * @return y-coordinate
     */
    public double y() {
        return this.y;
    }
    
    /**
     * sum of this vector and v
     *
     * @param v vector to add
     * @return sum of this vector and v
     */
    public V2d sum(V2d v) {
        return new V2d(this.x + v.x, this.y + v.y);
    }
    
    /**
     * difference of this vector and v
     *
     * @param degree vector to subtract
     * @return difference of this vector and v
     */
    public V2d rotate(double degree) {
        var rad = degree * Math.PI / 180;
        var cs = Math.cos(rad);
        var sn = Math.sin(rad);
        var x1 = this.x * cs - this.y * sn;
        var y1 = this.x * sn + this.y * cs;
        var v = new V2d(x1, y1).getNormalized();
        return v;
    }
    
    /**
     * module of this vector
     */
    public double abs() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    /**
     * normalized vector
     */
    public V2d getNormalized() {
        double module = Math.sqrt(this.x * this.x + this.y * this.y);
        return new V2d(this.x / module, this.y / module);
    }
    
    /**
     * scalar product of this vector and v
     *
     * @param fact vector to multiply
     * @return scalar product of this vector and v
     */
    public V2d mul(double fact) {
        return new V2d(this.x * fact, this.y * fact);
    }
    
    /**
     * @return string representation of this vector
     */
    public String toString() {
        return "V2dDTO(" + this.x + "," + this.y + ")";
    }
    
    /**
     * @return x-coordinate
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * sets x-coordinate
     *
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
     *
     * @param y y-coordinate
     */
    public void setY(double y) {
        this.y = y;
    }
}
