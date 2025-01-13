package sap.ass02.infrastructure.utils;

/**
 * Interface for performance measurers.
 *
 * @param <T> the type of the timer
 */
public interface PerformanceMeasurer<T> {
     /**
      * Starts a timer.
      *
      * @return the timer
      */
     T startTimer();
     
        /**
        * Stops a timer.
        *
        * @param timer the timer to stop
        */
     void stopTimer(T timer);
}
