package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import static JavaFXVersion.sorting.SortUtils.less;
import static JavaFXVersion.utilities.FileUtilities.writeImage;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class CycleSort extends AbstractSort {

    public CycleSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void killTask() {
        running = false;
    }

    @Override
    public boolean isThreadAlive() {
        return running;
    }

    private <T extends Comparable<T>> T replace(T[] arr, int pos, T item) {
        T temp = item;
        item = arr[pos];
        arr[pos] = temp;
        return item;
    }

    @Override
    public void sort(ImageView imageView, Tile[] array, MainWindow mainWindow) {

        setupEnv(imageView, array);

        thread = new Thread(() -> {

            int n = array.length;

            // traverse array elements
            for (int j = 0; j <= n - 2; j++) {
                // initialize item as starting point
                Tile item = array[j];

                // Find position where we put the item.
                int pos = j;
                for (int i = j + 1; i < n; i++) {
                    countComparison++;
                    if (less(array[i], item)) {
                        pos++;
                    }
                }

                // If item is already in correct position
                if (pos == j) {
                    continue;
                }

                // ignore all duplicate elements
                while (item.compareTo(array[pos]) == 0) {
                    pos += 1;
                }

                // put the item to it's right position
                if (pos != j) {
                    countSwaps++;
                    progressBar.setProgress(progress += increment);
                    if (countSwaps % delay == 0)
                        writeImage(userSettings,array,width,height,imageIndex++,countComparison,countSwaps, imageView.getFitWidth() / 150f);
                    item = replace(array, pos, item);
                }

                // Rotate rest of the cycle
                while (pos != j) {
                    pos = j;

                    // Find position where we put the element
                    for (int i = j + 1; i < n; i++) {
                        countComparison++;
                        if (less(array[i], item)) {
                            pos += 1;
                        }
                    }

                    // ignore all duplicate elements
                    while (item.compareTo(array[pos]) == 0) {
                        pos += 1;
                    }

                    // put the item to it's right position
                    if (item != array[pos]) {
                        countSwaps++;
                        item = replace(array, pos, item);
                        if(countSwaps % delay == 0)
                            writeImage(userSettings,array,width,height,imageIndex++,countComparison,countSwaps, imageView.getFitWidth() / 150f);                      progressBar.setProgress(progress += increment);
                    }
                }
            }

            runFFMPEG(array, imageView);
            Platform.runLater(() -> resumeProgram(imageView, mainWindow, array));
        });
        thread.start();
    }

    @Override
    protected void calculateNumberOfSwaps(Tile[] a) {
        Tile[] array = new Tile[a.length];
        System.arraycopy(a,0, array,0, a.length);
        int n = a.length;
        // traverse array elements
        for (int j = 0; j <= n - 2; j++) {
            // initialize item as starting point
            Tile item = array[j];

            // Find position where we put the item.
            int pos = j;
            for (int i = j + 1; i < n; i++) {
                countComparison++;
                if (less(array[i], item)) {
                    pos++;
                }
            }

            // If item is already in correct position
            if (pos == j) {
                continue;
            }

            // ignore all duplicate elements
            while (item.compareTo(array[pos]) == 0) {
                pos += 1;
            }

            // put the item to it's right position
            if (pos != j) {
                countSwaps++;
                item = replace(array, pos, item);
            }

            // Rotate rest of the cycle
            while (pos != j) {
                pos = j;

                // Find position where we put the element
                for (int i = j + 1; i < n; i++) {
                    countComparison++;
                    if (less(array[i], item)) {
                        pos += 1;
                    }
                }

                // ignore all duplicate elements
                while (item.compareTo(array[pos]) == 0) {
                    pos += 1;
                }

                // put the item to it's right position
                if (item != array[pos]) {
                    countSwaps++;
                    item = replace(array, pos, item);
                }
            }
        }
        resetCoordinates(userSettings, array);
    }
}

