package sap.ass02.infrastructure.persistence.sql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ddd.Repository;

import static sap.ass02.infrastructure.persistence.utils.EBikeDTOJsonSerializer.deserializeEBikeDTO;

public final class SQLQueryRepositoryAdapter extends AbstractSQLRepositoryAdapter {
    private static final Logger LOGGER = LogManager.getLogger(SQLQueryRepositoryAdapter.class);
    private final Repository sqlRepositoryAdapter = new SQLRepositoryAdapter();
    
    @Override
    public void start() {
        this.vertx.eventBus().consumer("insert-ebike", message -> {
            LOGGER.trace("Received insert-ebike event '{}'", message.body());
            EBikeDTO ebike = deserializeEBikeDTO(message);
            this.sqlRepositoryAdapter.insertEbike(ebike);
        });
        this.vertx.eventBus().consumer("ebike-update", message -> {
            LOGGER.trace("Received ebike-update event '{}'", message.body());
            EBikeDTO ebike = deserializeEBikeDTO(message);
            this.sqlRepositoryAdapter.updateEBike(ebike);
        });
    }
}
