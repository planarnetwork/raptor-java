package network.planar.raptor.algorithm;

import network.planar.raptor.gtfs.StopTime;
import network.planar.raptor.gtfs.Trip;
import network.planar.raptor.journey.Connection;
import network.planar.raptor.journey.Transfer;

import java.util.*;

public class RaptorAlgorithm {
    private final Map<String, Map<String, Integer>> routeStopIndex;
    private final Map<String, List<String>> routePath;
    private final Map<String, List<Transfer>> transfers;
    private final Map<String, Integer> interchange;
    private final ScanResultsFactory scanResultsFactory;
    private final QueueFactory queueFactory;
    private final RouteScannerFactory routeScannerFactory;

    public RaptorAlgorithm(
        Map<String, Map<String, Integer>> routeStopIndex,
        Map<String, List<String>> routePath,
        Map<String, List<Transfer>> transfers,
        Map<String, Integer> interchange,
        ScanResultsFactory scanResultsFactory,
        QueueFactory queueFactory,
        RouteScannerFactory routeScannerFactory
    ) {
        this.routeStopIndex = routeStopIndex;
        this.routePath = routePath;
        this.transfers = transfers;
        this.interchange = interchange;
        this.scanResultsFactory = scanResultsFactory;
        this.queueFactory = queueFactory;
        this.routeScannerFactory = routeScannerFactory;
    }

    public ScanResults.FinalizedResults scan(Map<String, Integer> origins, int date, int dow) {
        RouteScanner routeScanner = this.routeScannerFactory.create(date, dow);
        ScanResults results = this.scanResultsFactory.create(origins);
        Set<String> markedStops = origins.keySet();

        while (!markedStops.isEmpty()) {
            results.addRound();

            this.scanRoutes(results, routeScanner, markedStops);
            this.scanTransfers(results, markedStops);
            markedStops = results.getMarkedStops();
        }

        return results.finish();
    }

    private void scanRoutes(ScanResults results, RouteScanner routeScanner, Set<String> markedStops) {
        Map<String, String> queue = this.queueFactory.getQueue(markedStops);

        for (String routeId : queue.keySet()) {
            String stopP = queue.get(routeId);
            int boardingPoint = -1;
            List<StopTime> stops = null;
            Trip trip = null;

            for (int pi = routeStopIndex.get(routeId).get(stopP); pi < routePath.get(routeId).size(); pi++) {
                String stopPi = routePath.get(routeId).get(pi);
                int changeTime = interchange.get(stopPi);
                Integer previousPiArrival = results.previousArrival(stopPi);

                if (stops != null && stops.get(pi).dropOff && stops.get(pi).arrivalTime + changeTime < results.bestArrival(stopPi)) {
                    results.setTrip(trip, boardingPoint, pi, changeTime);
                }
                else if (previousPiArrival != null && (stops == null || previousPiArrival < stops.get(pi).arrivalTime + changeTime)) {
                    Trip newTrip = routeScanner.getTrip(routeId, pi, previousPiArrival);

                    if (newTrip != null) {
                        trip = newTrip;
                        stops = newTrip.stopTimes;
                        boardingPoint = pi;
                    }
                }
            }
        }
    }

    private void scanTransfers(ScanResults results, Set<String> markedStops) {
        for (String stopP : markedStops) {
            for (Transfer transfer : transfers.get(stopP)) {
                String stopPi = transfer.destination;
                int arrival = results.previousArrival(stopP) + transfer.duration + interchange.get(stopPi);

                if (transfer.startTime <= arrival && transfer.endTime >= arrival && arrival < results.bestArrival(stopPi)) {
                    results.setTransfer(transfer, arrival);
                }
            }
        }
    }

}
