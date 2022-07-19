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

import static JavaFXVersion.sorting.SortUtils.less;
import static JavaFXVersion.sorting.SortUtils.swap;
import static JavaFXVersion.utilities.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.utilities.FileUtilities.writeImage;

public class QuickSort extends AbstractSort implements SortAlgorithm {

    public QuickSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(Tile[] array, GridPane gridPane, MainWindow mainWindow) {
        running = true;
        deleteAllPreviousFiles(userSettings);
        calculateNumberOfSwaps(array, gridPane);
        setupEnv(gridPane);
        countSwaps = 0;
        countComparison = 0;
        width = (int) (array[0].getImage().getWidth() % 2 == 0 ? array[0].getImage().getWidth() :
                array[0].getImage().getWidth() - 1);
        height = (int) (array[0].getImage().getHeight() % 2 == 0 ? array[0].getImage().getHeight() :
                array[0].getImage().getHeight() - 1);
        thread = new Thread(() -> {
            doSort(array, 0, array.length - 1, gridPane, true);
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


    private void calculateNumberOfSwaps(Tile[] array, GridPane gridPane) {
        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        doSort(tmp, 0, tmp.length - 1, gridPane, false);
    }

    private <T extends Comparable<T>> void doSort(Tile[] array, int left, int right, GridPane gridPane, boolean write) {
        if (running) {
            countComparison++;
            if (left < right) {
                int pivot = randomPartition(array, left, right, gridPane, write);
                doSort(array, left, pivot - 1, gridPane, write);
                doSort(array, pivot, right, gridPane, write);
            }
        }
    }

    /**
     * Ramdomize the array to avoid the basically ordered sequences
     *
     * @param array The array to be sorted
     * @param left  The first index of an array
     * @param right The last index of an array
     * @return the partition index of the array
     */
    private <T extends Comparable<T>> int randomPartition(Tile[] array, int left, int right, GridPane gridPane,
                                                          boolean write) {
        int randomIndex = left + (int) (Math.random() * (right - left + 1));

        countSwaps++;
        if ((countSwaps % delay) == 0 && write)
            writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
        if (write)
            progressBar.setProgress(progress += increment);
        swap(array, randomIndex, right);

        return partition(array, left, right, gridPane);
    }

    /**
     * This method finds the partition index for an array
     *
     * @param array The array to be sorted
     * @param left  The first index of an array
     * @param right The last index of an array Finds the partition index of an
     *              array
     */
    private <T extends Comparable<T>> int partition(Tile[] array, int left, int right, GridPane gridPane) {
        int mid = (left + right) >>> 1;
        Tile pivot = array[mid];

        while (left <= right) {
            countComparison++;
            while (less(array[left], pivot)) {
                countComparison++;
                ++left;
            }
            while (less(pivot, array[right])) {
                countComparison++;
                --right;
            }
            countComparison++;
            if (left <= right) {
                int finalLeft = left;
                int finalRight = right;

                countSwaps++;
                swap(array, left, right);
                ++left;
                --right;
            }
        }
        return left;
    }


}

