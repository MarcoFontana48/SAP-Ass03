package sap.ass02.application;

import sap.ass02.domain.entity.MongoCredentials;
import sap.ass02.domain.entity.SQLCredentials;
import sap.ass02.domain.port.Service;

/**
 * Service class to provide the credentials for the services
 */
public final class ServiceImpl implements Service {
    /**
     * Get the sql credentials for the user service
     * @return the credentials for the user service
     */
    @Override
    public SQLCredentials getUserServiceSqlCredentials() {
        return new SQLCredentials(
                System.getenv("USER_SERVICE_SQL_DB_HOST"),
                System.getenv("USER_SERVICE_SQL_DB_PORT"),
                System.getenv("USER_SERVICE_SQL_DB_NAME"),
                System.getenv("USER_SERVICE_SQL_DB_USER"),
                System.getenv("USER_SERVICE_SQL_DB_PASSWORD")
        );
    }
    
    /**
     * Get the mongo credentials for the user service
     * @return the credentials for the user service
     */
    @Override
    public MongoCredentials getUserServiceMongoCredentials() {
        return new MongoCredentials(
                System.getenv("USER_SERVICE_MONGO_DB_HOST"),
                System.getenv("USER_SERVICE_MONGO_DB_PORT"),
                System.getenv("USER_SERVICE_MONGO_DB_NAME"),
                System.getenv("USER_SERVICE_MONGO_DB_USER"),
                System.getenv("USER_SERVICE_MONGO_DB_PASSWORD")
        );
    }
    
    /**
     * Get the sql credentials for the eBike service
     * @return the credentials for the eBike service
     */
    @Override
    public SQLCredentials getEBikeServiceSqlCredentials() {
        return new SQLCredentials(
                System.getenv("EBIKE_SERVICE_SQL_DB_HOST"),
                System.getenv("EBIKE_SERVICE_SQL_DB_PORT"),
                System.getenv("EBIKE_SERVICE_SQL_DB_NAME"),
                System.getenv("EBIKE_SERVICE_SQL_DB_USER"),
                System.getenv("EBIKE_SERVICE_SQL_DB_PASSWORD")
        );
    }
    
    /**
     * Get the mongo credentials for the eBike service
     * @return the credentials for the eBike service
     */
    @Override
    public MongoCredentials getEBikeServiceMongoCredentials() {
        return new MongoCredentials(
                System.getenv("EBIKE_SERVICE_MONGO_DB_HOST"),
                System.getenv("EBIKE_SERVICE_MONGO_DB_PORT"),
                System.getenv("EBIKE_SERVICE_MONGO_DB_NAME"),
                System.getenv("EBIKE_SERVICE_MONGO_DB_USER"),
                System.getenv("EBIKE_SERVICE_MONGO_DB_PASSWORD")
        );
    }
}
