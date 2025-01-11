package sap.ass02.domain;

import java.util.List;

public final class Environment {
    private static final Iterable<Station> STATIONS = List.of(
            new Station(new P2d(-100, 100)),
            new Station(new P2d(-100, -100)),
            new Station(new P2d(100, 100)),
            new Station(new P2d(100, -100)));
    
    public static Iterable<Station> getStations() {
        return STATIONS;
    }
}
