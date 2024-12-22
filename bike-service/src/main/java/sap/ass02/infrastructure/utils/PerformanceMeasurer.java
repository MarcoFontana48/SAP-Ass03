package sap.ass02.infrastructure.utils;

public interface PerformanceMeasurer<T> {
     T startTimer();
     
     void stopTimer(T timer);
}
