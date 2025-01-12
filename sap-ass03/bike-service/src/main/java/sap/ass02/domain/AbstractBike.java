package sap.ass02.domain;

/**
 * Abstract class representing a bike
 */
public abstract class AbstractBike {
    protected final String id;
    protected BikeState state;
    protected P2d location;
    protected V2d direction;
    protected double speed;
    protected int batteryLevel;
    
    /**
     * Constructor
     * @param id the bike id
     */
    public AbstractBike(String id) {
        this.id = id;
        this.state = BikeState.AVAILABLE;
        this.location = new P2d(0, 0);
        this.direction = new V2d(1, 0);
        this.speed = 0;
        this.batteryLevel = 1;
    }
    
    /**
     * Constructor
     * @param id the bike id
     * @param state the bike state
     * @param location the bike location
     * @param direction the bike direction
     * @param speed the bike speed
     * @param batteryLevel the bike battery level
     */
    public AbstractBike(String id, BikeState state, P2d location, V2d direction, double speed, int batteryLevel) {
        this.id = id;
        this.state = state;
        this.location = location;
        this.direction = direction;
        this.speed = speed;
        if (batteryLevel < 0) {
            this.batteryLevel = 0;
        } else if (batteryLevel > 100) {
            this.batteryLevel = 100;
        } else {
            this.batteryLevel = batteryLevel;
        }
    }
    
    /**
     * Enum representing the bike state
     */
    public enum BikeState {AVAILABLE, IN_USE, MOVING_TO_STATION, AT_STATION, MAINTENANCE, START_AUTONOMOUSLY_REACH_STATION, START_AUTONOMOUSLY_REACH_USER, MOVING_TO_USER, AT_USER}
    
    /**
     * Get the bike id
     * @return the bike id
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * Get the bike state
     * @return the bike state
     */
    public BikeState getState() {
        return this.state;
    }
    
    /**
     * Recharge the bike battery
     */
    public void rechargeBattery() {
        this.batteryLevel = 100;
    }
    
    /**
     * Get the bike battery level
     * @return the bike battery level
     */
    public int getBatteryLevel() {
        return this.batteryLevel;
    }
    
    /**
     * Decrease the bike battery level
     * @param delta the delta to decrease
     */
    public void decreaseBatteryLevel(int delta) {
        this.batteryLevel -= delta;
        if (this.batteryLevel < 0) {
            this.batteryLevel = 0;
            this.state = BikeState.MAINTENANCE;
        }
    }
    
    /**
     * Check if the bike is available
     * @return true if the bike is available, false otherwise
     */
    public boolean isAvailable() {
        return this.state.equals(BikeState.AVAILABLE);
    }
    
    /**
     * Update the bike state
     * @return the new state
     */
    public void updateState(BikeState state) {
        this.state = state;
    }
    
    /**
     * Update the bike location
     * @param newLoc the new location
     */
    public void updateLocation(P2d newLoc) {
        this.location = newLoc;
    }
    
    /**
     * Update the bike location
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void updateLocation(double x, double y) {
        this.location = new P2d(x, y);
    }
    
    /**
     * Update the bike speed
     * @param speed the new speed
     */
    public void updateSpeed(double speed) {
        this.speed = speed;
    }
    
    /**
     * Update the bike direction
     * @param dir the new direction
     */
    public void updateDirection(V2d dir) {
        this.direction = dir;
    }
    
    /**
     * Update the bike direction
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void updateDirection(double x, double y) {
        this.direction = new V2d(x, y);
    }
    
    /**
     * Update the bike battery level
     * @param batteryLevel the new battery level
     */
    public void updateBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    /**
     * Get the bike speed
     * @return the bike speed
     */
    public double getSpeed() {
        return this.speed;
    }
    
    /**
     * Get the bike direction
     * @return the bike direction
     */
    public V2d getDirection() {
        return new V2d(this.direction.x(), this.direction.y());
    }
    
    /**
     * Get the bike location
     * @return the bike location
     */
    public P2d getLocation() {
        return new P2d(this.location.getX(), this.location.getY());
    }
    
    /**
     * Converts the bike to a string
     * @return the string representation of the bike
     */
    public String toString() {
        return "{ id: " + this.id + ", loc: " + this.location + ", batteryLevel: " + this.batteryLevel + ", state: " + this.state + " }";
    }
    
}
