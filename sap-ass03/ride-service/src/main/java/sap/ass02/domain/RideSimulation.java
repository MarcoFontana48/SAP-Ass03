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
        
        LOGGER.trace("Starting ride simulation for bike '{}'", this.ride.getBike().toString());
        int ebikeSpeed = 1;
        LOGGER.trace("Setting bike '{}' speed to '{}'", this.ride.getBike().getBikeId(), ebikeSpeed);
        this.ride.getBike().updateSpeed(ebikeSpeed);
        
        var lastTimeDecreasedCredit = System.currentTimeMillis();
        int decreaseCreditAmount = 1;
        LOGGER.trace("Decreasing user '{}' credit by '{}'", this.user.getId(), decreaseCreditAmount);
        this.user.decreaseCredit(decreaseCreditAmount);
        
        var lastTimeChangedDir = System.currentTimeMillis();
        var lastTimeDecreasedBattery = System.currentTimeMillis();
        
        while (!this.stopped) {
            
            /* update position */
            LOGGER.trace("Updating bike '{}' position to '{}'", this.ride.getBike().getBikeId(), this.ride.getBike().getLocation());
            var direction = this.updatePosition(this.ride.getBike());
            
            /* change direction randomly */
            LOGGER.trace("Changing bike '{}' direction to '{}'", this.ride.getBike().getBikeId(), this.ride.getBike().getDirection());
            this.changeDirectionRandomly(lastTimeChangedDir, this.ride.getBike(), direction);
            
            LOGGER.trace("Updating bike '{}' in repository", this.ride.getBike().getBikeId());
            this.service.updateEBike(this.ride);
            
            /* update credit */
            LOGGER.trace("Updating user '{}' credit to '{}'", this.user.getId(), this.user.getCredit());
            lastTimeDecreasedCredit = this.updateCredit(lastTimeDecreasedCredit);
            
            /* decrease battery level */
            LOGGER.trace("Decreasing bike '{}' battery level to '{}'", this.ride.getBike().getBikeId(), this.ride.getBike().getBatteryLevel());
            lastTimeDecreasedBattery = this.updateBattery(lastTimeDecreasedBattery);
            
            if (this.ride.getBike().getBikeState() == AbstractBike.BikeState.MAINTENANCE || this.user.getCredit() <= 0) {
                if (this.user.getCredit() <= 0) {
                    this.ride.getBike().updateState(AbstractBike.BikeState.AVAILABLE);
                    this.service.updateEBike(this.ride);
                }
                this.service.stopRide(this.ride.getId());
                if (this.ride.getBike() instanceof ABike) {
                    ((ABike) this.ride.getBike()).start();
                }
//                this.stopSimulation();
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
            this.ride.getBike().decreaseBatteryLevel(1);
            lastTimeDecreasedBattery = System.currentTimeMillis();
            LOGGER.trace("Updating bike '{}' in repository", this.ride.getBike().getBikeId());
            this.service.updateEBike(this.ride);
        }
        return lastTimeDecreasedBattery;
    }
    
    private V2d updatePosition(AbstractBike bike) {
        var eBikeLocation = bike.getLocation();
        var eBikeDirection = bike.getDirection();
        var eBikeSpeed = bike.getSpeed();
        bike.updateLocation(eBikeLocation.sum(eBikeDirection.mul(eBikeSpeed)));
        eBikeLocation = bike.getLocation();
        this.updateXPosition(bike, eBikeLocation, eBikeDirection);
        this.updateYPosition(bike, eBikeLocation, eBikeDirection);
        return eBikeDirection;
    }
    
    private void updateYPosition(AbstractBike bike, P2d currentEBikePosition, V2d currentEBikeDirection) {
        if (currentEBikePosition.getY() > MAX_EBIKE_POSITION || currentEBikePosition.getY() < -MAX_EBIKE_POSITION) {
            bike.updateDirection(new V2d(currentEBikeDirection.x(), -currentEBikeDirection.y()));
            if (currentEBikePosition.getY() > MAX_EBIKE_POSITION) {
                bike.updateLocation(new P2d(currentEBikePosition.getX(), MAX_EBIKE_POSITION));
            } else {
                bike.updateLocation(new P2d(currentEBikePosition.getX(), -MAX_EBIKE_POSITION));
            }
        }
    }
    
    private void updateXPosition(AbstractBike bike, P2d currentEBikePosition, V2d currentEBikeDirection) {
        if (currentEBikePosition.getX() > MAX_EBIKE_POSITION || currentEBikePosition.getX() < -MAX_EBIKE_POSITION) {
            bike.updateDirection(new V2d(-currentEBikeDirection.x(), currentEBikeDirection.y()));
            if (currentEBikePosition.getX() > MAX_EBIKE_POSITION) {
                bike.updateLocation(new P2d(MAX_EBIKE_POSITION, currentEBikePosition.getY()));
            } else {
                bike.updateLocation(new P2d(-MAX_EBIKE_POSITION, currentEBikePosition.getY()));
            }
        }
    }
    
    private void changeDirectionRandomly(long lastTimeChangedDir, AbstractBike bike, V2d direction) {
        var elapsedTimeSinceLastChangeDir = System.currentTimeMillis() - lastTimeChangedDir;
        if (elapsedTimeSinceLastChangeDir > 500) {
            double angle = Math.random() * 60 - 30;
            bike.updateDirection(direction.rotate(angle));
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
