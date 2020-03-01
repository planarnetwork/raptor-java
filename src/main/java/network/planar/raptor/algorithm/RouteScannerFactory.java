package network.planar.raptor.algorithm;

import network.planar.raptor.gtfs.Calendar;
import network.planar.raptor.gtfs.Trip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteScannerFactory {
    private final Map<String, List<Trip>> tripsByRoute;
    private final Map<String, Calendar> calendars;

    public RouteScannerFactory(Map<String, List<Trip>> tripsByRoute, Map<String, Calendar> calendars) {
        this.tripsByRoute = tripsByRoute;
        this.calendars = calendars;
    }

    public RouteScanner create(int date, int dow) {
        return new RouteScanner(tripsByRoute, calendars, new HashMap<>(1000), date, dow);
    }
}
