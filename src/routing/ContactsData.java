package routing;

import com.opencsv.bean.CsvBindByPosition;

public class ContactsData {

    @CsvBindByPosition(position = 0)
    private double contactTime;

    @CsvBindByPosition(position = 1)
    private String node1Name;

    @CsvBindByPosition(position = 2)
    private String node2Name;

    @CsvBindByPosition(position = 3)
    private String connectionType;

    public ContactsData() {}

    public ContactsData(double contactTime, String node1Name, String node2Name, String connectionType) {
        this.contactTime = contactTime;
        this.node1Name = node1Name;
        this.node2Name = node2Name;
        this.connectionType = connectionType;
    }

    public double getContactTime() {
        return contactTime;
    }

    public void setContactTime(double contactTime) {
        this.contactTime = contactTime;
    }

    public String getNode1Name() {
        return node1Name;
    }

    public void setNode1Name(String node1Name) {
        this.node1Name = node1Name;
    }

    public String getNode2Name() {
        return node2Name;
    }

    public void setNode2Name(String node2Name) {
        this.node2Name = node2Name;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
}
