/*
 * Copyright 2011 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 * The Original PRoPHET code updated to PRoPHETv2 router
 * by Samo Grasic(samo@grasic.net) - Jun 2011
 */
package routing;

import core.*;
import routing.util.RoutingInfo;
import util.Tuple;

import java.util.*;

/**
 * Implementation of PRoPHETv2" router as described in
 * http://tools.ietf.org/html/draft-irtf-dtnrg-prophet-09
 */
public class ExchangePropertiesRouter extends ActiveRouter {
    /**
     * delivery predictability initialization constant
     */
    public static final double PEncMax = 0.5;
    /**
     * typical interconnection time in seconds
     */
    public static final double I_TYP = 1800;
    /**
     * delivery predictability transitivity scaling constant default value
     */
    public static final double DEFAULT_BETA = 0.9;
    /**
     * delivery predictability aging constant
     */
    public static final double DEFAULT_GAMMA = 0.999885791;

    public static final String KEY_PATTERN = "%s<->%s";

    Random randomGenerator = new Random();

    /**
     * Prophet router's setting namespace ({@value})
     */
    public static final String PROPHET_NS = "ProphetV2Router";
    /**
     * Number of seconds in time unit -setting id ({@value}).
     * How many seconds one time unit is when calculating aging of
     * delivery predictions. Should be tweaked for the scenario.
     */
    public static final String SECONDS_IN_UNIT_S = "secondsInTimeUnit";

    /**
     * Transitivity scaling constant (beta) -setting id ({@value}).
     * Default value for setting is {@link #DEFAULT_BETA}.
     */
    public static final String BETA_S = "beta";

    /**
     * Predictability aging constant (gamma) -setting id ({@value}).
     * Default value for setting is {@link #DEFAULT_GAMMA}.
     */
    public static final String GAMMA_S = "gamma";

    /**
     * the value of nrof seconds in time unit -setting
     */
    private int secondsInTimeUnit;
    /**
     * value of beta setting
     */
    private double beta;
    /**
     * value of gamma setting
     */
    private double gamma;

    private CsvWriter csvWriter = new CsvWriter();

    private ArrayList<Tuple<Double, Long>> worstCaseTransmissionOverHeadForContactProperties = new ArrayList<>();
    private ArrayList<Tuple<Double, Long>> worstCaseTransmissionOverHeadForStorageAndEnergy = new ArrayList<>();

    private ArrayList<Tuple<Double, Long>> bestCaseTransmisionOverHeadForContactProperties = new ArrayList<>();
    private ArrayList<Tuple<Double, Long>> bestCaseTransmissionOverHeadForStorageAndEnergy = new ArrayList<>();

    private ArrayList<Tuple<Double, Long>> storageOverHeadForContactProperties = new ArrayList<>();
    private ArrayList<Tuple<Double, Long>> storageOverHeadForStorageAndEnergy = new ArrayList<>();

    /**
     * last encouter timestamp (sim)time
     */

    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     *
     * @param s The settings object
     */
    public ExchangePropertiesRouter(Settings s) {
        super(s);
        Settings prophetSettings = new Settings(PROPHET_NS);
        secondsInTimeUnit = prophetSettings.getInt(SECONDS_IN_UNIT_S);
        if (prophetSettings.contains(BETA_S)) {
            beta = prophetSettings.getDouble(BETA_S);
        } else {
            beta = DEFAULT_BETA;
        }
        if (prophetSettings.contains(GAMMA_S)) {
            gamma = prophetSettings.getDouble(GAMMA_S);
        } else {
            gamma = DEFAULT_GAMMA;
        }


    }

    /**
     * Copyc onstructor.
     *
     * @param r The router prototype where setting values are copied from
     */
    protected ExchangePropertiesRouter(ExchangePropertiesRouter r) {
        super(r);
        this.secondsInTimeUnit = r.secondsInTimeUnit;
        this.beta = r.beta;
        this.gamma = r.gamma;
    }


    @Override
    public void changedConnection(Connection con) {
        DTNHost otherHost = con.getOtherNode(getHost());
        if (con.isUp()) {
            //Update contact properties
//            csvWriter.writeToWorstCaseTransmissionOverHeadForContactProperties(ObjectSizeCalculatorUtils.getContactPropertiesSize(otherHost.getContactPropertiesMap()));
            System.out.println("Worst case Transmission overhead getContactPropertiesSize = " + ObjectSizeCalculatorUtils.getContactPropertiesSize(otherHost.getContactPropertiesMap()));
            getWorstCaseTransmissionOverHeadForContactProperties().add(
                    new Tuple<Double, Long>(
                            SimClock.getTime(),
                            ObjectSizeCalculatorUtils.getContactPropertiesSize(otherHost.getContactPropertiesMap()))
            );

//            csvWriter.writeToWorstCaseTransmissionOverHeadForStorageAndEnergy();
            System.out.println("Worst case Transmission overhead getStorageAndEnergyPropertiesSize = " + ObjectSizeCalculatorUtils.getStorageAndEnergyPropertiesSize(otherHost.getStorageAndEnergyPropertiesMap()));
            getWorstCaseTransmissionOverHeadForStorageAndEnergy().add(
                    new Tuple<>(
                            SimClock.getTime(),
                            ObjectSizeCalculatorUtils.getStorageAndEnergyPropertiesSize(otherHost.getStorageAndEnergyPropertiesMap())
                    )
            );

            updateContactPropertiesOnConnUp(otherHost);
            updateTransitiveContactProperties(otherHost);

            //update storage & energy properties
            updateStorageAndEnergyProperties(otherHost);
            updateTransitiveStorageAndEnergyProperties(otherHost);
        } else {
            //Update contact properties
            updateContactPropertiesOnConnDown(otherHost);


        }
    }

    private void updateStorageAndEnergyProperties(DTNHost otherHost) {
        Map<String, StorageAndEnergyProperties> mapFromThisHost = getHost().getStorageAndEnergyPropertiesMap();
        String nodeName = otherHost.toString();
        StorageAndEnergyProperties properties = mapFromThisHost.get(nodeName);

        MessageRouter otherRouter = otherHost.getRouter();
        if (properties != null) {

            properties.addNodeProperties(otherRouter.getFreeBufferSize(), SimClock.getTime());

            System.out.println("Best case Transmission overhead StorageAndEnergy = " + Long.BYTES);
            getBestCaseTransmissionOverHeadForStorageAndEnergy().add(
                    new Tuple<Double, Long>(SimClock.getTime(),
                            Long.valueOf(Long.BYTES))
            );

            System.out.println("Storage overhead StorageAndEnergy = " + ObjectSizeCalculatorUtils.getNodePropertiesSizeOnMap());
            getStorageOverHeadForStorageAndEnergy().add(
                    new Tuple<>(
                            SimClock.getTime(),
                            ObjectSizeCalculatorUtils.getNodePropertiesSizeOnMap()
                    )
            );
        } else {
            properties = new StorageAndEnergyProperties(otherRouter.getBufferSize(), otherRouter.getFreeBufferSize(), SimClock.getTime());
            mapFromThisHost.put(nodeName, properties);

            System.out.println("Best case Transmission overhead StorageAndEnergy = " + (Long.BYTES + Long.BYTES));
            getBestCaseTransmissionOverHeadForStorageAndEnergy().add(
                    new Tuple<Double, Long>(SimClock.getTime(),
                            Long.valueOf(Long.BYTES) + Long.valueOf(Long.BYTES))
            );

            System.out.println("Storage overhead StorageAndEnergy = " + ObjectSizeCalculatorUtils.getStorageAndEnergySizeOnMap(nodeName, properties));
            getStorageOverHeadForStorageAndEnergy().add(
                    new Tuple<>(
                            SimClock.getTime(),
                            ObjectSizeCalculatorUtils.getStorageAndEnergySizeOnMap(nodeName, properties
                            )
                    )
            );
        }
    }

    private void updateTransitiveStorageAndEnergyProperties(DTNHost otherHost) {
        Map<String, StorageAndEnergyProperties> mapFromThisHost = getHost().getStorageAndEnergyPropertiesMap();
        Map<String, StorageAndEnergyProperties> mapFromOtherHost = otherHost.getStorageAndEnergyPropertiesMap();

        for (Map.Entry<String, StorageAndEnergyProperties> entry : mapFromOtherHost.entrySet()) {

            if (entry.getKey() == getHost().toString()) {
                continue;
            }

            StorageAndEnergyProperties storageAndEnergyPropertiesFromOtherHost = entry.getValue();
            StorageAndEnergyProperties storageAndEnergyPropertiesFromThisHost = mapFromThisHost.get(entry.getKey());

            if (storageAndEnergyPropertiesFromThisHost != null) {
                TreeMap<Double, NodeProperties> nodePropertiesForThisHost = storageAndEnergyPropertiesFromThisHost.getNodePropertiesMap();
                TreeMap<Double, NodeProperties> nodePropertiesFromOtherHost = storageAndEnergyPropertiesFromOtherHost.getNodePropertiesMap();

                for (Map.Entry<Double, NodeProperties> entry2 : nodePropertiesFromOtherHost.entrySet()) {

                    if (nodePropertiesForThisHost.get(entry2.getKey()) == null) {

                        nodePropertiesForThisHost.put(entry2.getKey(), entry2.getValue());

                        System.out.println("Best case Transmission overhead StorageAndEnergy = " + ObjectSizeCalculatorUtils.getNodePropertiesSizeOnMap());
                        getBestCaseTransmissionOverHeadForStorageAndEnergy().add(
                                new Tuple<>(SimClock.getTime(),
                                        ObjectSizeCalculatorUtils.getNodePropertiesSizeOnMap())
                        );

                        System.out.println("Storage overhead StorageAndEnergy = " + ObjectSizeCalculatorUtils.getNodePropertiesSizeOnMap());
                        getStorageOverHeadForStorageAndEnergy().add(
                                new Tuple<>(
                                        SimClock.getTime(),
                                        ObjectSizeCalculatorUtils.getNodePropertiesSizeOnMap()
                                )
                        );
                    }
                }

            } else {
                mapFromThisHost.put(entry.getKey(), entry.getValue());

                System.out.println("Best case Transmission overhead StorageAndEnergy = " + ObjectSizeCalculatorUtils.getStorageAndEnergySizeOnMap(entry));
                getBestCaseTransmissionOverHeadForStorageAndEnergy().add(
                        new Tuple<>(SimClock.getTime(),
                                ObjectSizeCalculatorUtils.getStorageAndEnergySizeOnMap(entry))
                );

                System.out.println("Storage overhead StorageAndEnergy = " + ObjectSizeCalculatorUtils.getStorageAndEnergySizeOnMap(entry));
                getStorageOverHeadForStorageAndEnergy().add(
                        new Tuple<>(
                                SimClock.getTime(),
                                ObjectSizeCalculatorUtils.getStorageAndEnergySizeOnMap(entry)
                        )
                );
            }

        }
    }

    private void updateTransitiveContactProperties(DTNHost otherHost) {
        Map<String, ContactProperties> mapFromThisHost = getHost().getContactPropertiesMap();
        Map<String, ContactProperties> mapFromOtherHost = otherHost.getContactPropertiesMap();

        for (Map.Entry<String, ContactProperties> entry : mapFromOtherHost.entrySet()) {

            ContactProperties contactPropertiesFromOtherHost = entry.getValue();
            ContactProperties contactPropertiesFromThisHost = mapFromThisHost.get(entry.getKey());

            String composedKey = getComposedKey(getHost().toString(), otherHost.toString());
            if (entry.getKey().equals(composedKey) || entry.getKey().contains(getHost().toString())) {
                continue;
            }


            if (contactPropertiesFromThisHost != null) {
                ContactPeriod lastContactPeriodFromOtherHost = contactPropertiesFromOtherHost.getLastContactPeriod();
                ContactPeriod lastContactPeriodFromThisHost = contactPropertiesFromThisHost.getLastContactPeriod();
                if (lastContactPeriodFromOtherHost.getStartTime() < lastContactPeriodFromThisHost.getStartTime()) {
                    continue;
                }

                for (ContactPeriod cp : entry.getValue().getContactTimeHistory()) {
                    if (cp.getStartTime() > lastContactPeriodFromThisHost.getStartTime()) {

                        contactPropertiesFromThisHost.addContactPeriod(cp);

                        System.out.println("Best case Transmission overhead getContactPropertieSize = " + ObjectSizeCalculatorUtils.getContactPropertiesSizeOnMap(entry));
                        getBestCaseTransmisionOverHeadForContactProperties().add(
                                new Tuple<>(
                                        SimClock.getTime(),
                                        ContactPeriod.SIZE_OF
                                )
                        );


                        System.out.println("Storage overhead Contact Properties = " + ObjectSizeCalculatorUtils.getContactPropertiesSizeOnMap(entry));
                        getStorageOverHeadForContactProperties().add(
                                new Tuple<>(
                                        SimClock.getTime(),
                                        ContactPeriod.SIZE_OF
                                )
                        );
                    }
                }

            } else {
                mapFromThisHost.put(entry.getKey(), entry.getValue());

                System.out.println("Best case Transmission overhead getContactPropertieSize = " + ObjectSizeCalculatorUtils.getContactPropertiesSizeOnMap(entry));
                getBestCaseTransmisionOverHeadForContactProperties().add(
                        new Tuple<>(
                                SimClock.getTime(),
                                ObjectSizeCalculatorUtils.getContactPropertiesSizeOnMap(entry))
                );


                System.out.println("Storage overhead Contact Properties = " + ObjectSizeCalculatorUtils.getContactPropertiesSizeOnMap(entry));
                getStorageOverHeadForContactProperties().add(
                        new Tuple<>(
                                SimClock.getTime(),
                                ObjectSizeCalculatorUtils.getContactPropertiesSizeOnMap(entry)
                        )
                );
            }

        }

    }

    private void updateContactPropertiesOnConnUp(DTNHost otherHost) {
        Map<String, ContactProperties> mapFromThisHost = getHost().getContactPropertiesMap();
        String composedKey = getComposedKey(getHost().toString(), otherHost.toString());
        ContactProperties contactProperties = mapFromThisHost.get(composedKey);

        if (contactProperties != null) {
            contactProperties.incrementCounter();
            contactProperties.addStartTime(SimClock.getTime());

            System.out.println("Storage overhead ContactProperties = " + ContactPeriod.SIZE_OF);
            getStorageOverHeadForContactProperties().add(
                    new Tuple<>(
                            SimClock.getTime(),
                            ContactPeriod.SIZE_OF
                    )
            );
        } else {
            contactProperties = new ContactProperties(SimClock.getTime());
            mapFromThisHost.put(composedKey, contactProperties);

            System.out.println("Storage overhead ContactProperties = " + ObjectSizeCalculatorUtils.getContactPropertiesSizeOnMap(composedKey, contactProperties));
            getStorageOverHeadForContactProperties().add(
                    new Tuple<>(
                            SimClock.getTime(),
                            ObjectSizeCalculatorUtils.getContactPropertiesSizeOnMap(composedKey, contactProperties)
                    )
            );
        }
    }

    private void updateContactPropertiesOnConnDown(DTNHost otherHost) {
        String composedKey = getComposedKey(getHost().toString(), otherHost.toString());
        ContactProperties contactProperties = getHost().getContactPropertiesMap().get(composedKey);
        if (contactProperties != null) {
            contactProperties.addEndTime(SimClock.getTime());
        }
    }


    @Override
    public RoutingInfo getRoutingInfo() {
        RoutingInfo top = super.getRoutingInfo();
        RoutingInfo ri = new RoutingInfo("Resource properties");

        for (Map.Entry<String, ContactProperties> cp : getHost().getContactPropertiesMap().entrySet()) {
            String contactID = cp.getKey();
            ContactProperties contactProperties = cp.getValue();

            ri.addMoreInfo(new RoutingInfo(String.format("%s : %s",
                    contactID, contactProperties.toString())));
        }

        for (Map.Entry<String, StorageAndEnergyProperties> se : getHost().getStorageAndEnergyPropertiesMap().entrySet()) {
            String nodeID = se.getKey();
            StorageAndEnergyProperties storageAndEnergyProperties = se.getValue();

            ri.addMoreInfo(new RoutingInfo(String.format("%s : %s",
                    nodeID, storageAndEnergyProperties.toString())));
        }

        top.addMoreInfo(ri);
        return top;
    }

    @Override
    public void printOverHead() {
        csvWriter.writeToWorstCaseTransmissionOverHeadForContactProperties(getWorstCaseTransmissionOverHeadForContactProperties());
        csvWriter.writeToWorstCaseTransmissionOverHeadForStorageAndEnergy(getWorstCaseTransmissionOverHeadForStorageAndEnergy());
        csvWriter.writeToBestCaseTransmissionOverHeadForContactProperties(getBestCaseTransmisionOverHeadForContactProperties());
        csvWriter.writeToBestCaseTransmissionOverHeadForStorageAndEnergy(getBestCaseTransmissionOverHeadForStorageAndEnergy());
        csvWriter.writeToStorageOverHeadForContactProperties(getStorageOverHeadForContactProperties());
        csvWriter.writeToStorageOverHeadForStorageAndEnergy(getStorageOverHeadForStorageAndEnergy());

        System.out.println("Finished writing to csv");
    }


    @Override
    public MessageRouter replicate() {
        ExchangePropertiesRouter r = new ExchangePropertiesRouter(this);
        return r;
    }


    /**
     * Get Key for Hosts that are currently connected
     *
     * @param thisHostName  name of the current host
     * @param otherHostName name of connected host
     * @return composed key with names of currently connected host. Smallest string takes place first.
     */
    private String getComposedKey(String thisHostName, String otherHostName) {
        if (thisHostName.compareTo(otherHostName) < 0) {
            return String.format(KEY_PATTERN, thisHostName, otherHostName);
        } else {
            return String.format(KEY_PATTERN, otherHostName, thisHostName);
        }
    }

    public ArrayList<Tuple<Double, Long>> getWorstCaseTransmissionOverHeadForContactProperties() {
        return worstCaseTransmissionOverHeadForContactProperties;
    }

    public ArrayList<Tuple<Double, Long>> getWorstCaseTransmissionOverHeadForStorageAndEnergy() {
        return worstCaseTransmissionOverHeadForStorageAndEnergy;
    }

    public ArrayList<Tuple<Double, Long>> getBestCaseTransmisionOverHeadForContactProperties() {
        return bestCaseTransmisionOverHeadForContactProperties;
    }

    public ArrayList<Tuple<Double, Long>> getBestCaseTransmissionOverHeadForStorageAndEnergy() {
        return bestCaseTransmissionOverHeadForStorageAndEnergy;
    }

    public ArrayList<Tuple<Double, Long>> getStorageOverHeadForContactProperties() {
        return storageOverHeadForContactProperties;
    }

    public ArrayList<Tuple<Double, Long>> getStorageOverHeadForStorageAndEnergy() {
        return storageOverHeadForStorageAndEnergy;
    }
}

