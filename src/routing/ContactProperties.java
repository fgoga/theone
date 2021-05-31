package routing;

public class ContactProperties {
    private int frequency;
    private double totalContactTime;
    private double lastContactTime;

    public ContactProperties(int frequency, double totalContactTime, double lastContactTime) {
        this.setFrequency(frequency);
        this.setTotalContactTime(totalContactTime);
        this.setLastContactTime(lastContactTime);
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void incrementFrequency() {
        this.frequency += 1;
    }

    public double getTotalContactTime() {
        return totalContactTime;
    }

    public void setTotalContactTime(double totalContactTime) {
        this.totalContactTime = totalContactTime;
    }

    public void incrementTotalContactTime(double timeDifference) {
        this.totalContactTime += timeDifference;
    }

    public double getLastContactTime() {
        return lastContactTime;
    }

    public void setLastContactTime(double lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

    @Override
    public String toString() {
        return "ContactProperties{" +
                "frequency=" + frequency +
                ", totalContactTime=" + totalContactTime +
                ", lastContactTime=" + lastContactTime +
                '}';
    }
}
