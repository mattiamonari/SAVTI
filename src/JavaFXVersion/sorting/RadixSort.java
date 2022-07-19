package JavaFXVersion.sorting;

import JavaFXVersion.FFMPEG;
import JavaFXVersion.MainWindow;
import JavaFXVersion.Tail;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static JavaFXVersion.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.FileUtilities.writeImage;

public class RadixSort extends AbstractSort implements SortAlgorithm {

    boolean write;

    public RadixSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(Tail[] array, GridPane gridPane, MainWindow mainWindow) {

        running = true;
        deleteAllPreviousFiles(userSettings);
        calculateNumberOfSwaps(array);
        setupEnv(gridPane);
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
                if (!running)
                    break;
                countingSort(array, size, place, delay, width, height, write);
            }
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
                gridPane.setVisible(true);
                ((BorderPane) gridPane.getParent()).getChildren().remove(progressBar);
                mainWindow.enableAll();
            });

        });
        thread.start();

    }

    Tail getMax(Tail[] array, int n) {
        Tail max = array[0];
        for (int i = 1; i < n; i++) {
            ++countComparison;
            if (SortUtils.greater(array[i], max))
                max = array[i];
        }
        return max;
    }

    void countingSort(Tail[] array, int size, int place, int delay, int width, int height, boolean write) {
        Tail[] output = new Tail[size + 1];
        Tail max = array[0];
        if (!running)
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
            if ((countSwaps % delay) == 0 && (write))
                writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
            if(write)
                progressBar.setProgress(progress += increment);
            //progressBar.setProgress(progress+=increment);

        }
    }


    private void calculateNumberOfSwaps(Tail[] array) {
        write = false;
        int size = array.length;
        Tail max = getMax(array, size);
        Tail[] tmp = new Tail[size];
        System.arraycopy(array, 0, tmp, 0, size);

        // Apply counting sort to sort elements based on place value.
        for (int place = 1; max.position / place > 0; place *= 10)
            countingSort(tmp, size, place, 1, 0, 0, write);
    }
}
