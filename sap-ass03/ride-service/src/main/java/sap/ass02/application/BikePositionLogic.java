package sap.ass02.application;

import sap.ass02.domain.EBike;
import sap.ass02.domain.P2d;
import sap.ass02.domain.V2d;

/**
 * Logic for updating the position of a bike.
 */
public final class BikePositionLogic {
    private static final int MAX_EBIKE_POSITION = 200;
    
    /**
     * Updates the position of the bike.
     *
     * @param bike the bike to update
     * @return the new direction of the bike
     */
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
    
    /**
     * Updates the y position of the bike.
     *
     * @param bike the bike to update
     * @param currentEBikePosition the current position of the bike
     * @param currentEBikeDirection the current direction of the bike
     */
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
    
    /**
     * Updates the x position of the bike.
     *
     * @param bike the bike to update
     * @param currentEBikePosition the current position of the bike
     * @param currentEBikeDirection the current direction of the bike
     */
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
