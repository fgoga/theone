package routing;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CalculateOverheadSumInTimeIntervals {

    private static TreeMap<Double , Long> everyThirtyMinutesOverheadMap = new TreeMap<>();

    public static void main(String[] args) throws IOException {

        String fileName = "C:\\Users\\Fiona\\Desktop\\STORAGE_OVERHEAD_STORAGE_ENERGY_CSV_PATH.csv";

        List<OverheadData> overheadData = new CsvToBeanBuilder(new FileReader(fileName))
                .withType(OverheadData.class)
                .build()
                .parse();

        overheadData.sort(Comparator.comparing(OverheadData::getTime));
//        for(OverheadData entry : overheadData) {
//            System.out.println("Read from Excel =" + entry.getTime());
//        }
        calculateOverheadEveryThirtyMinutes(overheadData);
        printMapData();
    }

    public static void calculateOverheadEveryThirtyMinutes(List<OverheadData> overheadData) {
        int k = 1;
        for (int i = 0; i < 16; i++) {
            long sum = 0;
            for (OverheadData entry : overheadData) {

                if (1800 * i <= entry.getTime() && entry.getTime() <= 1800 * k + 0.1) {
                    sum += entry.getOverheadValue();
                }
            }
            everyThirtyMinutesOverheadMap.put(1800.0*k , sum);
            k++;
        }

    }

    public static void printMapData(){
        for (Map.Entry<Double , Long> entry : everyThirtyMinutesOverheadMap.entrySet()) {
            System.out.println("Overhead every 30 minutes: " + entry.getKey() + "/" + entry.getValue());
        }
    }


}
