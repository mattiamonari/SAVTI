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

public class SelectionSort extends AbstractSort implements SortAlgorithm {

    public SelectionSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(Tail[] array, GridPane gridPane, MainWindow mainWindow) {
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
                    if (!running)
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
                if ((countSwaps % delay) == 0) {
                    writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
                }
                if (!running)
                    break;
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

    private void calculateNumberOfSwaps(Tail[] array) {
        Tail[] tmp = new Tail[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);

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
}
