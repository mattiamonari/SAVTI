package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import static JavaFXVersion.utilities.ImageUtilities.fillImage;

abstract public class AbstractSort implements SortAlgorithm{
    final UserSettings userSettings;
    Thread thread;
    int countComparison = 0, countSwaps = 0, imageIndex = 1, delay = 1;
    boolean running = true;
    ProgressBar progressBar;
    double progress = 0;
    double increment;
    int width;
    int height;

    public AbstractSort(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    @Override
    public void killTask() {
        running = false;
    }

    public boolean isThreadAlive() {
        return running;
    }

    void setupEnv(GridPane gridPane) {
        progressBar = new ProgressBar(0);
        increment = 1d / countSwaps;
        ((VBox) gridPane.getParent()).getChildren().add(progressBar);
        gridPane.setVisible(false);
        gridPane.setManaged(false);
        progressBar.setPrefWidth(gridPane.getWidth());
        progressBar.setMinWidth(gridPane.getWidth());
        progressBar.setPrefHeight(50);
        progressBar.setMinHeight(50);
        VBox.setMargin(progressBar, new Insets(10));

        delay = countSwaps / (userSettings.getFrameRate() * userSettings.getVideoDuration()) + 1;
    }

    void resumeProgram(GridPane gridPane, MainWindow mainWindow, Tile[] array){
        gridPane.setVisible(true);
        gridPane.setManaged(true);
        ((VBox) gridPane.getParent()).getChildren().remove(progressBar);
        fillImage(userSettings.getChunkWidth(),userSettings.getChunkHeight(),userSettings.getPrecision(), userSettings.getPrecision(),array,gridPane);
        mainWindow.enableAll();

    }

}
