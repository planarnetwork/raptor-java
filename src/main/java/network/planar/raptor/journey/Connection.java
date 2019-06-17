package network.planar.raptor.journey;

import network.planar.raptor.gtfs.Trip;

public class Connection {
    public final Trip trip;
    public final int startIndex;
    public final int endIndex;
    public final Transfer transfer;

    public Connection(Trip trip, int startIndex, int endIndex) {
        this.trip = trip;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.transfer = null;
    }

    public Connection(Transfer transfer) {
        this.transfer = transfer;
        this.trip = null;
        this.startIndex = -1;
        this.endIndex = -1;
    }
}
