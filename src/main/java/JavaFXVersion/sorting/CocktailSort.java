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

public class CocktailSort extends AbstractSort implements SortAlgorithm {

    public CocktailSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(Tile[] array, GridPane gridPane, MainWindow mainWindow) {
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
            int n = array.length;
            int swap = 1;
            int beg = 0;
            int end = n - 1;
            int i;
            while (swap == 1) {
                swap = 0;
                for (i = beg; i < end; ++i) {
                    if (!running)
                        break;
                    ++countComparison;
                    if (SortUtils.greater(array[i], array[i + 1])) {
                        ++countSwaps;
                        SortUtils.swap(array, i, i + 1);
                        if ((countSwaps % delay) == 0)
                            writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
                        progressBar.setProgress(progress += increment);
                        swap = 1;
                    }
                }

                if (swap == 0)
                    break;

                swap = 0;
                --end;

                for (i = end - 1; i >= beg; --i) {
                    if (!running)
                        break;
                    ++countComparison;
                    if (SortUtils.greater(array[i], array[i + 1])) {
                        ++countSwaps;
                        SortUtils.swap(array, i, i + 1);
                        if ((countSwaps % delay) == 0)
                            writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
                        progressBar.setProgress(progress += increment);
                        swap = 1;
                    }
                }
                ++beg;
            }
            writeImage(userSettings, array, width, height, imageIndex, countComparison, countSwaps);
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
        System.arraycopy(array, 0, tmp, 0, array.length);

        int n = tmp.length;
        int swap = 1;
        int beg = 0;
        int end = n - 1;
        int i;
        while (swap == 1) {
            swap = 0;
            for (i = beg; i < end; ++i) {
                if (SortUtils.greater(tmp[i], tmp[i + 1])) {
                    ++countSwaps;
                    SortUtils.swap(tmp, i, i + 1);
                    swap = 1;
                }
            }

            if (swap == 0)
                break;

            swap = 0;
            --end;
            for (i = end - 1; i >= beg; --i) {
                if (SortUtils.greater(tmp[i], tmp[i + 1])) {
                    ++countSwaps;
                    SortUtils.swap(tmp, i, i + 1);
                    swap = 1;
                }
            }
            ++beg;
        }
    }
}
