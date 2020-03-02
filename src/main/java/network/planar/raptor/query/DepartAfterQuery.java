package network.planar.raptor.query;

import network.planar.raptor.algorithm.RaptorAlgorithm;
import network.planar.raptor.algorithm.ScanResults;
import network.planar.raptor.journey.Journey;
import network.planar.raptor.results.JourneyFilter;
import network.planar.raptor.results.ResultsFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static network.planar.raptor.gtfs.DateUtil.toGtfsDate;

public class DepartAfterQuery {

    private final RaptorAlgorithm raptor;
    private final ResultsFactory resultsFactory;
    private final JourneyFilter journeyFilter;
    private final Map<String, Set<Integer>> stopDepartureTimes;

    public DepartAfterQuery(
        RaptorAlgorithm raptor,
        ResultsFactory resultsFactory,
        JourneyFilter journeyFilter,
        Map<String, Set<Integer>> stopDepartureTimes
    ) {
        this.raptor = raptor;
        this.resultsFactory = resultsFactory;
        this.journeyFilter = journeyFilter;
        this.stopDepartureTimes = stopDepartureTimes;
    }

    public List<Journey> plan(List<String> origins, List<String> destinations, LocalDate date, int time, int limit) {
        Set<Integer> departureTimes = origins
            .stream()
            .flatMap(o -> stopDepartureTimes.get(o).stream())
            .sorted()
            .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Journey> results = new ArrayList<>(limit);

        while (results.size() < limit) {
            final int targetTime = time;
            List<Integer> nextTenDepartureTimes = departureTimes
                .stream()
                .filter(t -> t >= targetTime)
                .limit(Runtime.getRuntime().availableProcessors())
                .collect(Collectors.toList());

            results.addAll(planAtTimes(origins, destinations, date, nextTenDepartureTimes));

            if (results.isEmpty()) {
                return results;
            }

            time = results.get(results.size() - 1).departureTime + 1;
        }

        return results;
    }

    private List<Journey> planAtTimes(List<String> origins, List<String> destinations, LocalDate date, List<Integer> departureTimes) {
        List<Journey> journeys = departureTimes
            .parallelStream()
            .flatMap(t -> planAtTime(origins, destinations, date, t).stream())
            .collect(Collectors.toList());

        return journeyFilter.apply(journeys);
    }

    private List<Journey> planAtTime(List<String> origins, List<String> destinations, LocalDate date, int time) {
        int dateInt = toGtfsDate(date);
        int dayOfWeek = date.getDayOfWeek().getValue();
        Map<String, Integer> originTimes = new HashMap<>(origins.size());

        for (String origin : origins) {
            originTimes.put(origin, time);
        }

        ScanResults.FinalizedResults results = raptor.scan(originTimes, dateInt, dayOfWeek);

        List<Journey> journeys = new ArrayList<>();

        for (String destination : destinations) {
            journeys.addAll(resultsFactory.getResults(results.kConnections, destination));
        }

        return journeys;
    }
}
