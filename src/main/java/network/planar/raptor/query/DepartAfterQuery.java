package network.planar.raptor.query;

import network.planar.raptor.algorithm.RaptorAlgorithm;
import network.planar.raptor.algorithm.ScanResults;
import network.planar.raptor.journey.Journey;
import network.planar.raptor.results.JourneyFilter;
import network.planar.raptor.results.ResultsFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static network.planar.raptor.gtfs.DateUtil.toGtfsDate;

public class DepartAfterQuery {

    private final RaptorAlgorithm raptor;
    private final ResultsFactory resultsFactory;
    private final JourneyFilter journeyFilter;

    public DepartAfterQuery(RaptorAlgorithm raptor, ResultsFactory resultsFactory, JourneyFilter journeyFilter) {
        this.raptor = raptor;
        this.resultsFactory = resultsFactory;
        this.journeyFilter = journeyFilter;
    }

    public List<Journey> plan(List<String> origins, List<String> destinations, LocalDate date, int time) {
        int dateInt = toGtfsDate(date);
        int dayOfWeek = date.getDayOfWeek().getValue();
        Map<String, Integer> originTimes = new HashMap<>(origins.size());

        for (String origin : origins) {
            originTimes.put(origin, time);
        }

        ScanResults.FinalizedResults results = raptor.scan(originTimes, dateInt, dayOfWeek);

        List<Journey> journeys = new ArrayList<>(100);

        for (String destination : destinations) {
            journeys.addAll(resultsFactory.getResults(results.kConnections, destination));
        }

        return journeyFilter.apply(journeys);
    }
}
