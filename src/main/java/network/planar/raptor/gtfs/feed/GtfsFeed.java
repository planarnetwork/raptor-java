package network.planar.raptor.gtfs.feed;

import network.planar.raptor.gtfs.Calendar;
import network.planar.raptor.gtfs.Trip;
import network.planar.raptor.journey.Transfer;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GtfsFeed {
    public final List<Trip> trips;
    public final Map<String, Calendar> services;
    public final Map<String, List<Transfer>> transfers;
    public final Map<String, Integer> interchange;
    public final Map<String, Set<Integer>> stopDepartureTimes;

    public GtfsFeed(
        List<Trip> trips,
        Map<String, Calendar> services,
        Map<String, List<Transfer>> transfers,
        Map<String, Integer> interchange,
        Map<String, Set<Integer>> stopDepartureTimes
    ) {
        this.trips = trips;
        this.services = services;
        this.transfers = transfers;
        this.interchange = interchange;
        this.stopDepartureTimes = stopDepartureTimes;
    }
}
