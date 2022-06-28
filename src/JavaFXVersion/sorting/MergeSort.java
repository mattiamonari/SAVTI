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

public class MergeSort implements SortAlgorithm {

    private final UserSettings userSettings;
    Thread thread;
    int countComparison = 0, countSwaps = 0, imageIndex = 1;
    boolean running = true;
    private ProgressBar progressBar;
    private double progress = 0;
    private double increment;
    int delay = 1;
    int width = 1;
    int height = 1;

    public MergeSort(UserSettings userSettings) {
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
        countComparison = 0;
        width = (int) (array[0].getImage().getWidth() % 2 == 0 ? array[0].getImage().getWidth() :
                array[0].getImage().getWidth() - 1);
        height = (int) (array[0].getImage().getHeight() % 2 == 0 ? array[0].getImage().getHeight() :
                array[0].getImage().getHeight() - 1);

        thread = new Thread(() -> {

            mergeSort(array, 0, array.length - 1, true);
            writeImage(userSettings, array, width, height, imageIndex, countComparison, countSwaps);
            FFMPEG prc = new FFMPEG(userSettings, progressBar);
            if (userSettings.saveImage == false)
                deleteAllPreviousFiles(userSettings);

            if (userSettings.isOpenFile()) {
                File out = new File(userSettings.getOutputDirectory() + "\\" + userSettings.getOutName());
                try {
                    Desktop.getDesktop().open(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(() -> {
                gridPane.setVisible(true);
                ((BorderPane) gridPane.getParent()).getChildren().remove(progressBar);
            });

        });

        thread.start();


    }

    private void setupEnv(GridPane gridPane) {
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

    private void calculateNumberOfSwaps(Tail[] array) {
        Tail[] tmp = new Tail[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        mergeSort(tmp, 0, array.length - 1, false);
    }

    void mergeSort(Tail[] arr, int l, int r, boolean write) {
        ++countComparison;
        if (l < r && running) {

            // m is the point where the array is divided into two subarrays
            int m = (l + r) / 2;

            mergeSort(arr, l, m, write);
            mergeSort(arr, m + 1, r, write);

            // Merge the sorted subarrays
            merge(arr, l, m, r, write);
        }
    }

    void merge(Tail[] arr, int p, int q, int r, boolean write) {

        if (running) {
            // Create L ← A[p..q] and M ← A[q+1..r]
            int n1 = q - p + 1;
            int n2 = r - q;

            Tail[] L = new Tail[n1];
            Tail[] M = new Tail[n2];

            System.arraycopy(arr, p, L, 0, n1);
            for (int j = 0; j < n2; j++)
                M[j] = arr[q + 1 + j];

            // Maintain current index of sub-arrays and main array
            int i, j, k;
            i = 0;
            j = 0;
            k = p;

            // Until we reach either end of either L or M, pick larger among
            // elements L and M and place them in the correct position at A[p..r]
            while (i < n1 && j < n2) {
                ++countComparison;
                if (SortUtils.greater(M[j], L[i])) {
                    ++countSwaps;
                    arr[k] = L[i];
                    i++;
                    if ((countSwaps % delay) == 0 && write) {
                        writeImage(userSettings, arr, width, height, imageIndex++, countComparison, countSwaps);
                    }
                    if (write)
                        progressBar.setProgress(progress += increment);

                } else {
                    ++countSwaps;
                    arr[k] = M[j];
                    j++;
                    if ((countSwaps % delay) == 0 && write) {
                        writeImage(userSettings, arr, width, height, imageIndex++, countComparison, countSwaps);
                    }
                }
                k++;
            }

            // When we run out of elements in either L or M,
            // pick up the remaining elements and put in A[p..r]
            while (i < n1) {
                arr[k] = L[i];
                i++;
                k++;
                if ((countSwaps % delay) == 0 && write) {
                    writeImage(userSettings, arr, width, height, imageIndex++, countComparison, countSwaps);
                }
            }

            while (j < n2) {
                arr[k] = M[j];
                j++;
                k++;
                if ((countSwaps % delay) == 0 && write) {
                    writeImage(userSettings, arr, width, height, imageIndex++, countComparison, countSwaps);
                }
            }
        }
    }

    @Override
    public <T extends Comparable<T>> T[] sort(T[] unsorted) {
        return null;
    }
}
