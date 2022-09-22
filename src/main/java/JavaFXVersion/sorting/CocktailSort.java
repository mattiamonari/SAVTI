package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import static JavaFXVersion.utilities.FileUtilities.writeImage;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class CocktailSort extends AbstractSort {

    public CocktailSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(ImageView imageView, Tile[] array, MainWindow mainWindow) {

        setupEnv(imageView, array);

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
                        if (countSwaps % delay == 0)
                            writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps, imageView.getFitWidth() / 150f);
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
                        if (countSwaps % delay == 0)
                            writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps, imageView.getFitWidth() / 150f);
                        progressBar.setProgress(progress += increment);
                        swap = 1;
                    }
                }
                ++beg;
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
        resetCoordinates(userSettings, array);
    }
}
