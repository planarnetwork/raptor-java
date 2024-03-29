package network.planar.raptor;

import network.planar.raptor.algorithm.RaptorAlgorithm;
import network.planar.raptor.algorithm.RaptorAlgorithmFactory;
import network.planar.raptor.gtfs.feed.GtfsFeed;
import network.planar.raptor.gtfs.feed.GtfsFeedAdapter;
import network.planar.raptor.gtfs.feed.GtfsFeedFactory;
import network.planar.raptor.journey.Journey;
import network.planar.raptor.query.DepartAfterQuery;
import network.planar.raptor.results.JourneyFactory;
import network.planar.raptor.results.JourneyFilter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;


public class Main {

    public static void main(String[] argv) {
        GtfsFeedFactory factory = new GtfsFeedFactory(new GtfsFeedAdapter());
        GtfsFeed feed = factory.create("/home/linus/Downloads/gb-rail-latest.zip");
        System.out.println("Trips: " + feed.trips.size());

        RaptorAlgorithmFactory raptorFactory = new RaptorAlgorithmFactory();
        RaptorAlgorithm raptor = raptorFactory.create(feed);
        DepartAfterQuery query = new DepartAfterQuery(raptor, new JourneyFactory(), new JourneyFilter(), feed.stopDepartureTimes);
        List<String> origins = asList("BHM", "BSW", "BMO");
        List<String> destinations = asList("LBG", "WAE", "CST", "LST", "CHX", "EUS", "VIC", "MYB");

        for (int i = 0; i < 15; i++) {
            Long start = System.currentTimeMillis();
            query.plan(origins, destinations, LocalDate.now(), 3600 * 10, 5);
            Long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start));
        }

        List<Journey> journeys = query.plan(origins, destinations, LocalDate.now(), 3600 * 10, 5);

        for (Journey journey : journeys) {
            String departure = toTime(journey.departureTime);
            String arrival = toTime(journey.arrivalTime);
            String legs = journey.legs
                .stream()
                .map(l -> l.isTransfer()
                    ? "  " + l.origin + " -> " + l.destination + "(" + l.duration + ")"
                    : "  " + l.origin + "(" + toTime(l.stopTimes.get(0).departureTime) + ") -> " + l.destination + "(" + toTime(l.stopTimes.get(l.stopTimes.size() - 1).arrivalTime) + ") " + l.trip.tripId
                )
                .reduce("", (result, leg) -> result + "\n" + leg);

            System.out.println(departure + ", " + arrival + ", " + legs);
        }
    }

    private static String toTime(int time) {
        int hours = (int) Math.floor(time / 3600);
        int minutes = (int) Math.floor((time - (hours * 3600)) / 60);
        int seconds = time - (hours * 3600) - (minutes * 60);

        String hourPad = (hours < 10) ? "0" : "";
        String minutesPad = (minutes < 10) ? "0" : "";
        String secondsPad = (seconds < 10) ? "0" : "";

        return hourPad + hours + ":" + minutesPad + minutes + ":" + secondsPad + seconds;
    }
}
