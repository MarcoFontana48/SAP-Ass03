package sap.ass02.infrastructure.persistence.local;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.UserDTO;
import sap.ass02.domain.utils.JsonFieldKey;

public final class LocalJsonQueryRepositoryAdapter extends AbstractLocalJsonRepositoryAdapter {
    private static final Logger LOGGER = LogManager.getLogger(LocalJsonQueryRepositoryAdapter.class);
    private final LocalJsonRepositoryAdapter localJsonRepositoryAdapter = new LocalJsonRepositoryAdapter();
    
    @Override
    public void start() {
        this.vertx.eventBus().consumer("insert-user", message -> {
            LOGGER.trace("Received insert-user event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            UserDTO user = new UserDTO(obj.getString(JsonFieldKey.USER_ID_KEY), obj.getInteger(JsonFieldKey.USER_CREDIT_KEY), obj.getDouble(JsonFieldKey.USER_X_LOCATION_KEY), obj.getDouble(JsonFieldKey.USER_Y_LOCATION_KEY));
            this.localJsonRepositoryAdapter.insertUser(user);
        });
        this.vertx.eventBus().consumer("update-user-credits", message -> {
            LOGGER.trace("Received update-user-credits event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            UserDTO user = new UserDTO(obj.getString(JsonFieldKey.USER_ID_KEY), obj.getInteger(JsonFieldKey.USER_CREDIT_KEY), obj.getDouble(JsonFieldKey.USER_X_LOCATION_KEY), obj.getDouble(JsonFieldKey.USER_Y_LOCATION_KEY));
            this.localJsonRepositoryAdapter.updateUserCredits(user.id(), user.credit());
        });
    }
}
