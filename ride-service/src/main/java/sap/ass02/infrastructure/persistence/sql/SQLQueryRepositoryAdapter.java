package sap.ass02.infrastructure.persistence.sql;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.*;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.Repository;

import java.sql.Date;
import java.util.Optional;

public final class SQLQueryRepositoryAdapter extends AbstractSQLRepositoryAdapter {
    private static final Logger LOGGER = LogManager.getLogger(SQLQueryRepositoryAdapter.class);
    private final Repository sqlRepositoryAdapter = new SQLRepositoryAdapter();

    @Override
    public void start() {
        this.vertx.eventBus().consumer("insert-ride", message -> {
            LOGGER.trace("Received insert-rideDTO event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            RideDTO rideDTO = new RideDTO(
                    Date.valueOf(obj.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                    Optional.of(Date.valueOf(obj.getString(JsonFieldKey.RIDE_END_DATE_KEY))),
                    new UserDTO(obj.getString(JsonFieldKey.RIDE_USER_ID_KEY), obj.getInteger(JsonFieldKey.USER_CREDIT_KEY)),
                    new EBikeDTO(obj.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY), EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0,0), new V2dDTO(0,0), 0, 0),
                    obj.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                    obj.getString(JsonFieldKey.RIDE_ID_KEY)
            );
            this.sqlRepositoryAdapter.insertRide(rideDTO);
        });
        this.vertx.eventBus().consumer("update-ride-end", message -> {
            LOGGER.trace("Received update-ride event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            RideDTO rideDTO = new RideDTO(
                    Date.valueOf(obj.getString(JsonFieldKey.RIDE_START_DATE_KEY)),
                    Optional.of(Date.valueOf(obj.getString(JsonFieldKey.RIDE_END_DATE_KEY))),
                    new UserDTO(obj.getString(JsonFieldKey.RIDE_USER_ID_KEY), obj.getInteger(JsonFieldKey.USER_CREDIT_KEY)),
                    new EBikeDTO(obj.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY), EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0,0), new V2dDTO(0,0), 0, 0),
                    obj.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                    obj.getString(JsonFieldKey.RIDE_ID_KEY)
            );
            this.sqlRepositoryAdapter.updateRideEnd(rideDTO);
        });
    }
}
