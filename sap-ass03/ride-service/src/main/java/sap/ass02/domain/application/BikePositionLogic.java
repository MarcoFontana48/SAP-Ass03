package sap.ass02.domain.application;

import sap.ass02.domain.EBike;
import sap.ass02.domain.P2d;
import sap.ass02.domain.V2d;

public final class BikePositionLogic {
    private static final int MAX_EBIKE_POSITION = 200;
    
    public static V2d updatePosition(EBike bike) {
        var eBikeLocation = bike.getLocation();
        var eBikeDirection = bike.getDirection();
        var eBikeSpeed = bike.getSpeed();
        bike.updateLocation(eBikeLocation.sum(eBikeDirection.mul(eBikeSpeed)));
        eBikeLocation = bike.getLocation();
        updateXPosition(bike, eBikeLocation, eBikeDirection);
        updateYPosition(bike, eBikeLocation, eBikeDirection);
        return eBikeDirection;
    }
    
    private static void updateYPosition(EBike bike, P2d currentEBikePosition, V2d currentEBikeDirection) {
        if (currentEBikePosition.getY() > MAX_EBIKE_POSITION || currentEBikePosition.getY() < -MAX_EBIKE_POSITION) {
            bike.updateDirection(new V2d(currentEBikeDirection.x(), -currentEBikeDirection.y()));
            if (currentEBikePosition.getY() > MAX_EBIKE_POSITION) {
                bike.updateLocation(new P2d(currentEBikePosition.getX(), MAX_EBIKE_POSITION));
            } else {
                bike.updateLocation(new P2d(currentEBikePosition.getX(), -MAX_EBIKE_POSITION));
            }
        }
    }
    
    private static void updateXPosition(EBike bike, P2d currentEBikePosition, V2d currentEBikeDirection) {
        if (currentEBikePosition.getX() > MAX_EBIKE_POSITION || currentEBikePosition.getX() < -MAX_EBIKE_POSITION) {
            bike.updateDirection(new V2d(-currentEBikeDirection.x(), currentEBikeDirection.y()));
            if (currentEBikePosition.getX() > MAX_EBIKE_POSITION) {
                bike.updateLocation(new P2d(MAX_EBIKE_POSITION, currentEBikePosition.getY()));
            } else {
                bike.updateLocation(new P2d(-MAX_EBIKE_POSITION, currentEBikePosition.getY()));
            }
        }
    }
}
