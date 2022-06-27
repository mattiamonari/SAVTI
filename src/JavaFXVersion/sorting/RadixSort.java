package JavaFXVersion.sorting;

import JavaFXVersion.FFMPEG;
import JavaFXVersion.Tail;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static JavaFXVersion.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.FileUtilities.writeImage;

public class RadixSort implements SortAlgorithm {

    private final UserSettings userSettings;
    Thread thread;
    int countComparison = 0, countSwaps = 0, imageIndex = 1;
    boolean running = true;
    private ProgressBar progressBar;
    private double progress = 0;
    private double increment;
    boolean write;

    public RadixSort(UserSettings userSettings) {
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

        int delay = countSwaps / (userSettings.getFrameRate() * 15) + 1;
        countSwaps = 0;
        countComparison = 0;
        write = true;
        int width = (int) (array[0].getImage().getWidth() % 2 == 0 ? array[0].getImage().getWidth() :
                array[0].getImage().getWidth() - 1);
        int height = (int) (array[0].getImage().getHeight() % 2 == 0 ? array[0].getImage().getHeight() :
                array[0].getImage().getHeight() - 1);

        thread = new Thread(() -> {
        // Get maximum element
        int size = array.length;
        Tail max = getMax(array, size);

        // Apply counting sort to sort elements based on place value.
        for (int place = 1; max.position / place > 0; place *= 10) {
            if(running == false)
                break;
            countingSort(array, size, place, delay, width, height, write);
        }
        writeImage(userSettings, array, width, height, imageIndex, countComparison, countSwaps);
        //?MAYBE JUST PASS userSettings?
        FFMPEG prc = new FFMPEG(userSettings.getFfmpegPath(), userSettings.getOutName(),
                userSettings.getOutputDirectory(),
                userSettings.getFrameRate(), userSettings.getMusic());
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

    Tail getMax(Tail array[], int n) {
        Tail max = array[0] ;
        for (int i = 1; i < n; i++) {
            ++countComparison;
            if (SortUtils.greater(array[i], max))
                max = array[i];
        }
        return max;
    }

    void countingSort(Tail array[], int size, int place, int delay, int width, int height, boolean write) {
        Tail[] output = new Tail[size + 1];
        Tail max = array[0];
        if (running == false)
            return;
        for (int i = 1; i < size; i++) {
            ++countComparison;
            if (SortUtils.greater(array[i], max))
                max = array[i];
        }
        int[] count = new int[max.position + 1];

        for (int i = 0; i < max.position; ++i)
            count[i] = 0;

        // Calculate count of elements
        for (int i = 0; i < size; i++)
            count[(array[i].position / place) % 10]++;

        // Calculate cumulative count
        for (int i = 1; i < 10; i++)
            count[i] += count[i - 1];

        // Place the elements in sorted order
        for (int i = size - 1; i >= 0; i--) {
            output[count[(array[i].position / place) % 10] - 1] = array[i];
            count[(array[i].position / place) % 10]--;
        }

        for (int i = 0; i < size; i++) {
            ++countSwaps;
            array[i] = output[i];
            if((countSwaps % delay) == 0 && (write == true))
                writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
            //progressBar.setProgress(progress+=increment);

        }
    }


    private void calculateNumberOfSwaps(Tail[] array){
        write = false;
        int size = array.length;
        Tail max = getMax(array, size);
        Tail[] tmp = new Tail[size];
        System.arraycopy(array,0,tmp, 0, size);

        // Apply counting sort to sort elements based on place value.
        for (int place = 1; max.position / place > 0; place *= 10)
            countingSort(array, size, place, 1,0,0, write);
    }

    @Override
    public <T extends Comparable<T>> T[] sort(T[] unsorted) {
        return null;
    }
}
