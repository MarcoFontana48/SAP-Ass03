package sap.ass02.infrastructure.persistence;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EBike;
import sap.ass02.domain.dto.EBikeDTO;

import java.util.Optional;

public final class ReadOnlyRepositoryAdapter extends AbstractVerticleReadOnlyRepository {
    private static final Logger LOGGER = LogManager.getLogger(ReadOnlyRepositoryAdapter.class);
    private final AbstractVerticleRepository readWriteRepository;
    
    public ReadOnlyRepositoryAdapter(AbstractVerticleRepository repository) {
        super();
        this.readWriteRepository = repository;
    }
    
    @Override
    public void start() {
        this.init();
        
        this.vertx.eventBus().consumer("insert-ebike", message -> {
            LOGGER.trace("Received vertx insert-ebike event '{}'", message.body());
            JsonObject ebikeJsonObject = new JsonObject(String.valueOf(message.body()));
            EBikeDTO ebike = new EBike(ebikeJsonObject).toDTO();
            this.insertEbike(ebike);
            LOGGER.trace("Inserted ebike '{}'", ebike);
        });
        
        this.vertx.eventBus().consumer("ebike-update", message -> {
            LOGGER.trace("Received vertx ebike-update event '{}'", message.body());
            JsonObject ebikeJsonObject = new JsonObject(String.valueOf(message.body()));
            this.readWriteRepository.updateEBike(new EBike(ebikeJsonObject).toDTO());
            LOGGER.trace("Updated ebike '{}'", ebikeJsonObject);
        });
    }
    
    private void init() {
        this.readWriteRepository.init();
    }
    
    /**
     * Retrieves the ebike from the repository given its id
     *
     * @param ebikeId
     * @return Optionally found user
     */
    @Override
    public Optional<EBikeDTO> getEbikeById(String ebikeId) {
        return Optional.empty();
    }
    
    /**
     * Retrieves all the ebikes stored inside the repository
     *
     * @return Iterable of found ebikes
     */
    @Override
    public Iterable<EBikeDTO> getAllEBikes() {
        return null;
    }
    
    private void insertEbike(EBikeDTO ride) {
        this.readWriteRepository.insertEbike(ride);
    }
}
