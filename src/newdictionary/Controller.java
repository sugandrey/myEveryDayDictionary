package newdictionary;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import newdictionary.dataProvider.EngWordsForCurrentDate;
import newdictionary.dataProvider.MyFileWriter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller {

    @FXML
    private ListView<String> englishField;
    @FXML
    private TextArea russianField;
    @FXML
    private Button saveButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextField searchEnglishWord;
    @FXML
    private TextField searchRussianWord;
    @FXML
    private Label warningLabel;

    private ObservableList<String> englishWordsOfDay;
    private ObservableList<String> russianWordsOfDay;

    EngWordsForCurrentDate wordsForCurrentDate;
    @FXML
    private ProgressBar progressBar;


    @FXML
    public void initialize() throws IOException, SQLException {
        wordsForCurrentDate = new EngWordsForCurrentDate();
        searchButton.setDisable(true);
        clearButton.setDisable(true);
        saveButton.setDisable(true);
        searchRussianWord.setEditable(false);
        setWarningLabelForEmpty();
        englishWordsOfDay = FXCollections.observableArrayList();
        russianWordsOfDay = FXCollections.observableArrayList();
        if(!LocalDate.now().toString().equals(MyFileWriter.readDateFromCurrentDateFile())) {
            wordsForCurrentDate.getTranslateForCurrentDate();

            for (Map.Entry<String, String> entry : MyFileWriter.getReadFromCurrentDayFile().entrySet()) {
                englishWordsOfDay.add(entry.getKey());
                russianWordsOfDay.add(entry.getValue());
                englishField.setItems(englishWordsOfDay);
                englishField.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                englishField.getSelectionModel().selectFirst();
                handleClickListView();
            }
        }
        else {
            for (Map.Entry<String, String> entry : MyFileWriter.getReadFromCurrentDayFile().entrySet()) {
                englishWordsOfDay.add(entry.getKey());
                russianWordsOfDay.add(entry.getValue());
            }
            englishField.setItems(englishWordsOfDay);
            englishField.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            englishField.getSelectionModel().selectFirst();
            handleClickListView();
        }
    }

    @FXML
    public void stopRun() {
        Platform.exit();
    }

    @FXML
    public void getTranslatedSearchWord() throws IOException {
        StringBuilder fullText = new StringBuilder();
        String text = searchEnglishWord.getText();
        String lowCaseText = text.toLowerCase();
        String rusWord = wordsForCurrentDate.getTranslateForSearchWord(fullText.append(lowCaseText).toString());
        searchRussianWord.setText(rusWord);
//        englishWordsOfDay.add(text);
//        russianWordsOfDay.add(rusWord);

        if(!searchRussianWord.getText().isEmpty()) {
            saveButton.setDisable(false);
        }
    }

    @FXML
    public void getDisableButtons() {
        String word = searchEnglishWord.getText();
        List<Integer> intArray = new ArrayList<>();
        char[] letters = word.toCharArray();
        Pattern pattern = Pattern.compile("[$&+,:;=?@#|'<>.{^*()}%!-\\]\\[\"]");
        Matcher matcher = pattern.matcher(word);
        boolean doesWordContainDigitsOrSpecialCharacters = matcher.find();
        for (char myChar : letters) {
            int value = Character.getType(myChar);
            if(value == 1) {
                intArray.add(value);
            }
        }
        if (word.isEmpty() || word.trim().isEmpty()) {
            searchButton.setDisable(true);
            clearButton.setDisable(true);
            saveButton.setDisable(true);
            searchRussianWord.clear();
            setWarningLabelForEmpty();
        } else if (doesWordContainDigitsOrSpecialCharacters && intArray.isEmpty()) {
            searchButton.setDisable(true);
            clearButton.setDisable(true);
            saveButton.setDisable(true);
            setWarningLabelForDigits();
        }
        else {
            searchButton.setDisable(false);
            clearButton.setDisable(false);
            warningLabel.setText("");
        }
    }

    @FXML
    public void getLabel() {
        Pattern pattern = Pattern.compile("[^a-zA-Z]");
        Matcher matcher = pattern.matcher(searchEnglishWord.getText());
        boolean doesWordContainDigitsOrSpecialCharacters = matcher.find();
        Pattern pattern1 = Pattern.compile("\\s");
        Matcher matcher1 = pattern1.matcher(searchEnglishWord.getText());
        boolean doesWordContainsSpace = matcher1.find();
        if (searchEnglishWord.getText().isEmpty() || searchEnglishWord.getText().trim().isEmpty()) {
            setWarningLabelForEmpty();
            searchButton.setDisable(true);
            clearButton.setDisable(true);
            saveButton.setDisable(true);
            searchRussianWord.clear();
        }
        if (doesWordContainDigitsOrSpecialCharacters) {
            setWarningLabelForDigits();
            searchButton.setDisable(true);
            clearButton.setDisable(true);
            saveButton.setDisable(true);
        }
        if (doesWordContainsSpace) {
            searchButton.setDisable(false);
            clearButton.setDisable(false);
            warningLabel.setText("");

        }
    }

    @FXML
    public void setWarningLabelForEmpty() {
        warningLabel.setText("The text field is empty. Enter a phrase for the translation please!");
    }

    @FXML
    public void setWarningLabelForDigits() {
        warningLabel.setText("The text field contains digits or special characters. Enter just letters please!");
    }


    @FXML
    public void clearTextButton() {
        englishField.getItems().clear();
        russianField.clear();
        searchEnglishWord.clear();
        searchRussianWord.clear();
        saveButton.setDisable(true);
        searchButton.setDisable(true);
        clearButton.setDisable(true);
    }

    @FXML
    public void handleClickListView() throws IOException {

        russianField.wrapTextProperty().setValue(true);
        russianField.setEditable(false);
        for (String english : MyFileWriter.getReadFromCurrentDayFile().keySet()) {
            if (englishField.getSelectionModel().getSelectedItem().equals(english)) {
                String value = MyFileWriter.getReadFromCurrentDayFile().get(english);
                russianField.setText(value);
            }
        }
    }
    @FXML
    public void saveSearchWordsToFile() throws IOException {
        wordsForCurrentDate.saveMyTranslatedWordsToMyDictionary(searchEnglishWord.getText(),searchRussianWord.getText());
        englishField.getItems().clear();
        russianField.clear();
        for (Map.Entry<String, String> entry : MyFileWriter.getReadFromCurrentDayFile().entrySet()) {
            englishWordsOfDay.add(entry.getKey());
            russianWordsOfDay.add(entry.getValue());
            englishField.setItems(englishWordsOfDay);
            englishField.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            englishField.getSelectionModel().select(searchEnglishWord.getText());
            handleClickListView();
        }
        saveButton.setDisable(true);
    }
}

