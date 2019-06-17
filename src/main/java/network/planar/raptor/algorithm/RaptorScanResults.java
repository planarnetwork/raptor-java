package network.planar.raptor.algorithm;

import network.planar.raptor.journey.Connection;

import java.util.Map;

public class RaptorScanResults {
    public final Map<String, Map<Integer, Connection>> kConnections;
    public final Map<String, Integer> bestArrivals;

    public RaptorScanResults(Map<String, Map<Integer, Connection>> kConnections, Map<String, Integer> bestArrivals) {
        this.kConnections = kConnections;
        this.bestArrivals = bestArrivals;
    }
}
