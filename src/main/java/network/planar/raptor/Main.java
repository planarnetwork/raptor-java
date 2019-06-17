package network.planar.raptor;

import network.planar.raptor.algorithm.RaptorAlgorithm;
import network.planar.raptor.algorithm.RaptorAlgorithmFactory;
import network.planar.raptor.gtfs.feed.GtfsFeed;
import network.planar.raptor.gtfs.feed.GtfsFeedAdapter;
import network.planar.raptor.gtfs.feed.GtfsFeedFactory;


public class Main {

    public static void main(String[] argv) {
        Long start = System.currentTimeMillis();
        GtfsFeedFactory factory = new GtfsFeedFactory(new GtfsFeedAdapter());
        GtfsFeed feed = factory.create("/home/linus/Documents/Rail/Data/gtfs/gb-rail-latest.zip");
        RaptorAlgorithmFactory raptorFactory = new RaptorAlgorithmFactory();
        RaptorAlgorithm raptor = raptorFactory.create(feed);
        Long end = System.currentTimeMillis();

        System.out.println("Trips: " + feed.trips.size());
        System.out.println("Time: " + (end - start) / 1000);
    }
}
