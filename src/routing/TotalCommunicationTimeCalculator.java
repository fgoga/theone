package routing;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TotalCommunicationTimeCalculator {

    public static void main(String[] args) throws IOException {

        String fileName = "C:\\Users\\Fiona\\Desktop\\contactsdata.csv";

        List<ContactsData> contactsData = new CsvToBeanBuilder(new FileReader(fileName))
                .withType(ContactsData.class)
                .build()
                .parse();

        contactsData.sort(Comparator.comparing(ContactsData::getContactTime));
//
//        //contactsData.forEach(cd -> System.out.println("cd.getNode1Name() + \"->\" + cd.getNode2Name() = " + cd.getNode1Name() + "->" + cd.getNode2Name()));
//        for (ContactsData temp : contactsData) {
//            System.out.print(temp.getContactTime() + " ");
//            System.out.print(temp.getNode1Name() + " ");
//            System.out.print(temp.getNode2Name() + " ");
//            System.out.print(temp.getConnectionType() + " ");
//            System.out.println();
//        }

        System.out.println("Total communication time =" + calculateTotalCommunicationTime(contactsData));
    }



    public static double calculateTotalCommunicationTime(List<ContactsData> contactsData) {
        double totalContactTime = 0.0;

        for (int i = 0; i < contactsData.size() - 1; i++) {
            ContactsData temp1 = contactsData.get(i);
            if (temp1.getConnectionType().equals("up")) {
                for (int j = i + 1; j < contactsData.size(); j++) {
                    ContactsData temp2 = contactsData.get(j);
                    if (contactsData.get(j).getNode1Name().equals(temp1.getNode1Name()) && temp2.getNode2Name().equals(temp1.getNode2Name()) && temp2.getConnectionType().equals("down")) {
                        totalContactTime += temp2.getContactTime() - temp1.getContactTime();
                        break;
                    }
                }
            }
        }


        return totalContactTime;
    }

}


