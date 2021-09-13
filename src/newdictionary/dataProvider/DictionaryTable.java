package newdictionary.dataProvider;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class DictionaryTable {

    private SimpleIntegerProperty id;
    private SimpleStringProperty engWord;
    private SimpleStringProperty rusWord;


    public DictionaryTable(int id, String engWord, String rusWord) {
        this.id = new SimpleIntegerProperty(id);
        this.engWord = new SimpleStringProperty(engWord);
        this.rusWord = new SimpleStringProperty(rusWord);
    }

    public int getId() {
        return id.get();
    }

    public String getEngWord() {
        return engWord.get();
    }

    public String getRusWord() {
        return rusWord.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setEngWord(String engWord) {
        this.engWord.set(engWord);
    }

    public void setRusWord(String rusWord) {
        this.rusWord.set(rusWord);
    }
}
