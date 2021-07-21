package routing;

public class NodeProperties {

    public static final long SIZE_OF = 16;

    private double freeBufferSize;  //8 bytes
//    private double availableBattery;  //8 bytes

    public NodeProperties(double freeBufferSize) {
        this.freeBufferSize = freeBufferSize;
    }

    public double getFreeBufferSize() {
        return freeBufferSize;
    }

    public void setFreeBufferSize(double freeBufferSize) {
        this.freeBufferSize = freeBufferSize;
    }

//    public double getAvailableBattery() {
//        return availableBattery;
//    }
//
//    public void setAvailableBattery(double availableBattery) {
//        this.availableBattery = availableBattery;
//    }

    @Override
    public String toString() {
        return "{" +
                "freeBufferSize=" + getFreeBufferSize() +
                '}';
    }
}
