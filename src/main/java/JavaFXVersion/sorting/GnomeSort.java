package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import static JavaFXVersion.utilities.FileUtilities.writeImage;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class GnomeSort extends AbstractSort {



    public GnomeSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(ImageView imageView, Tile[] array, MainWindow mainWindow) {

        setupEnv(imageView, array);

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
                    Tile tmp = array[i];
                    array[i] = array[i - 1];
                    array[--i] = tmp;
                    if (countSwaps % delay == 0)
                        writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps, imageView.getFitWidth() / 150f);
                    progressBar.setProgress(progress += increment);
                }
            }
            runFFMPEG(array, imageView);
            Platform.runLater(() -> resumeProgram(imageView, mainWindow, array));
        });
        thread.start();
    }
    @Override
    protected void calculateNumberOfSwaps(Tile[] array) {

        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        int i = 1;
        int n = tmp.length;
        while (i < n) {
            if (i == 0 || SortUtils.greater(tmp[i], tmp[i - 1])) {
                i++;
            } else {
                ++countSwaps;
                Tile temp = tmp[i];
                tmp[i] = tmp[i - 1];
                tmp[--i] = temp;
            }
        }
        resetCoordinates(userSettings, array);
    }
}
