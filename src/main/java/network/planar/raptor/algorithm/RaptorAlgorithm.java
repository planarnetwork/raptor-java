package network.planar.raptor.algorithm;

import network.planar.raptor.gtfs.StopTime;
import network.planar.raptor.gtfs.Trip;
import network.planar.raptor.journey.Connection;
import network.planar.raptor.journey.Transfer;

import java.util.*;

import static java.util.Arrays.asList;

public class RaptorAlgorithm {
    private final Map<String, Map<String, Integer>> routeStopIndex;
    private final Map<String, List<String>> routePath;
    private final Map<String, List<Transfer>> transfers;
    private final Map<String, Integer> interchange;
    private final List<String> stops;
    private final QueueFactory queueFactory;
    private final RouteScannerFactory routeScannerFactory;

    public RaptorAlgorithm(
        Map<String, Map<String, Integer>> routeStopIndex,
        Map<String, List<String>> routePath,
        Map<String, List<Transfer>> transfers,
        Map<String, Integer> interchange,
        List<String> stops, QueueFactory queueFactory,
        RouteScannerFactory routeScannerFactory
    ) {
        this.routeStopIndex = routeStopIndex;
        this.routePath = routePath;
        this.transfers = transfers;
        this.interchange = interchange;
        this.stops = stops;
        this.queueFactory = queueFactory;
        this.routeScannerFactory = routeScannerFactory;
    }

    public RaptorScanResults scan(Map<String, Integer> origins, int date, int dow) {
        RouteScanner routeScanner = this.routeScannerFactory.create();
        Map<String, Integer> bestArrivals = new HashMap<>(3500);
        Map<String, Map<Integer, Connection>> kConnections = new HashMap<>(3500);
        List<Map<String, Integer>> kArrivals = new ArrayList<>(16);
        kArrivals.add(new HashMap<>(3500));

        for (String stop : stops) {
            bestArrivals.put(stop, origins.getOrDefault(stop, Integer.MAX_VALUE));
            kArrivals.get(0).put(stop, origins.getOrDefault(stop, Integer.MAX_VALUE));
            kConnections.put(stop, new HashMap<>(3500));
        }

        int k = 1;

        for (Set<String> markedStops = origins.keySet(); !markedStops.isEmpty(); k++) {
            Map<String, String> queue = this.queueFactory.getQueue(markedStops);
            kArrivals.add(new HashMap<>(3500));

            // examine routes
            for (String routeId : queue.keySet()) {
                String stopP = queue.get(routeId);
                int boardingPoint = -1;
                List<StopTime> stops = null;
                Trip trip = null;

                for (int pi = routeStopIndex.get(routeId).get(stopP); pi < routePath.get(routeId).size(); pi++) {
                    String stopPi = routePath.get(routeId).get(pi);
                    int changeTime = interchange.get(stopPi);
                    Integer previousPiArrival = kArrivals.get(k - 1).get(stopPi);

                    if (stops != null && stops.get(pi).dropOff && stops.get(pi).arrivalTime + changeTime < bestArrivals.get(stopPi)) {
                        kArrivals.get(k).put(stopPi, stops.get(pi).arrivalTime + changeTime);
                        bestArrivals.put(stopPi, stops.get(pi).arrivalTime + changeTime);
                        kConnections.get(stopPi).put(k, new Connection(trip, boardingPoint, pi));
                    }
                    else if (previousPiArrival != null && (stops == null || previousPiArrival < stops.get(pi).arrivalTime + changeTime)) {
                        trip = routeScanner.getTrip(routeId, date, dow, pi, previousPiArrival);
                        stops = trip != null ? trip.stopTimes : null;
                        boardingPoint = pi;
                    }
                }
            }

            // examine transfers
            for (String stopP : markedStops) {
                for (Transfer transfer : transfers.get(stopP)) {
                    String stopPi = transfer.destination;
                    int arrival = kArrivals.get(k - 1).get(stopP) + transfer.duration + interchange.get(stopPi);

                    if (arrival < bestArrivals.get(stopPi)) {
                        kArrivals.get(k).put(stopPi, arrival);
                        bestArrivals.put(stopPi, arrival);
                        kConnections.get(stopPi).put(k, new Connection(transfer));
                    }
                }
            }

            markedStops = kArrivals.get(k).keySet();
        }

        return new RaptorScanResults(kConnections, bestArrivals);
    }

}
