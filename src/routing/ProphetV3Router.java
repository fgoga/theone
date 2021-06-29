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
public class ProphetV3Router extends ActiveRouter {
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

    /**
     * last encouter timestamp (sim)time
     */

    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     *
     * @param s The settings object
     */
    public ProphetV3Router(Settings s) {
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
    protected ProphetV3Router(ProphetV3Router r) {
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
            updateContactPropertiesOnConnUp(otherHost);
            updateTransitiveContactProperties(otherHost);
        } else {
            //Update contact properties
            updateContactPropertiesOnConnDown(otherHost);

            //update storage & energy properties
            updateStorageAndEnergyPropertiesOnConnDown(otherHost);
            updateTransitiveStorageAndEnergyProperties(otherHost);
        }
    }

    private void updateStorageAndEnergyPropertiesOnConnDown(DTNHost otherHost) {
        Map<String, StorageAndEnergyProperties> mapFromThisHost = getHost().getStorageAndEnergyPropertiesMap();
        String nodeName = otherHost.toString();
        StorageAndEnergyProperties properties = mapFromThisHost.get(nodeName);

        if (properties != null) {
            properties.addNodeProperties(getFreeBufferSize(), SimClock.getTime());
        } else {
            properties = new StorageAndEnergyProperties(getBufferSize(), getFreeBufferSize(), SimClock.getTime());
            mapFromThisHost.put(nodeName, properties);
        }
    }

    private void updateTransitiveStorageAndEnergyProperties(DTNHost otherHost) {
        Map<String, StorageAndEnergyProperties> mapFromThisHost = getHost().getStorageAndEnergyPropertiesMap();
        Map<String, StorageAndEnergyProperties> mapFromOtherHost = otherHost.getStorageAndEnergyPropertiesMap();

        for (Map.Entry<String, StorageAndEnergyProperties> entry : mapFromOtherHost.entrySet()) {

            StorageAndEnergyProperties storageAndEnergyPropertiesFromOtherHost = entry.getValue();
            StorageAndEnergyProperties storageAndEnergyPropertiesFromThisHost = mapFromThisHost.get(entry.getKey());

            if (storageAndEnergyPropertiesFromThisHost != null) {
                TreeMap<Double, NodeProperties> nodePropertiesForThisHost = storageAndEnergyPropertiesFromThisHost.getNodePropertiesMap();
                TreeMap<Double, NodeProperties> nodePropertiesFromOtherHost = storageAndEnergyPropertiesFromOtherHost.getNodePropertiesMap();

                for (Map.Entry<Double, NodeProperties> entry2 : nodePropertiesFromOtherHost.entrySet()) {
                    nodePropertiesForThisHost.put(entry2.getKey(), entry2.getValue());
                }

            } else {
                mapFromThisHost.put(entry.getKey(), entry.getValue());
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
            if (entry.getKey().equals(composedKey)) {
                continue;
            }

            if (contactPropertiesFromThisHost != null) {
                ContactPeriod lastContactPeriodFromOtherHost = contactPropertiesFromOtherHost.getLastContactPeriod();
                ContactPeriod lastContactPeriodFromThisHost = contactPropertiesFromThisHost.getLastContactPeriod();
                if (lastContactPeriodFromOtherHost.getStartTime() < lastContactPeriodFromThisHost.getStartTime()) {
                    continue;
                }
            }

            mapFromThisHost.put(entry.getKey(), entry.getValue());

        }

    }

    private void updateContactPropertiesOnConnUp(DTNHost otherHost) {
        Map<String, ContactProperties> mapFromThisHost = getHost().getContactPropertiesMap();
        String composedKey = getComposedKey(getHost().toString(), otherHost.toString());
        ContactProperties contactProperties = mapFromThisHost.get(composedKey);

        if (contactProperties != null) {
            contactProperties.incrementCounter();
            contactProperties.addStartTime(SimClock.getTime());
        } else {
            contactProperties = new ContactProperties(SimClock.getTime());
            mapFromThisHost.put(composedKey, contactProperties);
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

        top.addMoreInfo(ri);
        return top;
    }

    @Override
    public MessageRouter replicate() {
        ProphetV3Router r = new ProphetV3Router(this);
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
}

