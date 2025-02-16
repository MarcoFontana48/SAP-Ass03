package sap.ass02.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EBike;
import sap.ass02.domain.dto.DTOUtils;
import sap.ass02.domain.dto.EBikeDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * service class for the bike micro-service
 */
public final class BikeService implements Service {
    private static final Logger LOGGER = LogManager.getLogger(BikeService.class);
    private Repository repository;
    
    /**
     * adds a bike to the repository
     * @param ebikeId the id of the bike to be added
     * @return true if the bike was added successfully
     */
    @Override
    public boolean addBike(final String ebikeId) {
        EBike ebike = new EBike(ebikeId);
        LOGGER.trace("Adding ebike with id '{}'", ebikeId);
        this.repository.insertEbike(ebike.toDTO());
        return true;
    }
    
    /**
     * gets a bike from the repository
     * @param eBikeId the id of the bike to be retrieved
     * @return the bike
     */
    @Override
    public EBike getEBike(String eBikeId) {
        LOGGER.trace("Getting ebike with id '{}'", eBikeId);
        var ebike = this.repository.getEbikeById(eBikeId);
        return ebike.map(eBikeDTO -> new EBike(eBikeDTO.id())).orElse(null);
    }
    
    /**
     * updates a bike in the repository
     * @param eBike the bike to be updated
     * @return true if the bike was updated successfully
     */
    @Override
    public boolean updateEBike(EBike eBike) {
        LOGGER.trace("Updating ebike with id '{}', to '{}'", eBike.getId(), eBike.toJsonString());
        var ebike = this.repository.getEbikeById(eBike.getId());
        if (ebike.isEmpty()) {
            return false;
        }
        this.repository.updateEBike(eBike.toDTO());
        return true;
    }
    
    /**
     * gets all the bikes from the repository
     * @return the bikes
     */
    @Override
    public Iterable<EBike> getEBikes() {
        Iterable<EBikeDTO> allEBikesDTO = this.repository.getAllEBikes();
        List<EBike> eBikes = new ArrayList<>();
        allEBikesDTO.forEach(eBikeDTO -> eBikes.add(DTOUtils.toEBike(eBikeDTO)));
        LOGGER.trace("Retrieved {} ebikes:", eBikes.size());
        eBikes.forEach(eBike -> LOGGER.trace("\t- {}", eBike.toJsonString()));
        return eBikes;
    }
    
    /**
     * attaches a repository to the service
     * @param repository the repository
     */
    @Override
    public void attachRepository(Repository repository) {
        this.repository = repository;
    }
}
