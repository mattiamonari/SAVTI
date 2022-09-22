package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import static JavaFXVersion.utilities.FileUtilities.writeImage;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class RadixSort extends AbstractSort {

    boolean write;
    ImageView imageView;


    public RadixSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(ImageView imageView, Tile[] array, MainWindow mainWindow) {

        setupEnv(imageView, array);

        thread = new Thread(() -> {
            write = true;

            // Get maximum element
            int size = array.length;
            Tile max = getMax(array, size);

            // Apply counting sort to sort elements based on place value.
            for (int place = 1; max.currentPosition / place > 0; place *= 10) {
                if (!running)
                    break;
                countingSort(array, size, place, delay, width, height, write);
            }
            runFFMPEG(array, imageView);
            Platform.runLater(() -> resumeProgram(imageView, mainWindow, array));
        });
        thread.start();

    }

    Tile getMax(Tile[] array, int n) {
        Tile max = array[0];
        for (int i = 1; i < n; i++) {
            ++countComparison;
            if (SortUtils.greater(array[i], max))
                max = array[i];
        }
        return max;
    }

    void countingSort(Tile[] array, int size, int place, double delay, int width, int height, boolean write) {
        Tile[] output = new Tile[size + 1];
        Tile max = array[0];
        if (!running)
            return;
        for (int i = 1; i < size; i++) {
            ++countComparison;
            if (SortUtils.greater(array[i], max))
                max = array[i];
        }
        int[] count = new int[max.currentPosition + 1];

        for (int i = 0; i < max.currentPosition; ++i)
            count[i] = 0;

        // Calculate count of elements
        for (int i = 0; i < size; i++)
            count[(array[i].currentPosition / place) % 10]++;

        // Calculate cumulative count
        for (int i = 1; i < 10; i++)
            count[i] += count[i - 1];

        // Place the elements in sorted order
        for (int i = size - 1; i >= 0; i--) {
            output[count[(array[i].currentPosition / place) % 10] - 1] = array[i];
            count[(array[i].currentPosition / place) % 10]--;
        }

        for (int i = 0; i < size; i++) {
            ++countSwaps;
            array[i] = output[i];
            if (countSwaps % delay == 0 && write)
                writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps, imageView.getFitWidth() / 150f);
            if(write)
                progressBar.setProgress(progress += increment);
            //progressBar.setProgress(progress+=increment);

        }
    }


    @Override
    protected void calculateNumberOfSwaps(Tile[] array) {
        write = false;
        int size = array.length;
        Tile max = getMax(array, size);
        Tile[] tmp = new Tile[size];
        System.arraycopy(array, 0, tmp, 0, size);

        // Apply counting sort to sort elements based on place value.
        for (int place = 1; max.currentPosition / place > 0; place *= 10)
            countingSort(tmp, size, place, 1, 0, 0, write);

        resetCoordinates(userSettings, array);
    }
}
