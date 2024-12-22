package sap.ass02.domain;

public class P2d {
    private double x;
    private double y;
    
    public P2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public P2d sum(V2d v) {
        return new P2d(this.x + v.getX(), this.y + v.getY());
    }
    
    public V2d sub(P2d v) {
        return new V2d(this.x - v.getX(), this.y - v.getY());
    }
    
    public String toString() {
        return "P2dDTO(" + this.x + "," + this.y + ")";
    }
    
    public double getX() {
        return this.x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
}
