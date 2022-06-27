package JavaFXVersion.sorting;

import JavaFXVersion.FFMPEG;
import JavaFXVersion.MainWindow;
import JavaFXVersion.Tail;
import JavaFXVersion.UserSettings;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.FutureTask;

import static JavaFXVersion.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.FileUtilities.writeImage;

public class SelectionSort implements SortAlgorithm {

    private final UserSettings userSettings;
    Thread thread;
    int countComparison = 0, countSwaps = 0, imageIndex = 0,delay = 1;
    boolean running = true;
    private ProgressBar progressBar;
    private double progress = 0;
    private double increment;

    public SelectionSort(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    @Override
    public void killTask() {
        running = false;
    }

    @Override
    public boolean isThreadAlive() {
        return running;
    }

    @Override
    public void sort(Tail[] array, GridPane gridPane) {
        running = true;
        deleteAllPreviousFiles(userSettings);
        calculateNumberOfSwaps(array);
        setupEnv(gridPane);
        countSwaps = 0;
        int width = (int) (array[0].getImage().getWidth() % 2 == 0 ? array[0].getImage().getWidth() :
                array[0].getImage().getWidth() - 1);
        int height = (int) (array[0].getImage().getHeight() % 2 == 0 ? array[0].getImage().getHeight() :
                array[0].getImage().getHeight() - 1);
        thread = new Thread(() -> {
            int size = array.length;
            for (int step = 0; step < size - 1; step++) {
                int min_idx = step;
                for (int k = step + 1; k < size; k++) {
                        if(!running)
                            break;
                        // To sort in descending order, change > to < in this line.
                        // Select the minimum element in each loop.
                        ++countComparison;
                        if (SortUtils.greater(array[min_idx], array[k])) {
                            min_idx = k;
                        }
                }

                // put min at the correct position
                SortUtils.swap(array, step, min_idx);
                ++countSwaps;
                progressBar.setProgress(progress += increment);
                if((countSwaps % delay) == 0){
                    writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
                }
                if (running == false)
                    break;
            }
            writeImage(userSettings, array, width, height, imageIndex, countComparison, countSwaps);
            FFMPEG prc = new FFMPEG(userSettings, progressBar);
            deleteAllPreviousFiles(userSettings);

            if(userSettings.isOpenFile()) {
                File out = new File(userSettings.getOutputDirectory() + "\\" + userSettings.getOutName());
                try {
                    Desktop.getDesktop().open(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(() -> {
                gridPane.setVisible(true);
                ((BorderPane)gridPane.getParent()).getChildren().remove(progressBar);
            });

        });
        thread.start();
    }

    private void setupEnv(GridPane gridPane) {
        progressBar = new ProgressBar(0);
        increment = 1d / countSwaps;
        ((BorderPane)gridPane.getParent()).setBottom(progressBar);
        gridPane.setVisible(false);
        progressBar.setPrefWidth(gridPane.getWidth());
        progressBar.setMinWidth(gridPane.getWidth());
        progressBar.setPrefHeight(50);
        progressBar.setMinHeight(50);
        BorderPane.setAlignment(progressBar, Pos.CENTER);
        BorderPane.setMargin(progressBar, new Insets(0,0,10,0));

        delay = countSwaps / (userSettings.getFrameRate() * 15) + 1;
    }

    private void calculateNumberOfSwaps(Tail[] array) {
        Tail[] tmp = new Tail[array.length];
        System.arraycopy(array,0,tmp, 0, array.length);

        int size = tmp.length;
        for (int step = 0; step < size - 1; step++) {
            int min_idx = step;
            for (int k = step + 1; k < size; k++) {
                if (SortUtils.greater(tmp[min_idx], tmp[k])) {
                    min_idx = k;
                }
            }

            // put min at the correct position
            SortUtils.swap(tmp, step, min_idx);
            ++countSwaps;
        }
    }

    @Override
    public <T extends Comparable<T>> T[] sort(T[] unsorted) {
        return null;
    }
}
