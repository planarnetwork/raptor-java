package network.planar.raptor.gtfs;

import java.util.List;

public class Trip {
    public final String tripId;
    public final String serviceId;
    public final List<StopTime> stopTimes;

    public Trip(String tripId, String serviceId, List<StopTime> stopTimes) {
        this.tripId = tripId;
        this.serviceId = serviceId;
        this.stopTimes = stopTimes;
    }
}
