package savti.sorting;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import savti.*;

import static savti.sorting.SortUtils.less;
import static savti.utilities.FileUtilities.writeFrame;
import static savti.utilities.FileUtilities.writeFreezedFrames;
import static savti.utilities.ImageUtilities.resetCoordinates;

public class CycleSort extends AbstractSort {

    public CycleSort(UserSettings userSettings, TiledImage image, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, OutputHandler outputHandler) {
        super(userSettings, image, imageView, algorithmProgressBar,outputHandler);
    }

    private <T extends Comparable<T>> T replace(T[] arr, int pos, T item) {
        T temp = item;
        item = arr[pos];
        arr[pos] = temp;
        return item;
    }

    @Override
    public void sort() {

        setupEnv(image.getArray());

        int n = image.getArray().length;

        // traverse array elements
        for (int j = 0; j <= n - 2; j++) {
            // initialize item as starting point
            Tile item = image.getArray()[j];

            // Find position where we put the item.
            int pos = j;
            for (int i = j + 1; i < n; i++) {
                countComparison++;
                if (less(image.getArray()[i], item)) {
                    pos++;
                }
            }

            // If item is already in correct position
            if (pos == j) {
                continue;
            }

            // ignore all duplicate elements
            while (item.compareTo(image.getArray()[pos]) == 0) {
                pos += 1;
            }

            // put the item to it's right position
            if (pos != j) {
                countSwaps++;
                progress += increment;
                    algorithmProgressBar.setProgress(progress += increment);
                if (countSwaps % delay == 0) {
                   writeFrame(outputHandler,image,userSettings,countSwaps,countComparison,10);
                }
                item = replace(image.getArray(), pos, item);
            }

            // Rotate rest of the cycle
            while (pos != j) {
                pos = j;

                // Find position where we put the element
                for (int i = j + 1; i < n; i++) {
                    countComparison++;
                    if (less(image.getArray()[i], item)) {
                        pos += 1;
                    }
                }

                // ignore all duplicate elements
                while (item.compareTo(image.getArray()[pos]) == 0) {
                    pos += 1;
                }

                // put the item to it's right position
                if (item != image.getArray()[pos]) {
                    countSwaps++;
                    progress += increment;
                    algorithmProgressBar.setProgress(progress += increment);
                    item = replace(image.getArray(), pos, item);
                    if (countSwaps % delay == 0) {
                       writeFrame(outputHandler,image,userSettings,countSwaps,countComparison,10);
                    }
                }
            }
        }

        writeFreezedFrames(userSettings.getFrameRate() * 2, outputHandler, image, userSettings, countSwaps, countComparison, (int) (imageView.getFitWidth() / 150f));
        outputHandler.closeOutputChannel();        outputHandler.closeOutputChannel();
        Platform.runLater(() -> resumeProgram(imageView, image));
    }

    @Override
    protected void calculateNumberOfSwaps(Tile[] a) {
        Tile[] array = new Tile[a.length];
        System.arraycopy(a, 0, array, 0, a.length);
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
        resetCoordinates(userSettings, a);
    }

    @Override
    public void run() {
        sort();
    }
}

