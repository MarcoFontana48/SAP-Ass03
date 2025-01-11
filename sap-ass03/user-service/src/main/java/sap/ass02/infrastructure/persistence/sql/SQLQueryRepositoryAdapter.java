package sap.ass02.infrastructure.persistence.sql;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.UserDTO;
import sap.ddd.ReadWriteRepository;

public final class SQLQueryRepositoryAdapter extends AbstractSQLRepositoryAdapter {
    private static final Logger LOGGER = LogManager.getLogger(SQLQueryRepositoryAdapter.class);
    private final ReadWriteRepository sqlRepositoryAdapter = new SQLRepositoryAdapter();

    @Override
    public void start() {
        this.vertx.eventBus().consumer("insert-user", message -> {
            LOGGER.trace("Received insert-user event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            UserDTO user = new UserDTO(obj.getString("id"), obj.getInteger("credit"), obj.getDouble("x_location"), obj.getDouble("y_location"));
            this.sqlRepositoryAdapter.insertUser(user);
        });
        this.vertx.eventBus().consumer("update-user-credits", message -> {
            LOGGER.trace("Received update-user-credits event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            UserDTO user = new UserDTO(obj.getString("id"), obj.getInteger("credit"), obj.getDouble("x_location"), obj.getDouble("y_location"));
            this.sqlRepositoryAdapter.updateUserCredits(user.id(), user.credit());
        });
    }
}
