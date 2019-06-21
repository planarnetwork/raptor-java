package network.planar.raptor.gtfs.feed;

import com.conveyal.gtfs.GTFSFeed;
import com.conveyal.gtfs.model.CalendarDate;
import network.planar.raptor.gtfs.Calendar;
import network.planar.raptor.gtfs.StopTime;
import network.planar.raptor.gtfs.Trip;
import network.planar.raptor.journey.Transfer;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.primitives.Booleans.asList;
import static network.planar.raptor.gtfs.DateUtil.toGtfsDate;
import static org.mapdb.Fun.Tuple2;

public class GtfsFeedAdapter {

    private DateTimeFormatter gtfsFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

    public GtfsFeed convert(GTFSFeed feed) {
        List<Trip> trips = getTrips(feed);
        Map<String, Calendar> services = getServices(feed);
        ImmutablePair<Map<String, List<Transfer>>, Map<String, Integer>> transfers = getTransfers(feed);

        return new GtfsFeed(trips, services, transfers.left, transfers.right);
    }

    private List<Trip> getTrips(GTFSFeed feed) {
        HashMap<String, Trip> trips = new HashMap<>();

        for (Tuple2<String, Integer> key : feed.stop_times.keySet()) {
            StopTime stopTime = getStopTime(feed.stop_times.get(key));
            String serviceId = feed.trips.get(key.a).service_id;
            Trip trip = trips.computeIfAbsent(key.a, k -> new Trip(key.a, serviceId, new ArrayList<>()));

            trip.stopTimes.add(stopTime);
        }

        return new ArrayList<>(trips.values());
    }

    private StopTime getStopTime(com.conveyal.gtfs.model.StopTime stopTime) {
        return new StopTime(
            stopTime.stop_id,
            stopTime.arrival_time,
            stopTime.departure_time,
            stopTime.pickup_type == 0,
            stopTime.drop_off_type == 0
        );
    }

    private Map<String, Calendar> getServices(GTFSFeed feed) {
        Map<String, Calendar> services = new HashMap<>();

        for (com.conveyal.gtfs.model.Service service : feed.services.values()) {
            Map<Integer, Boolean> dates = new HashMap<>();

            for (CalendarDate date : service.calendar_dates.values()) {
                dates.put(toGtfsDate(date.date), date.exception_type == 1);
            }

            Calendar calendar = new Calendar(
                toGtfsDate(service.calendar.start_date),
                toGtfsDate(service.calendar.end_date),
                asList(
                    service.calendar.sunday == 1,
                    service.calendar.monday == 1,
                    service.calendar.tuesday == 1,
                    service.calendar.wednesday == 1,
                    service.calendar.thursday == 1,
                    service.calendar.friday == 1,
                    service.calendar.saturday == 1
                ),
                dates
            );

            services.put(service.service_id, calendar);
        }

        return services;
    }

    private ImmutablePair<Map<String, List<Transfer>>, Map<String, Integer>> getTransfers(GTFSFeed feed) {
        Map<String, List<Transfer>> transfers = new HashMap<>();
        Map<String, Integer> interchange = new HashMap<>();

        for (com.conveyal.gtfs.model.Transfer transfer : feed.transfers.values()) {
            if (!transfer.from_stop_id.equals(transfer.to_stop_id)) {
                transfers
                    .computeIfAbsent(transfer.from_stop_id, k -> new ArrayList<>())
                    .add(new Transfer(transfer.from_stop_id, transfer.to_route_id, transfer.min_transfer_time));
            }
            else {
                interchange.put(transfer.from_stop_id, transfer.min_transfer_time);
            }
        }

        return new ImmutablePair<>(transfers, interchange);
    }

}
