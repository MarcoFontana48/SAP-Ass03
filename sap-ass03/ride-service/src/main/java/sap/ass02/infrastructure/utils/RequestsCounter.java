package sap.ass02.infrastructure.utils;

public interface RequestsCounter {
    /**
     * Increase the total number of POST requests received by the service
     * @param statusCode the status code of the request
     */
    void increasePostRequestsCounter(int statusCode);
    
    /**
     * Increase the total number of GET requests received by the service
     * @param statusCode the status code of the request
     */
    void increaseGetRequestsCounter(int statusCode);
    
    /**
     * Increase the total number of PUT requests received by the service
     * @param statusCode the status code of the request
     */
    void increasePutRequestsCounter(int statusCode);
}
