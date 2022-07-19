package JavaFXVersion.sorting;

import JavaFXVersion.FFMPEG;
import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static JavaFXVersion.utilities.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.utilities.FileUtilities.writeImage;

public class MergeSort extends AbstractSort implements SortAlgorithm {

    public MergeSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(Tile[] array, GridPane gridPane, MainWindow mainWindow) {

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
            if (!userSettings.saveImage)
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
                resumeProgram(gridPane, mainWindow, array);
            });

        });

        thread.start();


    }

    private void calculateNumberOfSwaps(Tile[] array) {
        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        mergeSort(tmp, 0, array.length - 1, false);
    }

    void mergeSort(Tile[] arr, int l, int r, boolean write) {
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

    void merge(Tile[] arr, int p, int q, int r, boolean write) {

        if (running) {
            // Create L ← A[p..q] and M ← A[q+1..r]
            int n1 = q - p + 1;
            int n2 = r - q;

            Tile[] L = new Tile[n1];
            Tile[] M = new Tile[n2];

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
}
