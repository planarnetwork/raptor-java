package network.planar.raptor.algorithm;

import network.planar.raptor.journey.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanResultsFactory {
    private final List<String> stops;

    public ScanResultsFactory(List<String> stops) {
        this.stops = stops;
    }

    public ScanResults create(Map<String, Integer> origins) {
        Map<String, Integer> bestArrivals = new HashMap<>(3500);
        Map<String, Map<Integer, Connection>> kConnections = new HashMap<>(3500);
        List<Map<String, Integer>> kArrivals = new ArrayList<>(16);
        kArrivals.add(new HashMap<>(3500));

        for (String stop : stops) {
            bestArrivals.put(stop, origins.getOrDefault(stop, Integer.MAX_VALUE));
            kArrivals.get(0).put(stop, origins.getOrDefault(stop, Integer.MAX_VALUE));
            kConnections.put(stop, new HashMap<>(3500));
        }

        return new ScanResults(kConnections, kArrivals, bestArrivals);
    }
}
