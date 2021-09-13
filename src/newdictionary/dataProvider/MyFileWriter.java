package newdictionary.dataProvider;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class MyFileWriter {

    private final static String mapPath = System.getProperty("user.dir") + "\\DataBases\\dictionary.txt";
    private static final File mapFile = new File(mapPath);
    private final static String setPath = System.getProperty("user.dir") + "\\DataBases\\cleanEnglishWords.txt";
    private static final File setFile = new File(setPath);
    private static final String currentDayDictionary = System.getProperty("user.dir") + "\\DataBases\\dictionaryForCurrentDay.txt";
    private static final File fileForCurrentDayDictionary = new File(currentDayDictionary);
    private static final String pathToCurrentDate = System.getProperty("user.dir") + "\\DataBases\\currentDate.txt";


    static void writeToFile(Map<String, String> words) throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(mapFile, true));
        for (Map.Entry<String, String> entry : words.entrySet()) {
            bw.write(entry.getKey() + " : " + entry.getValue());
            bw.newLine();
            bw.flush();
        }
    }

    public static List<String> readKeyFromMapFile() throws IOException {
        String line;
        List<String> keyWords = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(mapFile));
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(":");
            String key = parts[0].trim();
            keyWords.add(key);
        }
        reader.close();
        //keyWords.stream().forEach(System.out::println);
        return keyWords;
    }

    static void writeToSetFile(LinkedList<String> words) throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(setFile));
        for (String word : words) {
            bw.write(word);
            bw.newLine();
            bw.flush();
        }
    }

    public static LinkedList<String> readFromSetFile() throws IOException {
        String line;
        LinkedList<String> keyWords = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new FileReader(setFile));
        while ((line = reader.readLine()) != null) {
            keyWords.add(line);
        }
        reader.close();
        return keyWords;
    }

    static LinkedList<String> deleteWordsFromArray(LinkedList<String> beforeDeleteList, List<String> deleteWord) {
        LinkedList<String> afterDeleteList = new LinkedList<>(beforeDeleteList);
        for (String word : deleteWord) {
            afterDeleteList.remove(word);
        }
        return afterDeleteList;
    }
    static void writeToCurrentDayDictionary(Map<String, String> words) throws IOException {
        boolean currentDate = LocalDate.now().toString().equals(readDateFromCurrentDateFile());

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileForCurrentDayDictionary, currentDate));
        for (Map.Entry<String, String> entry : words.entrySet()) {
            bw.write(entry.getKey() + " : " + entry.getValue());
            bw.newLine();
            bw.flush();
        }
    }

    static Map<String, String> readFromCurrentDayFile() {
        String line;
        Map<String, String> currentDayWords = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileForCurrentDayDictionary));
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String key = parts[0].trim();
                String value = parts[1].trim();
                currentDayWords.put(key, value);
                //reader.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return currentDayWords;
    }

    static void writeCurrentDate() throws IOException {
        LocalDate currentDate = LocalDate.now();
        FileWriter writer = new FileWriter(pathToCurrentDate);
        writer.write(currentDate.toString());
        writer.flush();
    }

    public static String readDateFromCurrentDateFile() throws IOException {
        String line;
        String searchWord = "";
        BufferedReader reader = new BufferedReader(new FileReader(pathToCurrentDate));
        while ((line = reader.readLine()) != null) {
            searchWord = line;
        }
        reader.close();
        return searchWord;
    }
    public static Map<String, String> getReadFromCurrentDayFile() {
        return readFromCurrentDayFile();
    }
    public static List<String> readValueFromMapFile() throws IOException {
        String line;
        List<String> values = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(mapFile));
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(":");
            String key = parts[1].trim();
            values.add(key);
        }
        reader.close();
        //values.stream().forEach(System.out::println);
        return values;
    }
}
