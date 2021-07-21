package test;

import main.myagent.InstrumentationAgent;
import org.junit.jupiter.api.Test;
import routing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TotalCommunicationTimeCalculatorTest {

    @Test
    public void test() {

        List<ContactsData> beans= new ArrayList<>();
        double totalContactTime = 0.0;

        ContactsData contactsData1 = new ContactsData(3.0, "1", "2", "up");

        ContactsData contactsData2 = new ContactsData(3.0, "1", "3", "up");

        ContactsData contactsData3 = new ContactsData(3.0, "2", "3", "up");

        ContactsData contactsData4 = new ContactsData(5.0, "2", "3", "down");

        ContactsData contactsData5 = new ContactsData(6.0, "1", "3", "down");

        ContactsData contactsData6 = new ContactsData(6.0, "1", "2", "down");

        ContactsData contactsData7 = new ContactsData(8.0, "1", "2", "up");

        ContactsData contactsData8 = new ContactsData(10.0, "1", "2", "down");

        beans.add(contactsData1);
        beans.add(contactsData2);
        beans.add(contactsData3);
        beans.add(contactsData4);
        beans.add(contactsData5);
        beans.add(contactsData6);
        beans.add(contactsData7);
        beans.add(contactsData8);

        assertEquals(10.0 , TotalCommunicationTimeCalculator.calculateTotalCommunicationTime(beans), 0);
    }
}
