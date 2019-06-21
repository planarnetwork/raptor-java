package network.planar.raptor.query;

import network.planar.raptor.algorithm.RaptorAlgorithm;
import network.planar.raptor.algorithm.RaptorScanResults;
import network.planar.raptor.journey.Journey;
import network.planar.raptor.results.ResultsFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static network.planar.raptor.gtfs.DateUtil.toGtfsDate;

public class DepartAfterQuery {

    private final RaptorAlgorithm raptor;
    private final ResultsFactory resultsFactory;

    public DepartAfterQuery(RaptorAlgorithm raptor, ResultsFactory resultsFactory) {
        this.raptor = raptor;
        this.resultsFactory = resultsFactory;
    }

    public List<Journey> plan(String origin, String destination, LocalDate date, int time) {
        Map<String, Integer> originTimes = new HashMap<>();
        originTimes.put(origin, time);

        int dateInt = toGtfsDate(date);
        int dayOfWeek = date.getDayOfWeek().getValue();
        RaptorScanResults results = raptor.scan(originTimes, dateInt, dayOfWeek);

        return this.resultsFactory.getResults(results.kConnections, destination);
    }
}
