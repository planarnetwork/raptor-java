package network.planar.raptor.journey;

import network.planar.raptor.gtfs.StopTime;
import network.planar.raptor.gtfs.Trip;

import java.util.List;

public class Leg {

    public final String origin;
    public final String destination;
    public final Trip trip;
    public final List<StopTime> stopTimes;
    public final int duration;

    public Leg(String origin, String destination, int duration) {
        this.origin = origin;
        this.destination = destination;
        this.duration = duration;
        this.trip = null;
        this.stopTimes = null;
    }

    public Leg(String origin, String destination, Trip trip, List<StopTime> stopTimes) {
        this.origin = origin;
        this.destination = destination;
        this.trip = trip;
        this.stopTimes = stopTimes;
        this.duration = -1;
    }

    public boolean isTransfer() {
        return this.trip == null;
    }
}
