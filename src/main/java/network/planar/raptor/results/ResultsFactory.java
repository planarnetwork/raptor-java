package network.planar.raptor.results;

import network.planar.raptor.journey.Connection;
import network.planar.raptor.journey.Journey;

import java.util.List;
import java.util.Map;

public interface ResultsFactory {

    List<Journey> getResults(Map<String, Map<Integer, Connection>> kConnections, String destination);

}
