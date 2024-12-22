package sap.ass02.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ddd.Service;

public final class RideSimulation extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(RideSimulation.class);
    private static final int MAX_EBIKE_POSITION = 200;
    private static final int THREAD_SLEEP_MILLIS = 20;
    private final Ride ride;
    private final User user;
    
    private final Service service;
    private volatile boolean stopped;
    
    public RideSimulation(Ride ride, User user, Service service) {
        this.ride = ride;
        this.user = user;
        this.service = service;
        this.stopped = false;
    }
    
    public void run() {
        LOGGER.trace("Starting ride simulation for ride '{}'", this.ride);
        
        LOGGER.trace("Starting ride simulation for eBike '{}'", this.ride.getEBike().toString());
        int ebikeSpeed = 1;
        LOGGER.trace("Setting eBike '{}' speed to '{}'", this.ride.getEBike().getId(), ebikeSpeed);
        this.ride.getEBike().updateSpeed(ebikeSpeed);
        
        var lastTimeDecreasedCredit = System.currentTimeMillis();
        int decreaseCreditAmount = 1;
        LOGGER.trace("Decreasing user '{}' credit by '{}'", this.user.getId(), decreaseCreditAmount);
        this.user.decreaseCredit(decreaseCreditAmount);
        
        var lastTimeChangedDir = System.currentTimeMillis();
        var lastTimeDecreasedBattery = System.currentTimeMillis();
        
        while (!this.stopped) {
            
            /* update position */
            LOGGER.trace("Updating eBike '{}' position to '{}'", this.ride.getEBike().getId(), this.ride.getEBike().getLocation());
            var direction = this.updatePosition(this.ride.getEBike());
            
            /* change direction randomly */
            LOGGER.trace("Changing eBike '{}' direction to '{}'", this.ride.getEBike().getId(), this.ride.getEBike().getDirection());
            this.changeDirectionRandomly(lastTimeChangedDir, this.ride.getEBike(), direction);
            
            LOGGER.trace("Updating eBike '{}' in repository", this.ride.getEBike().getId());
            this.service.updateEBike(this.ride);
            
            /* update credit */
            LOGGER.trace("Updating user '{}' credit to '{}'", this.user.getId(), this.user.getCredit());
            lastTimeDecreasedCredit = this.updateCredit(lastTimeDecreasedCredit);
            
            /* decrease battery level */
            LOGGER.trace("Decreasing eBike '{}' battery level to '{}'", this.ride.getEBike().getId(), this.ride.getEBike().getBatteryLevel());
            lastTimeDecreasedBattery = this.updateBattery(lastTimeDecreasedBattery);
            
            if (this.ride.getEBike().getState() == EBike.EBikeState.MAINTENANCE || this.user.getCredit() <= 0) {
                if (this.user.getCredit() <= 0) {
                    this.ride.getEBike().updateState(EBike.EBikeState.AVAILABLE);
                    this.service.updateEBike(this.ride);
                }
                this.service.stopRide(this.ride.getId());
                this.stopSimulation();
            }

//            this.app.refreshView();
            
            try {
                Thread.sleep(THREAD_SLEEP_MILLIS);
            } catch (Exception ignored) {
            }
            
        }
    }
    
    private long updateBattery(long lastTimeDecreasedBattery) {
        var elapsedTimeSinceLastDecBattery = System.currentTimeMillis() - lastTimeDecreasedBattery;
        if (elapsedTimeSinceLastDecBattery > 3000) {
            this.ride.getEBike().decreaseBatteryLevel(1);
            lastTimeDecreasedBattery = System.currentTimeMillis();
            LOGGER.trace("Updating eBike '{}' in repository", this.ride.getEBike().getId());
            this.service.updateEBike(this.ride);
        }
        return lastTimeDecreasedBattery;
    }
    
    private V2d updatePosition(EBike eBike) {
        var eBikeLocation = eBike.getLocation();
        var eBikeDirection = eBike.getDirection();
        var eBikeSpeed = eBike.getSpeed();
        eBike.updateLocation(eBikeLocation.sum(eBikeDirection.mul(eBikeSpeed)));
        eBikeLocation = eBike.getLocation();
        this.updateXPosition(eBike, eBikeLocation, eBikeDirection);
        this.updateYPosition(eBike, eBikeLocation, eBikeDirection);
        return eBikeDirection;
    }
    
    private void updateYPosition(EBike eBike, P2d currentEBikePosition, V2d currentEBikeDirection) {
        if (currentEBikePosition.getY() > MAX_EBIKE_POSITION || currentEBikePosition.getY() < -MAX_EBIKE_POSITION) {
            eBike.updateDirection(new V2d(currentEBikeDirection.x(), -currentEBikeDirection.y()));
            if (currentEBikePosition.getY() > MAX_EBIKE_POSITION) {
                eBike.updateLocation(new P2d(currentEBikePosition.getX(), MAX_EBIKE_POSITION));
            } else {
                eBike.updateLocation(new P2d(currentEBikePosition.getX(), -MAX_EBIKE_POSITION));
            }
        }
    }
    
    private void updateXPosition(EBike eBike, P2d currentEBikePosition, V2d currentEBikeDirection) {
        if (currentEBikePosition.getX() > MAX_EBIKE_POSITION || currentEBikePosition.getX() < -MAX_EBIKE_POSITION) {
            eBike.updateDirection(new V2d(-currentEBikeDirection.x(), currentEBikeDirection.y()));
            if (currentEBikePosition.getX() > MAX_EBIKE_POSITION) {
                eBike.updateLocation(new P2d(MAX_EBIKE_POSITION, currentEBikePosition.getY()));
            } else {
                eBike.updateLocation(new P2d(-MAX_EBIKE_POSITION, currentEBikePosition.getY()));
            }
        }
    }
    
    private void changeDirectionRandomly(long lastTimeChangedDir, EBike eBike, V2d direction) {
        var elapsedTimeSinceLastChangeDir = System.currentTimeMillis() - lastTimeChangedDir;
        if (elapsedTimeSinceLastChangeDir > 500) {
            double angle = Math.random() * 60 - 30;
            eBike.updateDirection(direction.rotate(angle));
            elapsedTimeSinceLastChangeDir = System.currentTimeMillis();
        }
    }
    
    private long updateCredit(long lastTimeDecreasedCredit) {
        var elapsedTimeSinceLastDecredit = System.currentTimeMillis() - lastTimeDecreasedCredit;
        if (elapsedTimeSinceLastDecredit > 1000) {
            this.user.decreaseCredit(1);
            lastTimeDecreasedCredit = System.currentTimeMillis();
            LOGGER.trace("Updating user '{}' in repository", this.user.getId());
            this.service.updateUserCredits(this.ride);
        }
        return lastTimeDecreasedCredit;
    }
    
    public void stopSimulation() {
        this.stopped = true;
        this.interrupt();
    }
    
    public Ride getRide() {
        return this.ride;
    }
}
