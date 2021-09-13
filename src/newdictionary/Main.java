package newdictionary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import newdictionary.dataProvider.MyFileWriter;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dictionary.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.initialize();
        primaryStage.setTitle("My Every Day Dictionary");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }

}
