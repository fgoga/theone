package routing;

public class ContactPeriod {

    public static final long SIZE_OF = 16;

    private double startTime;//8 bytes
    private double endTime;//8 bytes

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
