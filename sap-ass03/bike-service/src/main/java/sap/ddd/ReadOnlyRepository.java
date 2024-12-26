package sap.ddd;

import sap.ass02.domain.dto.EBikeDTO;

import java.util.Optional;

public interface ReadOnlyRepository extends Repository {
    /**
     * Retrieves the ebike from the repository given its id
     *
     * @param ebikeId
     * @return Optionally found user
     */
    Optional<EBikeDTO> getEbikeById(final String ebikeId);
    
    /**
     * Retrieves all the ebikes stored inside the repository
     *
     * @return Iterable of found ebikes
     */
    Iterable<EBikeDTO> getAllEBikes();
    
}
