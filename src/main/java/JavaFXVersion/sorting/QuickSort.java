package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import static JavaFXVersion.sorting.SortUtils.less;
import static JavaFXVersion.sorting.SortUtils.swap;
import static JavaFXVersion.utilities.FileUtilities.writeImage;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class QuickSort extends AbstractSort {

    ImageView imageView;

    public QuickSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(ImageView imageView, Tile[] array, MainWindow mainWindow) {

        setupEnv(imageView, array);

        this.imageView = imageView;

        thread = new Thread(() -> {
            doSort(array, 0, array.length - 1, true);

            runFFMPEG(array, imageView);
            Platform.runLater(() -> resumeProgram(imageView, mainWindow, array));
        });
        thread.start();
    }

    @Override
    protected void calculateNumberOfSwaps(Tile[] array) {
        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        doSort(tmp, 0, tmp.length - 1, false);
        resetCoordinates(userSettings, array);
    }

    private void doSort(Tile[] array, int left, int right, boolean write) {
        if (running) {
            countComparison++;

            if (left < right) {
                int pivot = randomPartition(array, left, right, write);
                doSort(array, left, pivot - 1, write);
                doSort(array, pivot, right, write);
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
    private int randomPartition(Tile[] array, int left, int right,
                                boolean write) {
        int randomIndex = left + (int) (Math.random() * (right - left + 1));
        countSwaps++;
        if (countSwaps % delay == 0 && write)
            writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps, imageView.getImage().getWidth() / 100f);
        if (write)
            progressBar.setProgress(progress += increment);
        swap(array, randomIndex, right);

        return partition(array, left, right, write);
    }

    /**
     * This method finds the partition index for an array
     *
     * @param array The array to be sorted
     * @param left  The first index of an array
     * @param right The last index of an array Finds the partition index of an
     *              array
     */
    private int partition(Tile[] array, int left, int right, boolean write) {
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
                countSwaps++;
                if (countSwaps % delay == 0 && write)
                    writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps, imageView.getImage().getWidth() / 100f);
                if (write)
                    progressBar.setProgress(progress += increment);
                swap(array, left, right);
                ++left;
                --right;
            }
        }
        return left;
    }


}

