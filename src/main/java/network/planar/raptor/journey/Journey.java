package network.planar.raptor.journey;

import java.util.List;

public class Journey {
    public final List<Leg> legs;
    public final int departureTime;
    public final int arrivalTime;

    public Journey(List<Leg> legs, int departureTime, int arrivalTime) {
        this.legs = legs;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }
}
