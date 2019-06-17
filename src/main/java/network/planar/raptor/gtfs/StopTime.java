package network.planar.raptor.gtfs;

public class StopTime {
    public final String stop;
    public final int arrivalTime;
    public final int departureTime;
    public final boolean pickUp;
    public final boolean dropOff;

    public StopTime(String stop, int arrivalTime, int departureTime, boolean pickUp, boolean dropOff) {
        this.stop = stop;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.pickUp = pickUp;
        this.dropOff = dropOff;
    }

}
