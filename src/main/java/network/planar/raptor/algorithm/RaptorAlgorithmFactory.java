package network.planar.raptor.algorithm;

import network.planar.raptor.gtfs.Trip;
import network.planar.raptor.gtfs.feed.GtfsFeed;
import network.planar.raptor.journey.Transfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;


public class RaptorAlgorithmFactory {

    private static final String OVERTAKING_ROUTE_SUFFIX = "overtaken";
    private static final Integer DEFAULT_INTERCHANGE_TIME = 0;

    public RaptorAlgorithm create(GtfsFeed feed) {
        Map<String, Map<String, Integer>> routeStopIndex = new HashMap<>();
        Map<String, List<String>> routePath = new HashMap<>();
        Map<String, List<String>> routesAtStop = new HashMap<>();
        Map<String, List<Trip>> tripsByRoute = new HashMap<>();

        feed.trips.sort(comparingInt(t -> t.stopTimes.get(0).departureTime));

        for (Trip trip : feed.trips) {
            String routeId = getRouteId(trip, tripsByRoute);

            if (!routeStopIndex.containsKey(routeId)) {
                List<String> path = trip.stopTimes.stream().map(st -> st.stop).collect(Collectors.toList());
                routeStopIndex.put(routeId, new HashMap<>());
                tripsByRoute.put(routeId, new ArrayList<>());
                routePath.put(routeId, path);

                for (int i = 0; i < path.size(); i++) {
                    routeStopIndex.get(routeId).put(path.get(i), i);
                    feed.transfers.putIfAbsent(path.get(i), new ArrayList<>());
                    feed.interchange.putIfAbsent(path.get(i), DEFAULT_INTERCHANGE_TIME);

                    if (trip.stopTimes.get(i).pickUp) {
                        routesAtStop.computeIfAbsent(path.get(i), k -> new ArrayList<>()).add(routeId);
                    }
                }
            }

            tripsByRoute.get(routeId).add(trip);
        }

        return new RaptorAlgorithm(
            routeStopIndex,
            routePath,
            feed.transfers,
            feed.interchange,
            new ArrayList<>(feed.interchange.keySet()),
            new QueueFactory(routesAtStop, routeStopIndex),
            new RouteScannerFactory(tripsByRoute, feed.services)
        );
    }

    private String getRouteId(Trip trip, Map<String, List<Trip>> tripsByRoute) {
        String routeId = trip.stopTimes
            .stream()
            .map(s -> s.stop + (s.pickUp ? 1 : 0) + (s.dropOff ? 1 : 0))
            .reduce("", (id, item) -> id + item);

        int arrivalTimeA = trip.stopTimes.get(trip.stopTimes.size() - 1).arrivalTime;
        boolean overtaken = tripsByRoute
            .getOrDefault(routeId, new ArrayList<>())
            .stream()
            .anyMatch(t -> arrivalTimeA < t.stopTimes.get(t.stopTimes.size() - 1).arrivalTime);

        return overtaken ? routeId + OVERTAKING_ROUTE_SUFFIX : routeId;
    }

}
