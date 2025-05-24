package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
        ScrollPane scrollPane = loader.load();
        Scene scene = new Scene(scrollPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        stage.setScene(scene);
        stage.setTitle("Workshop");
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
