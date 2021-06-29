package routing;

import java.util.ArrayList;
import java.util.List;

public class ContactProperties {
    private double totalContactTime;
    private double initialContactTime;
    private int counter;
    private final List<ContactPeriod> contactTimeHistory = new ArrayList<>();


    public ContactProperties(double initialContactTime) {
        counter = 1;
        totalContactTime = 0;
        this.initialContactTime = initialContactTime;

        ContactPeriod contactPeriod = new ContactPeriod(initialContactTime);
        contactTimeHistory.add(contactPeriod);
    }

    public int getCounter() {
        return counter;
    }

    public void incrementCounter() {
        counter += 1;
    }

    public double getTotalContactTime() {
        return totalContactTime;
    }


    private void incrementTotalContactTime(double timeDifference) {
        this.totalContactTime += timeDifference;
    }


    public double getInitialContactTime() {
        return initialContactTime;
    }

    private void setInitialContactTime(double initialContactTime) {
        this.initialContactTime = initialContactTime;
    }

    public List<ContactPeriod> getContactTimeHistory() {
        return contactTimeHistory;
    }

    public void addStartTime(double startTime) {
        ContactPeriod contactPeriod = new ContactPeriod(startTime);
        contactTimeHistory.add(contactPeriod);
    }

    public void addEndTime(double endTime) {
        ContactPeriod contactPeriod = getLastContactPeriod();
        contactPeriod.setEndTime(endTime);

        double contactDuration = contactPeriod.getEndTime() - contactPeriod.getStartTime();
        incrementTotalContactTime(contactDuration);
    }

    public ContactPeriod getLastContactPeriod() {
        return contactTimeHistory.get(contactTimeHistory.size() - 1);
    }

    @Override
    public String toString() {
        return "ContactProperties{" +
                "counter=" + getCounter() +
                ", totalContactTime=" + getTotalContactTime() +
                ", contactTimeHistory=" + getContactTimeHistory() +
                '}';
    }


}
