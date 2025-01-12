package sap.ass02.infrastructure.utils;

/**
 * Interface for measuring performance.
 * @param <T> the type of the timer
 */
public interface PerformanceMeasurer<T> {
     /**
      * Starts a timer.
      * @return the timer
      */
     T startTimer();
     
     /**
      * Stops a timer.
      * @param timer the timer
      */
     void stopTimer(T timer);
}
