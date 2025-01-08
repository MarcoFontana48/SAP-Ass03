package sap.ass02.domain;

public record Station(P2d location) implements Place {
    @Override
    public P2d location() {
        return this.location;
    }
}
