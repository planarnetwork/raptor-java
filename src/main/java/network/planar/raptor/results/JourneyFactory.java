package network.planar.raptor.results;

import network.planar.raptor.journey.Connection;
import network.planar.raptor.journey.Journey;
import network.planar.raptor.journey.Leg;

import java.util.*;

public class JourneyFactory implements ResultsFactory {

    @Override
    public List<Journey> getResults(Map<String, Map<Integer, Connection>> kConnections, String destination) {
        List<Journey> results = new ArrayList<>();

        for (int k : kConnections.getOrDefault(destination, new HashMap<>()).keySet()) {
            List<Leg> legs = getJourneyLegs(kConnections, k, destination);
            int departureTime = getDepartureTime(legs);
            int arrivalTime = getArrivalTime(legs);

            results.add(new Journey(legs, departureTime, arrivalTime));
        }

        return results;
    }

    private List<Leg> getJourneyLegs(Map<String, Map<Integer, Connection>> kConnections, int k, String destination) {
        List<Leg> legs = new ArrayList<>();

        for (int i = k; i > 0; i--) {
            Connection connection = kConnections.get(destination).get(i);
            Leg leg = connection.getLeg();

            legs.add(leg);
            destination = leg.origin;
        }

        Collections.reverse(legs);

        return legs;
    }


    private int getDepartureTime(List<Leg> legs) {
        int transferDuration = 0;

        for (Leg leg : legs) {
            if (leg.isTransfer()) {
                transferDuration += leg.duration;
            }
            else {
                return leg.stopTimes.get(0).departureTime - transferDuration;
            }
        }

        return 0;
    }

    private int getArrivalTime(List<Leg> legs) {
        int transferDuration = 0;

        for (int i = legs.size() - 1; i >= 0; i--) {
            Leg leg = legs.get(i);

            if (leg.isTransfer()) {
                transferDuration += leg.duration;
            }
            else {
                return leg.stopTimes.get(leg.stopTimes.size() - 1).arrivalTime + transferDuration;
            }
        }

        return 0;
    }
}
