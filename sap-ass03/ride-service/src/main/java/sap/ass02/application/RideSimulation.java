package sap.ass02.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.*;

/**
 * Class for simulating a ride.
 */
public final class RideSimulation extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(RideSimulation.class);
    private static final int THREAD_SLEEP_MILLIS = 20;
    private final Ride ride;
    private final User user;
    private final VerticleAgent aBikeAgent;
    private final Service service;
    private volatile boolean stopped;
    
    /**
     * Instantiates a new Ride simulation.
     * @param ride the ride
     * @param user the user
     * @param service the service
     * @param agent the agent
     */
    public RideSimulation(Ride ride, User user, Service service, VerticleAgent agent) {
        this.ride = ride;
        this.user = user;
        this.service = service;
        this.stopped = false;
        this.aBikeAgent = agent;
    }
    
    /**
     * Starts the ride simulation.
     */
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
            var direction = BikePositionLogic.updatePosition(this.ride.getBike());
            
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
            
            if (this.ride.getBike().getBikeState() == EBikeImpl.BikeState.MAINTENANCE || this.user.getCredit() <= 0) {
                if (this.user.getCredit() <= 0) {
                    this.ride.getBike().updateState(EBikeImpl.BikeState.AVAILABLE);
                    this.service.updateEBike(this.ride);
                }
                this.aBikeAgent.startToAutonomouslyReachNearestStation();
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
    
    /**
     * Updates the battery level of the bike.
     * @param lastTimeDecreasedBattery the last time the battery level was decreased
     * @return the last time the battery level was decreased
     */
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
    
    /**
     * Changes the direction of the bike randomly.
     * @param lastTimeChangedDir the last time the direction was changed
     * @param bike the bike
     * @param direction the direction
     */
    private void changeDirectionRandomly(long lastTimeChangedDir, EBike bike, V2d direction) {
        var elapsedTimeSinceLastChangeDir = System.currentTimeMillis() - lastTimeChangedDir;
        if (elapsedTimeSinceLastChangeDir > 500) {
            double angle = Math.random() * 60 - 30;
            bike.updateDirection(direction.rotate(angle));
            elapsedTimeSinceLastChangeDir = System.currentTimeMillis();
        }
    }
    
    /**
     * Updates the credit of the user.
     * @param lastTimeDecreasedCredit the last time the credit was decreased
     * @return the last time the credit was decreased
     */
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
    
    /**
     * Stops the ride simulation.
     */
    public void stopSimulation() {
        this.stopped = true;
        this.interrupt();
    }
    
    /**
     * Gets the ride.
     * @return the ride
     */
    public Ride getRide() {
        return this.ride;
    }
}
