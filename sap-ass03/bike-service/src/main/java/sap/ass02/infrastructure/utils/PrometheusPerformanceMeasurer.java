package sap.ass02.infrastructure.utils;

import io.prometheus.client.Histogram;

/**
 * A performance measurer that uses Prometheus to measure the latency of requests.
 */
public final class PrometheusPerformanceMeasurer implements PerformanceMeasurer<Histogram.Timer> {
    /**
     * The histogram to measure the latency of requests
     */
    private static final Histogram REQUESTS_LATENCY_SECONDS = Histogram.build()
            .name("requests_latency_seconds")
            .help("Request latency in seconds.")
            .register();
    
    /**
     * Start a timer to measure the latency of a request
     *
     * @return the timer
     */
    @Override
    public Histogram.Timer startTimer() {
        return REQUESTS_LATENCY_SECONDS.startTimer();
    }
    
    /**
     * Observe the latency of a request
     *
     * @param timer the timer
     */
    @Override
    public void stopTimer(Histogram.Timer timer) {
        timer.observeDuration();
    }
}
