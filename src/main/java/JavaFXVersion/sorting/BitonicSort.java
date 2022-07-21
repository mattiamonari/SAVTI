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
import java.util.Arrays;

import static JavaFXVersion.utilities.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.utilities.FileUtilities.writeImage;

/* Java program for Bitonic Sort. Note that this program
works only when size of input is a power of 2. */
public class BitonicSort extends AbstractSort implements SortAlgorithm {

    public BitonicSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void killTask() {
        running = false;
    }

    @Override
    public boolean isThreadAlive() {
        return running;
    }


    void compAndSwap(Tile a[], int i, int j, int dir, boolean write) {
        ++countComparison;
        if (( (a[i].compareTo(a[j]) > 0) && dir == 1) || ( (a[i].compareTo(a[j]) < 0) && dir == 0)) {
            ++countSwaps;
            // Swapping elements
            Tile temp = a[i];
            a[i] = a[j];
            a[j] = temp;
            if ((countSwaps % delay) == 0 && write) {
                writeImage(userSettings, a, width, height, imageIndex++, countComparison, countSwaps);
            }
            if(write)
                progressBar.setProgress(progress+=increment);
        }
    }
    void bitonicMerge(Tile a[], int low, int cnt, int dir, boolean write) {
        if (cnt > 1) {
            int k = cnt / 2;
            for (int i = low; i < low + k; i++) {
                compAndSwap(a, i, i + k, dir, write);
            }
            bitonicMerge(a, low, k, dir, write);
            bitonicMerge(a, low + k, k, dir, write);
        }
    }

    void bitonicSort(Tile[] array, int low, int cnt, int dir, boolean write) {
        if (cnt > 1) {
            int k = cnt / 2;

            // sort in ascending order since dir here is 1
            bitonicSort(array,low, k, 1, write);

            // sort in descending order since dir here is 0
            bitonicSort(array, low + k, k, 0, write);

            // Will merge whole sequence in ascending order
            // since dir=1.
            bitonicMerge(array, low, cnt, dir, write);
        }
    }

    @Override
    public void sort(Tile[] array, GridPane gridPane, MainWindow mainWindow) {
        //We use a new thread to pause/resume its execution whenever we want
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

            bitonicSort(array,0,array.length,1, true);

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
            Platform.runLater(() -> resumeProgram(gridPane, mainWindow, array));

        });
        thread.start();
    }

    private void calculateNumberOfSwaps(Tile[] array) {
        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array,0,tmp,0, array.length);
        bitonicSort(tmp, 0, array.length, 1, false);
    }
}