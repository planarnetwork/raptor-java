package network.planar.raptor.gtfs;

import java.util.List;
import java.util.Map;

public class Calendar {
    public final int startDate;
    public final int endDate;
    public final List<Boolean> days;
    public final Map<Integer, Boolean> dates;

    public Calendar(int startDate, int endDate, List<Boolean> days, Map<Integer, Boolean> dates) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.days = days;
        this.dates = dates;
    }
}
