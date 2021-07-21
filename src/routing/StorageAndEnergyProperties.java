package routing;

import java.util.*;

public class    StorageAndEnergyProperties {

    public static final long SIZE_OF = 16;

    private double bufferSize;  //8 bytes
    private double batteryCapacity; //8 bytes
    private final TreeMap<Double , NodeProperties> nodePropertiesMap = new TreeMap<>();


    public StorageAndEnergyProperties(long bufferSize, double freeBufferSize, double lastContactTime) {
        this.bufferSize = bufferSize;

        addNodeProperties(freeBufferSize, lastContactTime);
    }

    public double getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(double bufferSize) {
        this.bufferSize = bufferSize;
    }

//    public double getBatteryCapacity() {
//        return batteryCapacity;
//    }
//
//    public void setBatteryCapacity(double batteryCapacity) {
//        this.batteryCapacity = batteryCapacity;
//    }

    public TreeMap<Double,NodeProperties> getNodePropertiesMap() {
        return nodePropertiesMap;
    }

    public void addNodeProperties(double freeBufferSize, double lastContactTime) {
        NodeProperties nodeProperties = new NodeProperties(freeBufferSize);
        nodePropertiesMap.put(lastContactTime, nodeProperties);
    }

    @Override
    public String toString() {
        return "StorageAndEnergyProperties{" +
                "bufferSize=" + getBufferSize() +
                ", nodeProperties=" + getNodePropertiesMap() +
                '}';
    }
}
