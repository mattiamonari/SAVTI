package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import static JavaFXVersion.utilities.FileUtilities.writeImage;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class SelectionSort extends AbstractSort {
    public SelectionSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(ImageView imageView, Tile[] array, MainWindow mainWindow) {

        setupEnv(imageView, array);

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
                    if (SortUtils.greater(array[min_idx] , array[k])) {
                        min_idx = k;
                    }
                }
                // put min at the correct position
                SortUtils.swap(array , step , min_idx);
                ++countSwaps;
                progressBar.setProgress(progress += increment);
                if (countSwaps % delay == 0)
                    writeImage(userSettings , array , width , height , imageIndex++ , countComparison , countSwaps, imageView.getFitWidth() / 150f);
                if (!running)
                    break;
            }
            runFFMPEG(array, imageView);
            Platform.runLater(() -> resumeProgram(imageView, mainWindow, array));
        });
        thread.start();
    }

    @Override
    public void calculateNumberOfSwaps(Tile[] array) {
        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array , 0 , tmp , 0 , array.length);
        int size = tmp.length;
        for (int step = 0; step < size - 1; step++) {
            int min_idx = step;
            for (int k = step + 1; k < size; k++) {
                if (SortUtils.greater(tmp[min_idx] , tmp[k])) {
                    min_idx = k;
                }
            }
            // put min at the correct position
            SortUtils.swap(tmp , step , min_idx);
            ++countSwaps;
        }
        resetCoordinates(userSettings, array);
    }
}
