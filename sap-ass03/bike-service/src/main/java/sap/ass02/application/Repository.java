package sap.ass02.application;

import sap.ass02.domain.dto.EBikeDTO;

import java.util.Optional;

/**
 * Interface to implement repositories
 */
public interface Repository {
    /**
     * Initializes the repository
     */
    void init();

    /**
     * Inserts the given ebike inside the repository
     *
     * @param eBike
     */
    boolean insertEbike(EBikeDTO eBike);

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

    /**
     * Updates the eBike with the one passed as argument
     *
     * @param ebike
     * @return
     */
    boolean updateEBike(EBikeDTO ebike);
}
