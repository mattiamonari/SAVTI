package savti.sorting;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import savti.*;

import static savti.sorting.SortUtils.replace;
import static savti.utilities.FileUtilities.writeFrame;
import static savti.utilities.FileUtilities.writeFreezedFrames;
import static savti.utilities.ImageUtilities.resetCoordinates;

public class MergeSort extends AbstractSort {


    public MergeSort(UserSettings userSettings, TiledImage image, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, OutputHandler outputHandler) {
        super(userSettings, image, imageView, algorithmProgressBar, outputHandler);
    }

    @Override
    public void sort() {

        setupEnv(image.getArray());

        mergeSort(image.getArray(), 0, image.getArray().length - 1, true);

        writeFreezedFrames(userSettings.getFrameRate() * 2, outputHandler, image, userSettings, countSwaps, countComparison, (int) (image.getImage().getWidth() / 100f));

        outputHandler.closeOutputChannel();

        Platform.runLater(() -> resumeProgram(imageView, image));
    }

    @Override
    protected void calculateNumberOfSwaps(Tile[] array) {
        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        mergeSort(tmp, 0, array.length - 1, false);
        resetCoordinates(userSettings, array);
    }

    void mergeSort(Tile[] arr, int left, int right, boolean write) {
        ++countComparison;
        if (left < right) {

            // mid is the point where the array is divided into two subarrays
            int mid = (left + right) / 2;

            mergeSort(arr, left, mid, write);
            mergeSort(arr, mid + 1, right, write);

            // Merge the sorted subarrays
            merge(arr, left, mid, right, write);
        }
    }

    void merge(Tile[] arr, int p, int q, int r, boolean write) {

        // Create L ← A[p..q] and M ← A[q+1..r]
        int n1 = q - p + 1;
        int n2 = r - q;

        Tile[] L = new Tile[n1];
        Tile[] M = new Tile[n2];

        System.arraycopy(arr, p, L, 0, n1);
        for (int j = 0; j < n2; j++) {
            M[j] = Tile.newInstance(arr[q + 1 + j]);
        }

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
                progress += increment;
                algorithmProgressBar.setProgress(progress);
                replace(arr, k, L[i]);
                i++;

            } else {
                ++countSwaps;
                progress += increment;
                algorithmProgressBar.setProgress(progress);
                replace(arr, k, M[j]);
                j++;
            }
            if (countSwaps % delay == 0 && write)
                writeFrame(outputHandler, image, userSettings, countSwaps, countComparison, (int)(image.getImage().getWidth() / 100f));
            k++;
        }

        // When we run out of elements in either L or M,
        // pick up the remaining elements and put in A[p..r]
        while (i < n1) {
            replace(arr, k, L[i]);
            ++countSwaps;
            progress += increment;
            algorithmProgressBar.setProgress(progress);
            i++;
            k++;
            if (countSwaps % delay == 0 && write)
                writeFrame(outputHandler, image, userSettings, countSwaps, countComparison, (int)(image.getImage().getWidth() / 100f));
        }

        while (j < n2) {
            replace(arr, k, M[j]);
            ++countSwaps;
            progress += increment;
            algorithmProgressBar.setProgress(progress);
            j++;
            k++;
            if (countSwaps % delay == 0 && write)
                writeFrame(outputHandler, image, userSettings, countSwaps, countComparison, (int)(image.getImage().getWidth() / 100f));
        }
    }

    @Override
    public void run() {
        sort();
    }
}
