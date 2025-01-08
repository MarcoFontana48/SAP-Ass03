package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.BikeStateDTO;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.Entity;

import java.util.Objects;

public final class EBike extends AbstractBike  {
    public EBike(String id) {
        super(id);
    }
    
    public EBike(String id, BikeState state, P2d location, V2d direction, double speed, int batteryLevel) {
        super(id, state, location, direction, speed, batteryLevel);
    }
    
    public EBike(JsonObject asJsonObject) {
        this(
                asJsonObject.getString(JsonFieldKey.EBIKE_ID_KEY),
                BikeState.valueOf(asJsonObject.getString(JsonFieldKey.EBIKE_STATE_KEY)),
                new P2d(asJsonObject.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), asJsonObject.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY)),
                new V2d(asJsonObject.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), asJsonObject.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY)),
                asJsonObject.getDouble(JsonFieldKey.EBIKE_SPEED_KEY),
                asJsonObject.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY)
        );
    }
}
