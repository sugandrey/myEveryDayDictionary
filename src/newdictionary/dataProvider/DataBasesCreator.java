package newdictionary.dataProvider;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DataBasesCreator {
    public static final String ENGLISHWORDSTABLE = "EnglishWords";
    public static final String CLEANENGWORDSTABLE = "CleanEnglishWords";
    public static final String DICTIONARYTABLE = "Dictionary";
    public static final String CURRENTDAYDICTIONARYTABLE = "DictionaryForCurrentDay";
    public static final String CURRENTDATETABLE = "CurrentDay";
    public static final String IDENGLISHWORDCOLUMN = "id_EnglishWord";
    public static final String ENGLISHWORDCOLUMN = "englishWord";
    public static final String IDCLEANENGWORDCOLUMN = "id_CleanEnglishWord";
    public static final String CLEANENGWORDCOLUMN = "cleanEnglishWord";
    public static final String ENGWORDSDICTIONARYCOLUMN = "englishWords";
    public static final String RUSWORDSDICTIONARYCOLUMN = "russianWords";
    public static final String ENGWORDSFORDAYCOLUMN = "englishWordsForDay";
    public static final String RUSWORDSFORDAYCOLUMN = "russianWordsForDay";
    public static final String CURRENTDAYCOLUMN = "currentDate";
    public static final String pathToEngWordsFile = "DataBases\\englishWords.txt";
    public static final String pathToCleanEngWords = "DataBases\\cleanEnglishWords.txt";
    private static final String pathToDatabase = System.getProperty("user.dir") + "\\DataBases\\wordDictionary.db";
    public static final String CONNECTION = "jdbc:sqlite:" + pathToDatabase;
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static PreparedStatement preparedStatementSearch;
    private static DataBasesCreator instance = new DataBasesCreator();

    private DataBasesCreator() {

    }
    public static DataBasesCreator getInstance() {
        return instance;
    }




    public Set<String> getDataForEnglishWordTable() throws IOException {
        Set<String> words = new HashSet<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToEngWordsFile));
        String word;
        while ((word = bufferedReader.readLine()) != null) {
            words.add(word);
        }
        return words;
    }

    public Set<String> getDataForCleanEnglishTable() throws IOException {
        Set<String> words = new HashSet<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToCleanEngWords));
        String word;
        while ((word = bufferedReader.readLine()) != null) {
            words.add(word);
        }
        return words;
    }

//    public static void main(String[] args) {

//        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path);
//            Statement statement = connection.createStatement()) {
//            statement.execute("CREATE TABLE EnglishWords (englishWord TEXT)");
//            statement.execute("CREATE TABLE CleanEnglishWords (cleanEnglishWord TEXT)");
//            statement.execute("CREATE TABLE Dictionary (englishWords TEXT, russianWords TEXT)");
//            statement.execute("CREATE TABLE DictionaryForCurrentDay (englishWordsForDay TEXT, russianWordsForDay TEXT)");
//            statement.execute("CREATE TABLE CurrentDay (currentDate TEXT)");

//            if try with resources then no need to close statement and connection because it done that automatically

//        try {
//            connection = DriverManager.getConnection(CONNECTION);
//            Statement statement = connection.createStatement();
//            statement.execute("DROP TABLE IF EXISTS " + ENGLISHWORDSTABLE);
//            statement.execute("DROP TABLE IF EXISTS " + CLEANENGWORDSTABLE);
//            statement.execute("DROP TABLE IF EXISTS " + DICTIONARYTABLE);
//            statement.execute("DROP TABLE IF EXISTS " + CURRENTDAYDICTIONARYTABLE);
//            statement.execute("DROP TABLE IF EXISTS " + CURRENTDATETABLE);
//            statement.execute("CREATE TABLE IF NOT EXISTS " + ENGLISHWORDSTABLE + " " +
//                    "(" + IDENGLISHWORDCOLUMN + " NOT NULL PRIMARY KEY, " + ENGLISHWORDCOLUMN + " TEXT)");
//            statement.execute("CREATE TABLE IF NOT EXISTS " + CLEANENGWORDSTABLE +
//                    "(" + IDCLEANENGWORDCOLUMN + " integer NOT NULL PRIMARY KEY, " + CLEANENGWORDCOLUMN + " TEXT)");
//            statement.execute("CREATE TABLE IF NOT EXISTS " + DICTIONARYTABLE +
//                    "(" + IDCLEANENGWORDCOLUMN + " integer, " + ENGWORDSDICTIONARYCOLUMN + " TEXT, " +
//                    RUSWORDSDICTIONARYCOLUMN + " TEXT, FOREIGN KEY (" + IDCLEANENGWORDCOLUMN + ")" +
//                    "REFERENCES " + CLEANENGWORDSTABLE + " (" + IDCLEANENGWORDCOLUMN + "))");
//            statement.execute("CREATE TABLE IF NOT EXISTS " + CURRENTDATETABLE + " (" + CURRENTDAYCOLUMN + " TEXT)");
//            statement.execute("CREATE TABLE IF NOT EXISTS " + CURRENTDAYDICTIONARYTABLE +
//                    "(" + IDCLEANENGWORDCOLUMN + " integer, " + ENGWORDSFORDAYCOLUMN + " TEXT, " +
//                    RUSWORDSFORDAYCOLUMN + " TEXT, " +
//                    "FOREIGN KEY (" + IDCLEANENGWORDCOLUMN + ")" +
//                    " REFERENCES " + CLEANENGWORDSTABLE + " " + " (" + IDCLEANENGWORDCOLUMN + "))");
//            ResultSet set = selectFromCleanEngWords();
//            while (set.next()) {
//                System.out.println(set.getString(CLEANENGWORDCOLUMN));
//            }
//            set.close();
//            List<String> array = getSingleLetters();
//            for (String arr : array) {
//                ResultSet resultSet = statement.executeQuery("SELECT " + CLEANENGWORDCOLUMN + " FROM " + CLEANENGWORDSTABLE +
//                        " WHERE " + CLEANENGWORDCOLUMN + " LIKE " + "'" + arr + "'");
//                while (resultSet.next()) {
//                    System.out.println(resultSet.getString(CLEANENGWORDCOLUMN));
//                }
//                resultSet.close();
//            }

//            statement.close();
//            connection.close();


//                insertToEnglishWordsTable();
//                insertEnglishCleanWordsToDatabase();

//        } catch (SQLException e) {
//            System.out.println("Something was wrong" + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    public void insertToEnglishWordsTable() throws IOException {

        String sql = "INSERT INTO " + ENGLISHWORDSTABLE + " (" + IDENGLISHWORDCOLUMN + ", " + ENGLISHWORDCOLUMN + ") " +
                "VALUES(?,?)";
        List<String> words = new ArrayList<>(getDataForEnglishWordTable());

        int count = 0;
        try {
            connection = DriverManager.getConnection(CONNECTION);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (String word : words) {
                count++;
                preparedStatement.setInt(1, count);
                preparedStatement.setString(2, word);
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                System.out.println(count);
            }
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertEnglishCleanWordsToDatabase() throws IOException {

        String sql = "INSERT INTO " + CLEANENGWORDSTABLE + " (" + IDCLEANENGWORDCOLUMN + ", " + CLEANENGWORDCOLUMN + ") " +
                "VALUES(?,?)";
        Set<String> words = new HashSet<>(getDataForCleanEnglishTable());
        Iterator<String> iterator = words.iterator();
        int count = 0;
        try {
            connection = DriverManager.getConnection(CONNECTION);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            while (iterator.hasNext()) {
                String word = iterator.next();
                count++;
                preparedStatement.setInt(1, count);
                preparedStatement.setString(2, word);
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                System.out.println(count);
            }
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertToDictionaryTable() throws IOException {

        String sql = "INSERT INTO " + CLEANENGWORDSTABLE + "(" + IDCLEANENGWORDCOLUMN +", " + CLEANENGWORDCOLUMN + ") " +
                "VALUES(?,?)";
        Set<String> words = new HashSet<>(getDataForCleanEnglishTable());
        Iterator<String> iterator = words.iterator();
        int count = 0;
        try {
            connection = DriverManager.getConnection(CONNECTION);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            while (iterator.hasNext()) {
                String word = iterator.next();
                preparedStatement.setString(1, word);
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                System.out.println(count++);
            }
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(count);
    }

    public List<String> getSingleLetters() {
        String letters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        char[] charArr = letters.toCharArray();
        String[] myArr = new String[charArr.length];
        for (int i = 0; i < charArr.length; i++) {
            myArr[i] = String.valueOf(charArr[i]);
        }
        return Arrays.stream(myArr).collect(Collectors.toList());
    }

    public ResultSet selectFromCleanEngWords() throws SQLException {

        String sql = "SELECT " + IDCLEANENGWORDCOLUMN + ", " + CLEANENGWORDCOLUMN + " FROM " + CLEANENGWORDSTABLE
                + " LIMIT " + 100;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(CONNECTION);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public ResultSet selectFromDictionary() {

        String sql = "SELECT " + ENGWORDSDICTIONARYCOLUMN + ", " + RUSWORDSDICTIONARYCOLUMN + " FROM " + DICTIONARYTABLE;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(CONNECTION);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public void insertToDictionary(int id, String englishWord, String russianWord, String tableName) throws SQLException {
        String sqlDictionary = "INSERT INTO " + DICTIONARYTABLE + " (" + IDCLEANENGWORDCOLUMN +
                ", " + ENGWORDSDICTIONARYCOLUMN + ", " + RUSWORDSDICTIONARYCOLUMN + ")" +
                " VALUES(" + id + ", '"+ englishWord + "', '" + russianWord + "')";
        String sqlCurrentDayDictionary = "INSERT INTO " + CURRENTDAYDICTIONARYTABLE + " (" + IDCLEANENGWORDCOLUMN +
                ", " + ENGWORDSFORDAYCOLUMN + ", " + RUSWORDSFORDAYCOLUMN + ")" +
                " VALUES(" + id + ", '"+ englishWord + "', '" + russianWord + "')";
            connection = DriverManager.getConnection(CONNECTION);


        if(tableName.equals(CURRENTDAYDICTIONARYTABLE)) {
            preparedStatement = connection.prepareStatement(sqlCurrentDayDictionary);
            preparedStatement.execute();
        }
        else {
            preparedStatement = connection.prepareStatement(sqlDictionary);
            preparedStatement.execute();
        }
        preparedStatement.close();
        connection.close();
    }
    public void deleteFromCurrentDayDictionary() {

        String deleteWords = "DELETE FROM " + CURRENTDAYDICTIONARYTABLE;
        try {
            connection = DriverManager.getConnection(CONNECTION);
            Statement statement = connection.createStatement();
            statement.execute(deleteWords);
            statement.close();
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertDateToDatabase() {
        String date = LocalDate.now().toString();
        String insSql = "INSERT INTO " + CURRENTDATETABLE + " (" + CURRENTDAYCOLUMN + ") VALUES('" + date + "')";
        String delSql = "DELETE FROM " + CURRENTDATETABLE;
        try {
            connection = DriverManager.getConnection(CONNECTION);
            Statement statement = connection.createStatement();
            statement.execute(delSql);
            statement.execute(insSql);
            statement.close();
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteWordsFromCleanEnglishWords(List<String> words) {

      for (String word : words) {
          String sql = "DELETE FROM " + CLEANENGWORDSTABLE + " WHERE " + CLEANENGWORDCOLUMN + "='" + word + "'";
          try {
              connection = DriverManager.getConnection(CONNECTION);
              Statement statement = connection.createStatement();
              statement.execute(sql);
              statement.close();
              connection.close();

          }catch (SQLException e) {
              e.printStackTrace();
          }

      }

    }
    public void insertSearchWordToDictionary(String englishWord, String russianWord, String tableName) throws SQLException {
        String sqlDictionary = "INSERT INTO " + DICTIONARYTABLE + " (" +
                ENGWORDSDICTIONARYCOLUMN + ", " + RUSWORDSDICTIONARYCOLUMN + ")" +
                " VALUES('" + englishWord + "', '" + russianWord + "')";
        String sqlCurrentDayDictionary = "INSERT INTO " + CURRENTDAYDICTIONARYTABLE + " ("+ ENGWORDSFORDAYCOLUMN + ", " + RUSWORDSFORDAYCOLUMN + ")" +
                " VALUES('" + englishWord + "', '" + russianWord + "')";
        connection = DriverManager.getConnection(CONNECTION);
        Statement statement = connection.createStatement();

        if(tableName.equals(CURRENTDAYDICTIONARYTABLE)) {

            statement.execute(sqlCurrentDayDictionary);
        }
        else {
            statement.execute(sqlDictionary);
        }
        statement.close();
        connection.close();
    }
}
