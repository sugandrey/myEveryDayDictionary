package newdictionary.dataProvider;

import javafx.collections.ObservableList;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EngWordsForCurrentDate {

    private List<String> myKeys = new ArrayList<>();
    private Map<String, String> translator = new HashMap<>();
    private List<String> deleteWords = new ArrayList<>();
    private Map<String, String> searchWords = new HashMap<>();
    private List<DictionaryTable> words = new ArrayList<>();

    private static void getEngWord() throws IOException {
        Set<String> words = new HashSet<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("DataBases\\englishWords.txt"));
        String word;
        String specChar = "[$&+,:;=?@#|'<>.{^*()}%!-\\]\\[\"]";
        while ((word = bufferedReader.readLine()) != null) {
            char firstChar = word.charAt(0);
            if (!Character.isDigit(firstChar)) {
                if (!word.contains(specChar)) {
                    words.add(word);
                }
            }
        }
        LinkedList<String> myWords = new LinkedList<>(words);
        MyFileWriter.writeToSetFile(myWords);
    }

    private void getTranslate() throws IOException, SQLException {

        List<String> dictionaryEngWords = new ArrayList<>();
        int count = 0;
        String translatedText;
//        LinkedList<String> myEngWords = MyFileWriter.readFromSetFile();
//        if (myEngWords.isEmpty()) {
//            getEngWord();
//            myEngWords = MyFileWriter.readFromSetFile();
//        }
        ResultSet set = DataBasesCreator.getInstance().selectFromCleanEngWords();
        ResultSet resultSet = DataBasesCreator.getInstance().selectFromDictionary();
        String upComma = "'";
            while (set.next()) {
                String word = set.getString(DataBasesCreator.CLEANENGWORDCOLUMN);
                if(word.contains(upComma)) {
                    word = word.replace(upComma,"");
                }
                int id = set.getInt(DataBasesCreator.IDCLEANENGWORDCOLUMN);
                String translateRequest = "q=" + word + "&target=ru&source=en";
                URL url = new URL("https://google-translate1.p.rapidapi.com/language/translate/v2");
                byte[] data = translateRequest.getBytes(StandardCharsets.UTF_8);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("accept-encoding", "application/gzip");
                connection.setRequestProperty("x-rapidapi-key", "4");
                connection.setRequestProperty("x-rapidapi-host", "google-translate1.p.rapidapi.com");
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                dos.write(data);
                dos.flush();
                dos.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    while (resultSet.next()) {
                        String engWord = resultSet.getString(DataBasesCreator.ENGWORDSDICTIONARYCOLUMN);
                        dictionaryEngWords.add(engWord);
                    }
                    resultSet.close();
                    if (!dictionaryEngWords.contains(word)) {
                        if (count < 5) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String inputLine;
                            StringBuilder response = new StringBuilder();
                            while ((inputLine = reader.readLine()) != null) {
                                response.append(inputLine);
                            }
                            reader.close();
                            System.out.println(response.toString());
                            String translatedWord = response.toString();
                            String[] arrayOfWords = translatedWord.split(":");
                            translatedText = arrayOfWords[3];
                            String delChars = "[$&+,:;=?@#|'<>.{^*()}%!-\\]\\[\"]";
                            translatedText = translatedText.replaceAll(delChars, "");
                            if(!translatedText.isEmpty()) {
                                count++;

                                words.add(new DictionaryTable(id,word,translatedText));
                                translator.put(word, translatedText);
                                myKeys.add(word);
                            }

                        } else {
                            break;
                        }
                    }
                    else {
                        deleteWords.add(word);
                    }

                } else {
                    deleteWords.add(word);
                }
                connection.disconnect();
            }
            set.close();





//            for (String word : myEngWords) {
//                String translateRequest = "q=" + word + "&target=ru&source=en";
//                URL url = new URL("https://google-translate1.p.rapidapi.com/language/translate/v2");
//                byte[] data = translateRequest.getBytes(StandardCharsets.UTF_8);
//
//                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//
//                connection.setDoOutput(true);
//                connection.setRequestMethod("POST");
//                connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
//                connection.setRequestProperty("accept-encoding", "application/gzip");
//                connection.setRequestProperty("x-rapidapi-key", "4");
//                connection.setRequestProperty("x-rapidapi-host", "google-translate1.p.rapidapi.com");
//
//                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
//                dos.write(data);
//                dos.flush();
//                dos.close();
//
//                System.out.println(connection.getResponseCode());
//                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK && !MyFileWriter.readKeyFromMapFile().contains(word)) {
//                    if (count < 5) {
//                        count++;
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                        String inputLine;
//                        StringBuilder response = new StringBuilder();
//                        while ((inputLine = reader.readLine()) != null) {
//                            response.append(inputLine);
//                        }
//                        reader.close();
//                        System.out.println(response.toString());
//                        String translatedWord = response.toString();
//                        String[] arrayOfWords = translatedWord.split(":");
//                        translatedText = arrayOfWords[3];
//                        String delChars = "[$&+,:;=?@#|'<>.{^*()}%!-\\]\\[\"]";
//                        translatedText = translatedText.replaceAll(delChars, "");
//                        translator.put(word, translatedText);
//                        myKeys.add(word);
//                    } else {
//                        break;
//                    }
//                } else {
//                    deleteWords.add(word);
//                }
//                connection.disconnect();
//            }

        //MyFileWriter.writeToSetFile(MyFileWriter.deleteWordsFromArray(myEngWords, deleteWords));

        for(DictionaryTable element : words) {
            DataBasesCreator.getInstance().insertToDictionary(element.getId(), element.getEngWord() ,element.getRusWord(),
                    DataBasesCreator.DICTIONARYTABLE);
        }
        DataBasesCreator.getInstance().deleteFromCurrentDayDictionary();
        for(DictionaryTable element : words) {
            DataBasesCreator.getInstance().insertToDictionary(element.getId(), element.getEngWord() ,element.getRusWord(),
                    DataBasesCreator.CURRENTDAYDICTIONARYTABLE);
        }
        MyFileWriter.writeToFile(translator);
        MyFileWriter.writeToCurrentDayDictionary(translator);
        MyFileWriter.writeCurrentDate();
        DataBasesCreator.getInstance().insertDateToDatabase();
        for (Map.Entry<String, String> entry : translator.entrySet()) {
            System.out.println(entry.getKey() + " -:- " + entry.getValue());
        }
        DataBasesCreator.getInstance().deleteWordsFromCleanEnglishWords(deleteWords);

//        String cleanRusText;
//        int count = 0;
//        LinkedList<String> myEngWords = MyFileWriter.readFromSetFile();
//
//        if (!myEngWords.isEmpty()) {
//            for (String word : myEngWords) {
//
//                URL url = new URL("https://dictionary.cambridge.org/api/v1/dictionaries/english-russian/entries/"
//                        + word);
//
//                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//                connection.setRequestProperty("accessKey", "jynnVbX5itf26vHuUafjnOqB1MWFAQr6luO25QmI30K14ZUVw6UM2A7WeIWqcPEX");
//                connection.setRequestProperty("Accept", "application/json");
//                connection.setRequestMethod("GET");
//                System.out.println(connection.getResponseCode());
//                if (connection.getResponseCode() == 200 && !MyFileWriter.readKeyFromMapFile().contains(word)) {
//                    StringBuilder rusWord = new StringBuilder();
//                    if (count < 5) {
//                        count++;
//                        try {
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                            String line;
//                            while ((line = reader.readLine()) != null) {
//                                String[] newLine = line.split(">");
//
//                                String[] russianLetters = {"а", "б", "в", "г", "д", "е", "ё", "ж",
//                                        "з", "и", "й", "к", "л", "м", "н", "о", "п", "р", "с", "т",
//                                        "у", "ф", "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "э", "ю", "я"};
//                                boolean flag = true;
//                                for (String russianLetter : russianLetters) {
//                                    String[] translateText = Arrays.stream(newLine).filter(a -> a.startsWith(russianLetter)).distinct().toArray(String[]::new);
//
//                                    for (String text : translateText) {
//                                        String[] filteredText = text.split("<");
//                                        String cleanText = filteredText[0];
//                                        cleanRusText = cleanText.trim();
//                                        if (flag) {
//                                            rusWord = new StringBuilder(cleanRusText);
//                                            flag = false;
//                                        } else {
//                                            rusWord.append("; ").append(cleanRusText);
//                                        }
//                                        System.out.println(word + " = " + cleanRusText + " = " + count);
//                                    }
//                                }
//                            }
//                            translator.put(word, rusWord.toString());
//                            myKeys.add(word);
//                            reader.close();
//
//                        } catch (FileNotFoundException e) {
//                            System.out.println(e.getMessage());
//                        }
//                    } else {
//                        break;
//                    }
//                } else {
//                    deleteWords.add(word);
//                }
//            }
//        } else {
//            getEngWord();
//            getTranslate();
//        }
//        MyFileWriter.writeToSetFile(MyFileWriter.deleteWordsFromArray(myEngWords, deleteWords));
//        MyFileWriter.writeToFile(translator);
//        MyFileWriter.writeToCurrentDayDictionary(translator);
//        MyFileWriter.writeCurrentDate();
//
//        for (Map.Entry<String, String> entry : translator.entrySet()) {
//            System.out.println(entry.getKey() + " -:- " + entry.getValue());
//        }
    }

    public void getTranslateForCurrentDate() throws IOException, SQLException {
        getTranslate();
        MyFileWriter.writeCurrentDate();
        MyFileWriter.readFromCurrentDayFile();
    }

    public String getTranslateForSearchWord(String word) throws IOException {
        String translatedText = "";
        String translateRequest = "q=" + word + "&target=ru&source=en";
        URL url = new URL("https://google-translate1.p.rapidapi.com/language/translate/v2");
        byte[] data = translateRequest.getBytes(StandardCharsets.UTF_8);

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("accept-encoding", "application/gzip");
        connection.setRequestProperty("x-rapidapi-key", "4");
        connection.setRequestProperty("x-rapidapi-host", "google-translate1.p.rapidapi.com");

        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        dos.write(data);
        dos.flush();
        dos.close();

        System.out.println(connection.getResponseCode());
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            System.out.println(response.toString());
            String translatedWord = response.toString();
            String[] arrayOfWords = translatedWord.split(":");
            translatedText = arrayOfWords[3];
            String delChars = "[$&+,:;=?@#|'<>.{^*()}%!-\\]\\[\"]";
            translatedText = translatedText.replaceAll(delChars, "");
            System.out.println(translatedText);

        } else {
            System.out.println("Post request is not working");
        }
        connection.disconnect();
        return translatedText;
    }

    public void saveMyTranslatedWordsToMyDictionary(String englishWord, String russianWord) throws IOException {
        int count = 0;
        List<String> dictionaryRusWords = new ArrayList<>();
        searchWords.put(englishWord, russianWord);
        ResultSet set = DataBasesCreator.getInstance().selectFromDictionary();
        try {
            while (set.next()) {
                String rusWord = set.getString(DataBasesCreator.RUSWORDSDICTIONARYCOLUMN);
                dictionaryRusWords.add(rusWord);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        for (String word : dictionaryRusWords) {
            if (word.equalsIgnoreCase(russianWord)) {
                count++;
                if(count > 0) {
                    break;
                }
            }
        }
        if (count == 0) {
            MyFileWriter.writeToFile(searchWords);
            try {
                DataBasesCreator.getInstance().insertSearchWordToDictionary(englishWord,russianWord,DataBasesCreator.DICTIONARYTABLE);
                System.out.println("The word is saved");
            }catch (SQLException e) {
             e.printStackTrace();
            }
        }
        else {
            System.out.println("This word already exists in dictionary");
            searchWords.clear();
            dictionaryRusWords.clear();
        }
        MyFileWriter.writeToCurrentDayDictionary(searchWords);
        try {
            DataBasesCreator.getInstance().insertSearchWordToDictionary(englishWord,russianWord,DataBasesCreator.CURRENTDAYDICTIONARYTABLE);
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
