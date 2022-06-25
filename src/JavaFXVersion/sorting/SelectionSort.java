package JavaFXVersion.sorting;

import JavaFXVersion.FFMPEG;
import JavaFXVersion.MainWindow;
import JavaFXVersion.Tail;
import JavaFXVersion.UserSettings;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
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
    int countComparison = 0, countSwaps = 0, imageIndex = 0;
    boolean running = true;

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
        int delay = countSwaps / (userSettings.getFrameRate() * 15) + 1;
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
                        if(running == false)
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
                if((countSwaps % delay) == 0){
                    writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
                }
                if (running == false)
                    break;
            }
            writeImage(userSettings, array, width, height, imageIndex, countComparison, countSwaps);

            FFMPEG prc = new FFMPEG(userSettings.getFfmpegPath(), userSettings.getOutName(),
                    userSettings.getOutputDirectory(),
                    userSettings.getFrameRate(), userSettings.getMusic());
            deleteAllPreviousFiles(userSettings);
        });
        thread.start();
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
