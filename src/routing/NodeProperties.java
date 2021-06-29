package routing;

public class NodeProperties {
    private double freeBufferSize;
//    private double availableBattery;

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

}
