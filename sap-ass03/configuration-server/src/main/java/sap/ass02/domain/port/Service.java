package sap.ass02.domain.port;

import sap.ass02.domain.entity.MongoCredentials;
import sap.ass02.domain.entity.SQLCredentials;

/**
 * Service interface to provide the credentials for the services
 */
public interface Service extends Port {
    /**
     * Get the sql credentials for the user service
     * @return the credentials for the user service
     */
    SQLCredentials getUserServiceSqlCredentials();
    
    /**
     * Get the mongo credentials for the user service
     * @return the credentials for the user service
     */
    MongoCredentials getUserServiceMongoCredentials();
    
    /**
     * Get the sql credentials for the eBike service
     * @return the credentials for the eBike service
     */
    SQLCredentials getEBikeServiceSqlCredentials();
    
    /**
     * Get the mongo credentials for the eBike service
     * @return the credentials for the eBike service
     */
    MongoCredentials getEBikeServiceMongoCredentials();
}
