package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import static JavaFXVersion.utilities.FileUtilities.writeImage;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class MergeSort extends AbstractSort {

    ImageView imageView;

    public MergeSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(ImageView imageView, Tile[] array, MainWindow mainWindow) {

        setupEnv(imageView, array);

        this.imageView = imageView;

        thread = new Thread(() -> {

            mergeSort(array, 0, array.length - 1, true);
            runFFMPEG(array, imageView);
            Platform.runLater(() -> resumeProgram(imageView, mainWindow, array));

        });

        thread.start();


    }

    @Override
    protected void calculateNumberOfSwaps(Tile[] array) {
        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        mergeSort(tmp, 0, array.length - 1, false);
        resetCoordinates(userSettings, array);
    }

    void mergeSort(Tile[] arr, int l, int r, boolean write) {
        ++countComparison;
        if (l < r && running) {

            // m is the point where the array is divided into two subarrays
            int m = (l + r) / 2;

            mergeSort(arr, l, m, write);
            mergeSort(arr, m + 1, r, write);

            // Merge the sorted subarrays
            merge(arr, l, m, r, write);
        }
    }

    void merge(Tile[] arr, int p, int q, int r, boolean write) {

        if (running) {
            // Create L ← A[p..q] and M ← A[q+1..r]
            int n1 = q - p + 1;
            int n2 = r - q;

            Tile[] L = new Tile[n1];
            Tile[] M = new Tile[n2];

            System.arraycopy(arr, p, L, 0, n1);
            for (int j = 0; j < n2; j++)
                M[j] = arr[q + 1 + j];

            // Maintain current index of sub-arrays and main array
            int i, j, k;
            i = 0;
            j = 0;
            k = p;

            // Until we reach either end of either L or M, pick larger among
            // elements L and M and place them in the correct position at A[p..r]
            while (i < n1 && j < n2) {
                ++countComparison;
                if (SortUtils.greater(M[j], L[i])) {
                    ++countSwaps;
                    arr[k] = L[i];
                    i++;

                } else {
                    ++countSwaps;
                    arr[k] = M[j];
                    j++;
                }
                if (countSwaps % delay == 0 && write)
                    writeImage(userSettings, arr, width, height, imageIndex++, countComparison, countSwaps, imageView.getFitWidth() / 150f);
                if (write)
                    progressBar.setProgress(progress += increment);
                k++;
            }

            // When we run out of elements in either L or M,
            // pick up the remaining elements and put in A[p..r]
            while (i < n1) {
                arr[k] = L[i];
                i++;
                k++;
                if (countSwaps % delay == 0 && write)
                    writeImage(userSettings, arr, width, height, imageIndex++, countComparison, countSwaps, imageView.getFitWidth() / 150f);
                if (write)
                    progressBar.setProgress(progress += increment);
            }

            while (j < n2) {
                arr[k] = M[j];
                j++;
                k++;
                if (countSwaps % delay == 0 && write)
                    writeImage(userSettings, arr, width, height, imageIndex++, countComparison, countSwaps,imageView.getFitWidth() / 150f);
                if (write)
                    progressBar.setProgress(progress += increment);
            }
        }
    }
}
