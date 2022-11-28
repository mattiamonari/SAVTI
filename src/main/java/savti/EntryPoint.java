package savti;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class EntryPoint extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("sortingAlgorithmsVisualization");
        stage.setMaximized(true);
        MainWindow mw = new MainWindow(stage);
        Scene scene = new Scene(mw);
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        stage.setScene(scene);
        stage.show();
    }
}
