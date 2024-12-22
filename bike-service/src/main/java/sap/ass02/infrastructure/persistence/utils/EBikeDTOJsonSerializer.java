package sap.ass02.infrastructure.persistence.utils;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ass02.domain.utils.JsonFieldKey;

public final class EBikeDTOJsonSerializer {
    private static final Logger LOGGER = LogManager.getLogger(EBikeDTOJsonSerializer.class);
    
    public static EBikeDTO deserializeEBikeDTO(Message<Object> message) {
        JsonObject obj = new JsonObject(String.valueOf(message.body()));
        LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
        return new EBikeDTO(
                obj.getString(JsonFieldKey.EBIKE_ID_KEY),
                EBikeDTO.EBikeStateDTO.valueOf(obj.getString(JsonFieldKey.EBIKE_STATE_KEY)),
                new P2dDTO(obj.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), obj.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY)),
                new V2dDTO(obj.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), obj.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY)),
                obj.getDouble(JsonFieldKey.EBIKE_SPEED_KEY),
                obj.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY)
        );
    }
}
