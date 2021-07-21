package routing;

import com.opencsv.CSVWriter;
import core.SimClock;
import util.Tuple;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvWriter {

    public static final String WORST_CASE_TRANSMISSION_OVERHEAD_CONTACT_PROPERTIES_CSV_PATH = "C:\\Users\\Fiona\\IdeaProjects\\theone_new\\reports\\WORST_CASE_TRANSMISSION_OVERHEAD_CONTACT_PROPERTIES_CSV_PATH.csv";
    public static final String WORST_CASE_TRANSMISSION_OVERHEAD_STORAGE_ENERGY_CSV_PATH = "C:\\Users\\Fiona\\IdeaProjects\\theone_new\\reports\\WORST_CASE_TRANSMISSION_OVERHEAD_STORAGE_ENERGY_CSV_PATH.csv";
    public static final String BEST_CASE_TRANSMISSION_OVERHEAD_CONTACT_PROPERTIES_CSV_PATH = "C:\\Users\\Fiona\\IdeaProjects\\theone_new\\reports\\BEST_CASE_TRANSMISSION_OVERHEAD_CONTACT_PROPERTIES_CSV_PATH.csv";
    public static final String BEST_CASE_TRANSMISSION_OVERHEAD_STORAGE_ENERGY_CSV_PATH = "C:\\Users\\Fiona\\IdeaProjects\\theone_new\\reports\\BEST_CASE_TRANSMISSION_OVERHEAD_STORAGE_ENERGY_CSV_PATH.csv";

    public static final String STORAGE_OVERHEAD_CONTACT_PROPERTIES_CSV_PATH = "C:\\Users\\Fiona\\IdeaProjects\\theone_new\\reports\\STORAGE_OVERHEAD_CONTACT_PROPERTIES_CSV_PATH.csv";
    public static final String STORAGE_OVERHEAD_STORAGE_ENERGY_CSV_PATH = "C:\\Users\\Fiona\\IdeaProjects\\theone_new\\reports\\STORAGE_OVERHEAD_STORAGE_ENERGY_CSV_PATH.csv";


    //transmission overhead
    public void writeToWorstCaseTransmissionOverHeadForContactProperties(ArrayList<Tuple<Double, Long>> list) {
        writeToFile(list, WORST_CASE_TRANSMISSION_OVERHEAD_CONTACT_PROPERTIES_CSV_PATH);
    }

    public void writeToWorstCaseTransmissionOverHeadForStorageAndEnergy(ArrayList<Tuple<Double, Long>> list) {
        writeToFile(list, WORST_CASE_TRANSMISSION_OVERHEAD_STORAGE_ENERGY_CSV_PATH);
    }

    public void writeToBestCaseTransmissionOverHeadForContactProperties(ArrayList<Tuple<Double, Long>> list) {
        writeToFile(list, BEST_CASE_TRANSMISSION_OVERHEAD_CONTACT_PROPERTIES_CSV_PATH);
    }

    public void writeToBestCaseTransmissionOverHeadForStorageAndEnergy(ArrayList<Tuple<Double, Long>> list) {
        writeToFile(list, BEST_CASE_TRANSMISSION_OVERHEAD_STORAGE_ENERGY_CSV_PATH);
    }

    //storage overhead
    public void writeToStorageOverHeadForContactProperties(ArrayList<Tuple<Double, Long>> list) {
        writeToFile(list, STORAGE_OVERHEAD_CONTACT_PROPERTIES_CSV_PATH);
    }

    public void writeToStorageOverHeadForStorageAndEnergy(ArrayList<Tuple<Double, Long>> list) {
        writeToFile(list, STORAGE_OVERHEAD_STORAGE_ENERGY_CSV_PATH);
    }

    private void writeToFile(long size, String path) {
        // default all fields are enclosed in double quotes
        // default separator is a comma

        String[] header = {String.valueOf(SimClock.getTime()), String.valueOf(size)};

        try (CSVWriter writer = new CSVWriter(new FileWriter(path, true))) {
            writer.writeNext(header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(ArrayList<Tuple<Double, Long>> list, String path) {
        // default all fields are enclosed in double quotes
        // default separator is a comma


        try (CSVWriter writer = new CSVWriter(new FileWriter(path, true))) {
            for (Tuple<Double, Long> tuple : list) {
                String[] row = {tuple.getKey().toString(), tuple.getValue().toString()};
                writer.writeNext(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
