package sap.ass02.infrastructure.utils;

/**
 * Interface for objects that can measure performance.
 */
public interface PerformanceMeasurer<T> {
     T startTimer();
     
     void stopTimer(T timer);
}
