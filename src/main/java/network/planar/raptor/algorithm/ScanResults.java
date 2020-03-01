package network.planar.raptor.algorithm;

import network.planar.raptor.gtfs.StopTime;
import network.planar.raptor.gtfs.Trip;
import network.planar.raptor.journey.Connection;
import network.planar.raptor.journey.Transfer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScanResults {
    private final Map<String, Map<Integer, Connection>> kConnections;
    private final List<Map<String, Integer>> kArrivals;
    private final Map<String, Integer> bestArrivals;
    private int k = 0;

    public ScanResults(
        Map<String, Map<Integer, Connection>> kConnections,
        List<Map<String, Integer>> kArrivals,
        Map<String, Integer> bestArrivals
    ) {
        this.kConnections = kConnections;
        this.kArrivals = kArrivals;
        this.bestArrivals = bestArrivals;
    }

    public void addRound() {
        kArrivals.add(++k, new HashMap<>(3500));
    }

    public Integer previousArrival(String stopPi) {
        return kArrivals.get(k - 1).get(stopPi);
    }

    public void setTrip(Trip trip, int startIndex, int endIndex, int interchange) {
        final StopTime lastStop = trip.stopTimes.get(endIndex);
        final int time = lastStop.arrivalTime + interchange;
        final String stopPi = lastStop.stop;

        kArrivals.get(k).put(stopPi, time);
        bestArrivals.put(stopPi, time);
        kConnections.get(stopPi).put(k, new Connection(trip, startIndex, endIndex));
    }

    public void setTransfer(Transfer transfer, int time) {
        final String stopPi = transfer.destination;

        kArrivals.get(k).put(stopPi, time);
        bestArrivals.put(stopPi, time);
        kConnections.get(stopPi).put(k, new Connection(transfer));
    }

    public Integer bestArrival(String stop) {
        return bestArrivals.get(stop);
    }

    public Set<String> getMarkedStops() {
        return kArrivals.get(k).keySet();
    }

    public FinalizedResults finish() {
        return new FinalizedResults(kConnections, bestArrivals);
    }

    public class FinalizedResults {
        public final Map<String, Map<Integer, Connection>> kConnections;
        public final Map<String, Integer> bestArrivals;

        FinalizedResults(Map<String, Map<Integer, Connection>> kConnections, Map<String, Integer> bestArrivals) {
            this.kConnections = kConnections;
            this.bestArrivals = bestArrivals;
        }
    }
}
