package network.planar.raptor.gtfs.feed;

import com.conveyal.gtfs.GTFSFeed;

public class GtfsFeedFactory {

    private final GtfsFeedAdapter adapter;

    public GtfsFeedFactory(GtfsFeedAdapter adapter) {
        this.adapter = adapter;
    }

    public GtfsFeed create(String file) {
        GTFSFeed feed = GTFSFeed.fromFile(file);

        return adapter.convert(feed);
    }

}
