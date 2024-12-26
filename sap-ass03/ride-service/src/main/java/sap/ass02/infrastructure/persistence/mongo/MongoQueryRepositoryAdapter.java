package sap.ass02.infrastructure.persistence.mongo;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.*;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.ReadWriteRepository;

import java.sql.Date;
import java.util.Optional;

public class MongoQueryRepositoryAdapter extends AbstractMongoRepositoryAdapter {
    private static final Logger LOGGER = LogManager.getLogger(MongoQueryRepositoryAdapter.class);
    private final ReadWriteRepository mongoRepositoryAdapter = new MongoRepositoryAdapter();

    @Override
    public void start() {
        this.vertx.eventBus().consumer("ride-started", message -> {
            LOGGER.trace("Received ride-started event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            RideDTO ride = new RideDTO(
                new Date(Long.parseLong(obj.getString(JsonFieldKey.RIDE_START_DATE_KEY))),
                Optional.of(new Date(Long.parseLong(String.valueOf(obj.getInteger(JsonFieldKey.RIDE_END_DATE_KEY))))),
                new UserDTO(obj.getString(JsonFieldKey.RIDE_USER_ID_KEY), -1),
                new EBikeDTO(obj.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY), EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0,0), new V2dDTO(0,0), 0, 0),
                obj.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                obj.getString(JsonFieldKey.RIDE_ID_KEY)
            );
            this.mongoRepositoryAdapter.insertRide(ride);
        });
        this.vertx.eventBus().consumer("ride-ended", message -> {
            LOGGER.trace("Received ride-ended event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            RideDTO ride = new RideDTO(
                new Date(Long.parseLong(obj.getString(JsonFieldKey.RIDE_START_DATE_KEY))),
                Optional.of(new Date(Long.parseLong(String.valueOf(obj.getInteger(JsonFieldKey.RIDE_END_DATE_KEY))))),
                new UserDTO(obj.getString(JsonFieldKey.RIDE_USER_ID_KEY), -1),
                new EBikeDTO(obj.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY), EBikeDTO.EBikeStateDTO.AVAILABLE, new P2dDTO(0,0), new V2dDTO(0,0), 0, 0),
                obj.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                obj.getString(JsonFieldKey.RIDE_ID_KEY)
            );
            this.mongoRepositoryAdapter.updateRideEnd(ride);
        });
    }
}
