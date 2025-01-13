package sap.ass02.domain;

import java.util.List;

/**
 * Environment class.
 */
public final class Environment {
    /**
     * Contains a list of stations.
     */
    private static final Iterable<Station> STATIONS = List.of(
            new Station(new P2d(-100, 100)),
            new Station(new P2d(-100, -100)),
            new Station(new P2d(100, 100)),
            new Station(new P2d(100, -100)));
    
    /**
     * Get the stations.
     * @return stations
     */
    public static Iterable<Station> getStations() {
        return STATIONS;
    }
}
