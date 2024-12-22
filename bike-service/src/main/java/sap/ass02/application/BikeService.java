package sap.ass02.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.EBike;
import sap.ass02.domain.dto.DTOUtils;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ddd.Repository;
import sap.ddd.Service;

import java.util.ArrayList;
import java.util.List;

public final class BikeService implements Service {
    private static final Logger LOGGER = LogManager.getLogger(BikeService.class);
    private Repository repository;
    
    @Override
    public boolean addEBike(final String ebikeId) {
        EBike ebike = new EBike(ebikeId);
        LOGGER.trace("Adding ebike with id '{}'", ebikeId);
        this.repository.insertEbike(ebike.toDTO());
        return true;
    }
    
    @Override
    public EBike getEBike(String eBikeId) {
        LOGGER.trace("Getting ebike with id '{}'", eBikeId);
        var ebike = this.repository.getEbikeById(eBikeId);
        return ebike.map(eBikeDTO -> new EBike(eBikeDTO.id())).orElse(null);
    }
    
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
    
    @Override
    public Iterable<EBike> getEBikes() {
        Iterable<EBikeDTO> allEBikesDTO = this.repository.getAllEBikes();
        List<EBike> eBikes = new ArrayList<>();
        allEBikesDTO.forEach(eBikeDTO -> eBikes.add(DTOUtils.toEBike(eBikeDTO)));
        LOGGER.trace("Retrieved {} ebikes:", eBikes.size());
        eBikes.forEach(eBike -> LOGGER.trace("\t- {}", eBike.toJsonString()));
        return eBikes;
    }
    
    @Override
    public void attachRepository(Repository repository) {
        this.repository = repository;
    }
}
