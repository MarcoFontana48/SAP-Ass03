package sap.ass02.infrastructure.utils;

import io.prometheus.client.Counter;

public final class PrometheusRequestsCounter implements RequestsCounter {
    private static final Counter REQUESTS_METHOD_STATUS_NUM_COUNTER = Counter.build()
            .name("bike_service_requests_method_status_total")
            .help("Total requests to bike service divided by method type and status.")
            .labelNames("method","status")  // method has values POST, PUT, GET, etc.. status is 200, 500, etc.. that are assigned in the handlers
            .register();
    
    /**
     * Increase the total number of POST requests received by the service
     *
     * @param statusCode the status code of the request
     */
    @Override
    public void increasePostRequestsCounter(int statusCode) {
        REQUESTS_METHOD_STATUS_NUM_COUNTER.labels("POST", String.valueOf(statusCode)).inc();
    }
    
    /**
     * Increase the total number of GET requests received by the service
     *
     * @param statusCode the status code of the request
     */
    @Override
    public void increaseGetRequestsCounter(int statusCode) {
        REQUESTS_METHOD_STATUS_NUM_COUNTER.labels("GET", String.valueOf(statusCode)).inc();
    }
    
    /**
     * Increase the total number of PUT requests received by the service
     *
     * @param statusCode the status code of the request
     */
    @Override
    public void increasePutRequestsCounter(int statusCode) {
        REQUESTS_METHOD_STATUS_NUM_COUNTER.labels("PUT", String.valueOf(statusCode)).inc();
    }
}
