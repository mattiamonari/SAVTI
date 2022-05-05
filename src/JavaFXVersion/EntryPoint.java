package JavaFXVersion;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
//TODO!!!!!!! Sistemare un po' il codice in generale, commentare in maniera più chiara e migliorare la GUI

public class EntryPoint extends Application {


    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage stage) throws Exception {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        stage.setTitle("sortingAlgorithmsVisualization");
        MainWindow mw = new MainWindow(stage);
        Scene scene = new Scene(mw, screenSize.getWidth(), screenSize.getHeight());
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }



}
