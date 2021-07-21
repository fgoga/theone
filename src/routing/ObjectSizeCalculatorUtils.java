package routing;

import main.myagent.InstrumentationAgent;

import java.util.Map;

public class ObjectSizeCalculatorUtils {

    public static long getContactPropertiesSizeJVM(Map<String, ContactProperties> contactPropertiesMap) {
        long size = 0;
        size += InstrumentationAgent.getObjectSize(contactPropertiesMap);

        for (Map.Entry<String, ContactProperties> e : contactPropertiesMap.entrySet()) {
            size += InstrumentationAgent.getObjectSize(e.getKey());

            ContactProperties value = e.getValue();
            size += InstrumentationAgent.getObjectSize(value);
            size += InstrumentationAgent.getObjectSize(value.getContactTimeHistory());

            for (ContactPeriod contactPeriod : value.getContactTimeHistory()) {
                size += InstrumentationAgent.getObjectSize(contactPeriod);
            }
        }


        return size;
    }

    public static long getContactPropertiesSize(Map<String, ContactProperties> contactPropertiesMap) {
        long size = 0;

        for (Map.Entry<String, ContactProperties> e : contactPropertiesMap.entrySet()) {
            size += getContactPropertiesSizeOnMap(e);
        }


        return size;
    }

    public static long getContactPropertiesSizeOnMap(Map.Entry<String, ContactProperties> e) {
        return getContactPropertiesSizeOnMap(e.getKey(), e.getValue());
    }

    public static long getContactPropertiesSizeOnMap(String key, ContactProperties contactProperties) {
        long size = 0;

        char[] chars = key.toCharArray();
        // TODO: 7/5/2021 check solution
        size += chars.length; //  1 char allocates 1 byte of memory

        size += ContactProperties.SIZE_OF;
        size += contactProperties.getContactTimeHistory().size() * ContactPeriod.SIZE_OF;
        return size;
    }

    public static long getStorageAndEnergyPropertiesSize(Map<String, StorageAndEnergyProperties> storageAndEnergyPropertiesMap) {
        long size = 0;

        for (Map.Entry<String, StorageAndEnergyProperties> e : storageAndEnergyPropertiesMap.entrySet()) {
            size += getStorageAndEnergySizeOnMap(e);
        }


        return size;
    }

    public static long getStorageAndEnergySizeOnMap(Map.Entry<String, StorageAndEnergyProperties> e) {
        return getStorageAndEnergySizeOnMap(e.getKey(), e.getValue());
    }

    public static long getStorageAndEnergySizeOnMap(String key, StorageAndEnergyProperties storageAndEnergyProperties) {
        long size = 0;
        char[] chars = key.toCharArray();
        // TODO: 7/5/2021 check solution
        size += chars.length; //  1 char allocate 1 byte of memory

        size += StorageAndEnergyProperties.SIZE_OF;
        size += storageAndEnergyProperties.getNodePropertiesMap().size() * getNodePropertiesSizeOnMap(); //calculate memory allocated from keys of map
        return size;
    }

    public static long getNodePropertiesSizeOnMap() {
        return NodeProperties.SIZE_OF + Double.BYTES;
    }


}
