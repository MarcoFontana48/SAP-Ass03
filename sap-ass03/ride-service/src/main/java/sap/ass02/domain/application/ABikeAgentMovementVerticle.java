package sap.ass02.domain.application;

import io.vertx.core.AbstractVerticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.*;
import sap.ddd.Service;

public final class ABikeAgentMovementVerticle extends AbstractVerticle implements VerticleAgent {
    private static final Logger LOGGER = LogManager.getLogger(ABikeAgentMovementVerticle.class);
    private final ABike aBike;
    private final double perceptionRadius = 101;
    private final Service service;
    private final User user;
    
    public ABikeAgentMovementVerticle(Ride ride, Service service) {
        this.aBike = new ABikeImpl(ride.getBike());
        this.user = ride.getUser();
        this.service = service;
    }
    
    public void startToAutonomouslyReachNearestStation() {
        this.aBike.updateState(ABikeImpl.BikeState.START_AUTONOMOUSLY_REACH_STATION);
        this.service.updateEBike(this.aBike);
        String eventKey = "abike-state-update-" + this.aBike.getBikeId();
        String eventValue = ABikeImpl.BikeState.START_AUTONOMOUSLY_REACH_STATION.toString();
        LOGGER.trace("Publishing event '{}','{}' to event bus", eventKey, eventValue);
        this.vertx.eventBus().publish(eventKey, eventValue);
    }
    
    public void reachUserAutonomously() {
        this.aBike.updateState(ABikeImpl.BikeState.START_AUTONOMOUSLY_REACH_USER);
        this.service.updateEBike(this.aBike);
        String eventKey = "abike-state-update-" + this.aBike.getBikeId();
        String eventValue = ABikeImpl.BikeState.START_AUTONOMOUSLY_REACH_USER.toString();
        LOGGER.trace("Publishing event '{}','{}' to event bus", eventKey, eventValue);
        this.vertx.eventBus().publish(eventKey, eventValue);
    }
    
    @Override
    public void start() {
        String eventKey = "abike-state-update-" + this.aBike.getBikeId();
        this.vertx.eventBus().consumer(eventKey, message -> {
            LOGGER.trace("Received event: '{}','{}'", eventKey, message.body());
            switch (EBike.BikeState.valueOf(message.body().toString())) {
                case START_AUTONOMOUSLY_REACH_STATION, MAINTENANCE:
                    Station station = this.evaluateNearestStation();
                    this.changeDirectionTowards(station);
                    break;
                case MOVING_TO_STATION:
                    this.stepForward();
                    Station newStation = this.evaluateNearestStation(); // evaluates if a new nearest station is closer than the current one
                    this.changeDirectionTowards(newStation);
                    this.evaluateAgentPositionRelativeTo(newStation);
                    break;
                case AT_STATION:
                    this.rechargeBike();
                    this.stop();
                    break;
                case START_AUTONOMOUSLY_REACH_USER:
                    User user = this.findUser();
                    this.changeDirectionTowards(user);
                    break;
                case MOVING_TO_USER:
                    this.stepForward();
                    this.evaluateAgentPositionRelativeTo(this.user);
                    break;
                case AT_USER:
                    this.stop();
                    break;
                default:
                    LOGGER.warn("Unhandled bike state: {}", message.body());
                    break;
            }
        });
    }
    
    private User findUser() {
        return this.service.getUser(this.user.getId());
    }
    
    private void rechargeBike() {
        this.aBike.updateState(ABikeImpl.BikeState.AVAILABLE);
        this.aBike.rechargeBattery();
        this.service.updateEBike(this.aBike);
    }
    
    private void evaluateAgentPositionRelativeTo(Place place) {
        if (this.aBike.getLocation().getX() == place.location().getX() && this.aBike.getLocation().getY() == place.location().getY()) {
            if (place instanceof Station) {
                this.aBike.updateState(ABikeImpl.BikeState.AT_STATION);
                this.service.updateEBike(this.aBike);
                this.vertx.eventBus().publish("abike-state-update-" + this.aBike.getBikeId(), ABikeImpl.BikeState.AT_STATION.toString());
            }
        }
    }
    
    private void evaluateAgentPositionRelativeTo(User user) {
        if (this.aBike.getLocation().getX() == user.getXLocation() && this.aBike.getLocation().getY() == user.getYLocation()) {
            this.aBike.updateState(ABikeImpl.BikeState.AT_USER);
            this.service.updateEBike(this.aBike);
            this.vertx.eventBus().publish("abike-state-update-" + this.aBike.getBikeId(), ABikeImpl.BikeState.AT_USER.toString());
        }
    }
    
    private void stepForward() {
        BikePositionLogic.updatePosition(this.aBike);
        this.service.updateEBike(this.aBike);
    }
    
    private void changeDirectionTowards(Place place) {
        this.aBike.updateDirection(new V2d(place.location().getX() - this.aBike.getLocation().getX(), place.location().getY() - this.aBike.getLocation().getY()));
        if (place instanceof Station) {
            this.aBike.updateState(ABikeImpl.BikeState.MOVING_TO_STATION);
            this.vertx.eventBus().publish("abike-state-update-" + this.aBike.getBikeId(), ABikeImpl.BikeState.MOVING_TO_STATION.toString());
            this.service.updateEBike(this.aBike);
        }
    }
    
    private void changeDirectionTowards(User user) {
        this.aBike.updateDirection(new V2d(user.getXLocation() - this.aBike.getLocation().getX(), user.getYLocation() - this.aBike.getLocation().getY()));
        this.aBike.updateState(ABikeImpl.BikeState.MOVING_TO_USER);
        this.vertx.eventBus().publish("abike-state-update-" + this.aBike.getBikeId(), ABikeImpl.BikeState.MOVING_TO_USER.toString());
        this.service.updateEBike(this.aBike);
    }
    
    public void stop() {
        this.vertx.undeploy(this.deploymentID());
    }
    
    private long getTime() {
        return System.currentTimeMillis();
    }
    
    private Station evaluateNearestStation() {
        Iterable<Station> stations = Environment.getStations();
        Station nearestStation = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Station station : stations) {
            double distance = Math.sqrt(Math.pow(this.aBike.getLocation().getX() - station.location().getX(), 2) + Math.pow(this.aBike.getLocation().getY() - station.location().getY(), 2));
            if (distance < minDistance && distance <= this.perceptionRadius) {
                minDistance = distance;
                nearestStation = station;
            }
        }
        
        return nearestStation;
    }
}
