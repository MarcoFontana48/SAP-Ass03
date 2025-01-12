package sap.ass02.domain.dto;

import sap.ass02.domain.AbstractBike;
import sap.ass02.domain.EBike;
import sap.ass02.domain.P2d;
import sap.ass02.domain.V2d;

/**
 * Utility class for DTOs
 */
public class DTOUtils {
    
    /**
     * Converts an ebike dto to an ebike
     * @param ebikeDTO the ebike DTO
     * @return the ebike
     */
    public static EBike toEBike(EBikeDTO ebikeDTO) {
        EBike ebike = new EBike(ebikeDTO.id());
        ebike.updateState(AbstractBike.BikeState.valueOf(ebikeDTO.state().toString()));
        ebike.updateLocation(new P2d(ebikeDTO.location().x(), ebikeDTO.location().y()));
        ebike.updateDirection(new V2d(ebikeDTO.direction().x(), ebikeDTO.direction().y()));
        ebike.updateSpeed(ebikeDTO.speed());
        ebike.updateBatteryLevel(ebikeDTO.batteryLevel());
        return ebike;
    }
}
