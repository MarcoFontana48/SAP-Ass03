package sap.ass02.infrastructure.persistence.local;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.*;
import sap.ass02.domain.utils.JsonFieldKey;

import java.sql.Date;
import java.util.Optional;

public final class LocalJsonQueryRepositoryAdapter extends AbstractLocalJsonRepositoryAdapter {
    private static final Logger LOGGER = LogManager.getLogger(LocalJsonQueryRepositoryAdapter.class);
    private final AbstractLocalJsonRepositoryAdapter localJsonRepositoryAdapter = new LocalJsonRepositoryAdapter();
    
    @Override
    public void start() {
        this.vertx.eventBus().consumer("ride-started", message -> {
            LOGGER.trace("Received ride-started event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            
            UserDTO user = new UserDTO(
                    obj.getString(JsonFieldKey.USER_ID_KEY),
                    -1
            );
            
            EBikeDTO ebike = new EBikeDTO(
                    obj.getString(JsonFieldKey.EBIKE_ID_KEY),
                    BikeStateDTO.AVAILABLE,
                    new P2dDTO(
                            0,
                            0
                    ),
                    new V2dDTO(
                            0,
                            0
                    ),
                    0,
                    0
            );
            
            RideDTO ride = new RideDTO(
                Date.valueOf(obj.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                Optional.ofNullable(obj.getString(JsonFieldKey.RIDE_END_DATE_KEY)).map(Date::valueOf),
                user,
                ebike,
                obj.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                obj.getString(JsonFieldKey.RIDE_ID_KEY)
            );
            LOGGER.trace("retrieved ride: '{}'", ride.toJsonString());
            
            this.localJsonRepositoryAdapter.insertRide(ride);
        });
        
        this.vertx.eventBus().consumer("ride-ended", message -> {
            LOGGER.trace("Received insert-user event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            
            UserDTO user = new UserDTO(
                    obj.getString(JsonFieldKey.RIDE_USER_ID_KEY),
                    obj.getInteger(JsonFieldKey.USER_CREDIT_KEY)
            );
            
            EBikeDTO ebike = new EBikeDTO(
                    obj.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY),
                    BikeStateDTO.valueOf(obj.getString(JsonFieldKey.EBIKE_STATE_KEY)),
                    new P2dDTO(
                            obj.getInteger(JsonFieldKey.EBIKE_X_LOCATION_KEY),
                            obj.getInteger(JsonFieldKey.EBIKE_Y_LOCATION_KEY)
                    ),
                    new V2dDTO(
                            obj.getInteger(JsonFieldKey.EBIKE_X_DIRECTION_KEY),
                            obj.getInteger(JsonFieldKey.EBIKE_Y_DIRECTION_KEY)
                    ),
                    obj.getDouble(JsonFieldKey.EBIKE_SPEED_KEY),
                    obj.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY)
            );
            
            RideDTO ride = new RideDTO(
                new Date(Long.parseLong(obj.getString(JsonFieldKey.RIDE_START_DATE_KEY))),
                Optional.of(new Date(Long.parseLong(String.valueOf(obj.getInteger(JsonFieldKey.RIDE_END_DATE_KEY))))),
                user,
                ebike,
                obj.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                obj.getString(JsonFieldKey.RIDE_ID_KEY)
            );
            
            this.localJsonRepositoryAdapter.insertRide(ride);
        });
    }
}
