package JavaFXVersion;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

//TODO!!!!!!! Sistemare un po' il codice in generale, commentare in maniera più chiara e migliorare la GUI

public class EntryPoint extends Application {


    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("sortingAlgorithmsVisualization");
        MainWindow mw = new MainWindow(stage);
        Scene scene = new Scene(mw, 2000,1000);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }



}
