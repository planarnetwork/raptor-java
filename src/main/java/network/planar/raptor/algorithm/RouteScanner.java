package network.planar.raptor.algorithm;

import network.planar.raptor.gtfs.Calendar;
import network.planar.raptor.gtfs.Trip;

import java.util.List;
import java.util.Map;

public class RouteScanner {

    private final Map<String, List<Trip>> tripsByRoute;
    private final Map<String, Calendar> calendars;
    private final Map<String, Integer> routeScanPosition;

    public RouteScanner(
        Map<String, List<Trip>> tripsByRoute,
        Map<String, Calendar> calendars,
        Map<String, Integer> routeScanPosition
    ) {
        this.tripsByRoute = tripsByRoute;
        this.calendars = calendars;
        this.routeScanPosition = routeScanPosition;
    }

    public Trip getTrip(String routeId, int date, int dow, int stopIndex, int time) {
        if (!this.routeScanPosition.containsKey(routeId)) {
            this.routeScanPosition.put(routeId, tripsByRoute.get(routeId).size() - 1);
        }

        Trip lastFound = null;

        // iterate backwards through the trips on the route, starting where we last found a trip
        for (int i = routeScanPosition.get(routeId); i >= 0; i--) {
            Trip trip = tripsByRoute.get(routeId).get(i);

            // if the trip is unreachable, exit the loop
            if (trip.stopTimes.get(stopIndex).departureTime < time) {
                break;
            }
            // if it is reachable and the service is running that day, update the last valid trip found
            else if (serviceIsRunning(trip.serviceId, date, dow)) {
                lastFound = trip;
            }

            // if we found a trip, update the last found index, if we still haven't found a trip we can also update the
            // last found index as any subsequent scans will be for an earlier time. We can't update the index every time
            // as there may be some services that are reachable but not running before the last found service and searching
            // must continue from the last reachable point.
            if (lastFound == null || lastFound == trip) {
                routeScanPosition.put(routeId, i);
            }
        }

        return lastFound;
    }

    private boolean serviceIsRunning(String serviceId, int date, int dow) {
        Calendar calendar = calendars.get(serviceId);

        return calendar.dates.getOrDefault(date, false) || (!calendar.dates.containsKey(date) &&
            calendar.startDate <= date &&
            calendar.endDate >= date &&
            calendar.days.get(dow)
        );
    }
}
