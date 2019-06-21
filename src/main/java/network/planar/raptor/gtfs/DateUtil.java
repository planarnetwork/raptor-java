package network.planar.raptor.gtfs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static DateTimeFormatter gtfsFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static int toGtfsDate(LocalDate date) {
        return Integer.parseInt(date.format(gtfsFormat));
    }
}
