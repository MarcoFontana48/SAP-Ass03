package sap.ass02.domain.application;

import io.vertx.core.AbstractVerticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.AbstractBike;
import sap.ass02.domain.EBike;
import sap.ass02.domain.P2d;
import sap.ass02.domain.V2d;
import sap.ass02.domain.dto.DTOUtils;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ddd.ReadOnlyRepository;
import sap.ddd.ReadWriteRepository;
import sap.ddd.Service;

import java.util.ArrayList;
import java.util.List;

public final class BikeServiceVerticle extends AbstractVerticle implements ServiceVerticle {
    private static final Logger LOGGER = LogManager.getLogger(BikeServiceVerticle.class);
    private final Service bikeService = new BikeService();
    private ReadOnlyRepository queryOnlyRepository;
    
    /**
     * Adds an ebike to the repository
     *
     * @param ebikeId the ebike id
     */
    @Override
    public boolean addEBike(String ebikeId) {
        LOGGER.trace("Adding ebike with id '{}'", ebikeId);
        this.bikeService.addEBike(ebikeId);
        EBikeDTO addedEBike = new EBike(ebikeId).toDTO();
        LOGGER.trace("Publishing insert-ebike event '{}'", addedEBike.toJsonString());
        this.vertx.eventBus().publish("insert-ebike", addedEBike.toJsonString());
        return true;
    }
    
    /**
     * Gets an ebike from the repository
     *
     * @param eBikeId the ebike id
     * @return the user
     */
    @Override
    public EBike getEBike(String eBikeId) {
        LOGGER.trace("Getting ebike with id '{}'", eBikeId);
        var ebike = this.queryOnlyRepository.getEbikeById(eBikeId);
        return ebike.map(eBikeDTO -> new EBike(
                eBikeDTO.id(),
                AbstractBike.BikeState.valueOf(eBikeDTO.state().toString()),
                new P2d(eBikeDTO.location().x(), eBikeDTO.location().y()),
                new V2d(eBikeDTO.direction().x(), eBikeDTO.direction().y()),
                eBikeDTO.speed(),
                eBikeDTO.batteryLevel()
        )).orElse(null);
    }
    
    /**
     * Updates the ebike in the repository given the ebike
     *
     * @param eBike the ebike
     */
    @Override
    public boolean updateEBike(EBike eBike) {
        LOGGER.trace("Updating ebike with id '{}'", eBike.getId());
        this.bikeService.updateEBike(eBike);
        EBikeDTO ebikeDTO = new EBike(eBike.getId(), eBike.getState(), eBike.getLocation(), eBike.getDirection(), eBike.getSpeed(), eBike.getBatteryLevel()).toDTO();
        LOGGER.trace("Publishing update-ebike event '{}'", ebikeDTO.toJsonString());
        this.vertx.eventBus().publish("ebike-update", ebikeDTO.toJsonString());
        return true;
    }
    
    /**
     * Gets all the ebikes from the repository
     *
     * @return the ebikes
     */
    @Override
    public Iterable<EBike> getEBikes() {
        LOGGER.trace("Getting all ebikes from '{}' repository", this.queryOnlyRepository.getClass().getSimpleName());
        Iterable<EBikeDTO> allEBikesDTO = this.queryOnlyRepository.getAllEBikes();
        List<EBike> eBikes = new ArrayList<>();
        allEBikesDTO.forEach(eBikeDTO -> eBikes.add(DTOUtils.toEBike(eBikeDTO)));
        LOGGER.trace("Retrieved {} ebikes:", eBikes.size());
        eBikes.forEach(eBike -> LOGGER.trace("\t- {}", eBike.toJsonString()));
        return eBikes;
    }
    
    /**
     * Attach a repository to the service
     *
     * @param repository the repository
     */
    @Override
    public void attachRepository(ReadWriteRepository repository) {
        this.bikeService.attachRepository(repository);
    }
    
    /**
     * @param repository
     */
    @Override
    public void attachReadOnlyRepository(ReadOnlyRepository repository) {
        this.queryOnlyRepository = repository;
    }
}
