package JavaFXVersion.sorting;

import JavaFXVersion.UserSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.List;

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
        ((BorderPane) gridPane.getParent()).setBottom(progressBar);
        gridPane.setVisible(false);
        progressBar.setPrefWidth(gridPane.getWidth());
        progressBar.setMinWidth(gridPane.getWidth());
        progressBar.setPrefHeight(50);
        progressBar.setMinHeight(50);
        BorderPane.setAlignment(progressBar, Pos.CENTER);
        BorderPane.setMargin(progressBar, new Insets(0, 0, 10, 0));

        delay = countSwaps / (userSettings.getFrameRate() * userSettings.getVideoDuration()) + 1;
    }

    @Override
    public <T extends Comparable<T>> T[] sort(T[] unsorted) {
        return null;
    }

    @Override
    public <T extends Comparable<T>> List<T> sort(List<T> unsorted) {
        return SortAlgorithm.super.sort(unsorted);
    }

}
