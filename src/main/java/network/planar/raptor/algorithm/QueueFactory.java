package network.planar.raptor.algorithm;

import java.util.*;

public class QueueFactory {
    private final Map<String, List<String>> routesAtStop;
    private final Map<String, Map<String, Integer>> routeStopIndex;

    public QueueFactory(Map<String, List<String>> routesAtStop, Map<String, Map<String, Integer>> routeStopIndex) {
        this.routesAtStop = routesAtStop;
        this.routeStopIndex = routeStopIndex;
    }

    public Map<String, String> getQueue(Set<String> markedStops) {
        Map<String, String> queue = new HashMap<>();

        for (String stop : markedStops) {
            for (String routeId : routesAtStop.getOrDefault(stop, new ArrayList<>())) {
                if (queue.containsKey(routeId)) {
                    int newStopIndex = routeStopIndex.get(routeId).get(stop);
                    int currentStopIndex = routeStopIndex.get(routeId).get(queue.get(routeId));
                    String newStop = newStopIndex < currentStopIndex ? stop : queue.get(routeId);

                    queue.put(routeId, newStop);
                }
                else {
                    queue.put(routeId, stop);
                }
            }
        }

        return queue;
    }
}
