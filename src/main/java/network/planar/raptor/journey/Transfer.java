package network.planar.raptor.journey;

public class Transfer {
    public final String origin;
    public final String destination;
    public final int duration;

    public Transfer(String origin, String destination, int duration) {
        this.origin = origin;
        this.destination = destination;
        this.duration = duration;
    }
}
