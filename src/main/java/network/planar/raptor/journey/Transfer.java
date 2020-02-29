package network.planar.raptor.journey;

public class Transfer {
    public final String origin;
    public final String destination;
    public final int duration;
    public final int startTime;
    public final int endTime;

    public Transfer(String origin, String destination, int duration, int startTime, int endTime) {
        this.origin = origin;
        this.destination = destination;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
