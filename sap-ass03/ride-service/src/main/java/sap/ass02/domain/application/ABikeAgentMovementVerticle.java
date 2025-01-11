package sap.ass02.domain.application;

import io.vertx.core.AbstractVerticle;
import sap.ass02.domain.*;
import sap.ddd.Service;

public final class ABikeAgentMovementVerticle extends AbstractVerticle implements VerticleAgent {
    private final ABike aBike;
    private final double perceptionRadius = 101;
    private final Service service;
    
    public ABikeAgentMovementVerticle(Ride ride, Service service) {
        this.aBike = new ABikeImpl(ride.getBike());
        this.service = service;
    }
    
    public void startToAutonomouslyReachNearestStation() {
        this.aBike.updateState(ABikeImpl.BikeState.START_AUTONOMOUSLY_REACH_STATION);
        this.service.updateEBike(this.aBike);
        this.vertx.eventBus().publish("abike-state-update-" + this.aBike.getBikeId(), ABikeImpl.BikeState.START_AUTONOMOUSLY_REACH_STATION.toString());
    }
    
    @Override
    public void start() {
        this.vertx.eventBus().consumer("abike-state-update-" + this.aBike.getBikeId(), message -> {
            if (message.body().equals(ABikeImpl.BikeState.START_AUTONOMOUSLY_REACH_STATION.toString()) || message.body().equals(ABikeImpl.BikeState.MAINTENANCE.toString())) {
                Station station = this.evaluateNearestStation();
                this.changeDirectionTowards(station);
            }
        });
        
        this.vertx.eventBus().consumer("abike-state-update-" + this.aBike.getBikeId(), message -> {
            if (message.body().equals(EBike.BikeState.MOVING_TO_STATION.toString())) {
                this.stepForward();
                
                // evaluates if a new nearest station is closer than the current one
                Station newStation = this.evaluateNearestStation();
                this.changeDirectionTowards(newStation);
                
                this.evaluateAgentPositionRelativeTo(newStation);
            }
        });
        
        this.vertx.eventBus().consumer("abike-state-update-" + this.aBike.getBikeId(), message -> {
            if (message.body().equals(EBike.BikeState.AT_STATION.toString())) {
                this.rechargeBike();
                this.stop();
            }
        });
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
