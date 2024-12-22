package sap.ass02.infrastructure.persistence.local;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.EBikeDTO;

import static sap.ass02.infrastructure.persistence.utils.EBikeDTOJsonSerializer.deserializeEBikeDTO;

/**
 * Proxy class for AbstractLocalJsonRepositoryAdapter to make it a query-only repository updated via events to implement CQRS pattern
 */
public final class LocalJsonQueryRepositoryAdapter extends AbstractLocalJsonRepositoryAdapter {
    private static final Logger LOGGER = LogManager.getLogger(LocalJsonQueryRepositoryAdapter.class);
    private final AbstractLocalJsonRepositoryAdapter localJsonRepositoryAdapter = new LocalJsonRepositoryAdaptor();
    
    @Override
    public void start() {
        this.vertx.eventBus().consumer("insert-ebike", message -> {
            LOGGER.trace("Received insert-ebike event '{}'", message.body());
            EBikeDTO ebike = deserializeEBikeDTO(message);
            this.localJsonRepositoryAdapter.insertEbike(ebike);
        });
        this.vertx.eventBus().consumer("ebike-update", message -> {
            LOGGER.trace("Received ebike-update event '{}'", message.body());
            EBikeDTO ebike = deserializeEBikeDTO(message);
            this.localJsonRepositoryAdapter.updateEBike(ebike);
        });
    }
}
