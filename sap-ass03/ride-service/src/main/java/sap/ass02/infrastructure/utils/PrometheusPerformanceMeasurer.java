package sap.ass02.infrastructure.utils;

import io.prometheus.client.Histogram;

public class PrometheusPerformanceMeasurer implements PerformanceMeasurer<Histogram.Timer> {
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
