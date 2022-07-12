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

public class GnomeSort extends AbstractSort implements SortAlgorithm {



    public GnomeSort(UserSettings userSettings) {
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
            int i = 1;
            int n = array.length;
            while (i < n) {
                if (!running)
                    break;
                ++countComparison;
                if (i == 0 || SortUtils.greater(array[i], array[i - 1])) {
                    i++;
                } else {
                    ++countSwaps;
                    Tail tmp = array[i];
                    array[i] = array[i - 1];
                    array[--i] = tmp;
                    if ((countSwaps % delay) == 0)
                        writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
                    progressBar.setProgress(progress += increment);
                }
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
        int i = 1;
        int n = tmp.length;
        while (i < n) {
            if (i == 0 || SortUtils.greater(tmp[i], tmp[i - 1])) {
                i++;
            } else {
                ++countSwaps;
                Tail temp = tmp[i];
                tmp[i] = tmp[i - 1];
                tmp[--i] = temp;
            }
        }
    }
}
