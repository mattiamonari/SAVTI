package savti.sorting;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import savti.*;

import static savti.utilities.FileUtilities.writeFrame;
import static savti.utilities.FileUtilities.writeFreezedFrames;
import static savti.utilities.ImageUtilities.resetCoordinates;

public class SelectionSort extends AbstractSort {
    public SelectionSort(UserSettings userSettings, TiledImage image, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, OutputHandler outputHandler) {
        super(userSettings, image, imageView, algorithmProgressBar, outputHandler);
    }

    @Override
    public void sort() {

        setupEnv(image.getArray());

        int size = image.getArray().length;
        for (int step = 0; step < size - 1; step++) {
            int min_idx = step;
            for (int k = step + 1; k < size; k++) {
                // To sort in descending order, change > to < in this line.
                // Select the minimum element in each loop.
                ++countComparison;
                if (SortUtils.greater(image.getArray()[min_idx], image.getArray()[k])) {
                    min_idx = k;
                }
            }
            // put min at the correct position
            SortUtils.swap(image.getArray(), step, min_idx);
            ++countSwaps;
            progress += increment;
            algorithmProgressBar.setProgress(progress);
            if (countSwaps % delay == 0) {
                writeFrame(outputHandler, image, userSettings, countSwaps, countComparison, (int)(image.getImage().getWidth() / 100f));
            }
        }

        writeFreezedFrames(userSettings.getFrameRate() * 2, outputHandler, image, userSettings, countSwaps, countComparison, (int) (image.getImage().getWidth() / 100f));
        outputHandler.closeOutputChannel();

        Platform.runLater(() -> resumeProgram(imageView, image));
    }

    @Override
    public void calculateNumberOfSwaps(Tile[] array) {
        Tile[] tmp = new Tile[array.length];
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
        resetCoordinates(userSettings, array);
    }

    @Override
    public void run() {
        sort();
    }
}
