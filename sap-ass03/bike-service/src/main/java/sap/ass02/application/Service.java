package sap.ass02.application;

import sap.ass02.domain.EBike;

/**
 * Interface for services.
 */
public interface Service {
    /**
     * Adds an ebike to the repository
     * @param ebikeId the ebike id
     */
    boolean addBike(final String ebikeId);
    
    /**
     * Gets an ebike from the repository
     * @param eBikeId the ebike id
     * @return the user
     */
    EBike getEBike(String eBikeId);
    
    /**
     * Updates the ebike in the repository given the ebike
     * @param eBike the ebike
     */
    boolean updateEBike(EBike eBike);
    
    /**
     * Gets all the ebikes from the repository
     * @return the ebikes
     */
    Iterable<EBike> getEBikes();
    
    /**
     * Attach a repository to the service
     * @param repository the repository
     */
    void attachRepository(Repository repository);
}
