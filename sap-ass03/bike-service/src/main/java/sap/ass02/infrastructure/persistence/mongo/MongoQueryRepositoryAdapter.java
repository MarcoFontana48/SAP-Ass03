package sap.ass02.infrastructure.persistence.mongo;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ddd.ReadWriteRepository;

public class MongoQueryRepositoryAdapter extends AbstractMongoRepositoryAdapter {
    private static final Logger LOGGER = LogManager.getLogger(MongoQueryRepositoryAdapter.class);
    private final ReadWriteRepository mongoRepositoryAdapter = new MongoRepositoryAdapter();
    
    @Override
    public void start() {
        this.vertx.eventBus().consumer("insert-user", message -> {
            LOGGER.trace("Received insert-eBikeDTO event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            EBikeDTO eBikeDTO = new EBikeDTO(
                    obj.getString("id"),
                    EBikeDTO.EBikeStateDTO.valueOf(obj.getString("status")),
                    new P2dDTO(obj.getDouble("x_location"), obj.getInteger("y_location")),
                    new V2dDTO(obj.getDouble("x_direction"), obj.getDouble("y_direction")),
                    obj.getDouble("speed"),
                    obj.getInteger("battery"));
            this.mongoRepositoryAdapter.insertEbike(eBikeDTO);
        });
        this.vertx.eventBus().consumer("update-user-credits", message -> {
            LOGGER.trace("Received update-eBikeDTO-credits event '{}'", message.body());
            JsonObject obj = new JsonObject(String.valueOf(message.body()));
            LOGGER.trace("retrieved {}: '{}'", obj.getClass().getSimpleName(), obj);
            EBikeDTO eBikeDTO = new EBikeDTO(
                    obj.getString("id"),
                    EBikeDTO.EBikeStateDTO.valueOf(obj.getString("status")),
                    new P2dDTO(obj.getDouble("x_location"), obj.getInteger("y_location")),
                    new V2dDTO(obj.getDouble("x_direction"), obj.getDouble("y_direction")),
                    obj.getDouble("speed"),
                    obj.getInteger("battery")
            );
            this.mongoRepositoryAdapter.updateEBike(eBikeDTO);
        });
    }
}
