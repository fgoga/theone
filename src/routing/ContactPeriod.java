package routing;

public class ContactPeriod {
    private double startTime;
    private double endTime;

    public ContactPeriod(double startTime) {
        this.startTime = startTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }


    @Override
    public String toString() {
        return "ContactPeriod{" +
                "startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
