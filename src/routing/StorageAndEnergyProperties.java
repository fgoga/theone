package routing;

import java.util.*;

public class StorageAndEnergyProperties {

    private double bufferSize;
//    private double batteryCapacity;
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
}
