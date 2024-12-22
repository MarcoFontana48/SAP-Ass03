package sap.ass02.domain.dto;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.property.Jsonifyable;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.ValueObject;

import java.util.Optional;

public record RideDTO(java.sql.Date startedDate, Optional<java.sql.Date> endDate, UserDTO user, EBikeDTO ebike, boolean ongoing, String id) implements Jsonifyable, ValueObject {

    @Override
    public String toJsonString() {
        JsonObject jsonObject = new JsonObject()
                .put(JsonFieldKey.RIDE_START_DATE_KEY, this.startedDate.toString())
                .put(JsonFieldKey.RIDE_END_DATE_KEY, this.endDate.map(java.sql.Date::toString).orElse(null))
                .put(JsonFieldKey.RIDE_USER_ID_KEY, this.user.id())
                .put(JsonFieldKey.RIDE_EBIKE_ID_KEY, this.ebike.id())
                .put(JsonFieldKey.RIDE_ONGONING_KEY, this.ongoing)
                .put(JsonFieldKey.RIDE_ID_KEY, this.id);
//                .put(JsonFieldKey.RIDE_USER_KEY, this.user)
//                .put(JsonFieldKey.RIDE_EBIKE_KEY, this.ebike);
        return jsonObject.encode();
    }
}
