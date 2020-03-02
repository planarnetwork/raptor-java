package network.planar.raptor.results;

import network.planar.raptor.journey.Journey;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JourneyFilter implements Comparator<Journey> {

    public List<Journey> apply(List<Journey> journeys) {
        journeys.sort(this);
        List<Journey> result = new ArrayList<>(journeys.size());

        for (int i = 0; i < journeys.size(); i++) {
            Journey a = journeys.get(i);

            if (keepJourney(a, i, journeys)) {
                result.add(a);
            }
        }

        return result;
    }

    private boolean keepJourney(Journey a, int i, List<Journey> journeys) {
        for (int j = i + 1; j < journeys.size(); j++) {
            Journey b = journeys.get(j);

            if (arrivesEarlier(b, a) && lessChanges(b, a)) {
                return false;
            }
        }

        return true;
    }

    private boolean arrivesEarlier(Journey b, Journey a) {
        return b.arrivalTime <= a.arrivalTime;
    }

    private boolean lessChanges(Journey b, Journey a) {
        return b.legs.size() <= a.legs.size();
    }

    @Override
    public int compare(Journey a, Journey b) {
        return a.departureTime != b.departureTime
            ? a.departureTime - b.departureTime
            : b.arrivalTime - a.arrivalTime;
    }
}
