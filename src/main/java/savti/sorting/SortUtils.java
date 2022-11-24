package savti.sorting;

import savti.*;

import java.util.Random;

import static savti.utilities.FileUtilities.writeFrame;
import static savti.utilities.FileUtilities.writeFreezedFrames;

public final class SortUtils {

    private SortUtils() {

    }

    /**
     * Helper method for swapping places in array
     *
     * @param array The array which elements we want to swap
     * @param idx   index of the first element
     * @param idy   index of the second element
     */
    public static void swap(Tile[] array, int idx, int idy) {
        Tile swap = array[idx];
        array[idx] = array[idy];
        array[idy] = swap;
        swapCoordinates(array[idx], array[idy]);
    }


    /**
     * Given an array of Tiles, replace the element at position idx with a new tile
     *
     * @param array   Array of tiles
     * @param idx     Position of the element which will be replaced
     * @param newTile The new element
     */
    static void replace(Tile[] array, int idx, Tile newTile) {
        swapCoordinates(array[idx], newTile);
        array[idx] = newTile;
    }


    /**
     * Swap the coordinates of two tiles.
     *
     * @param t1 First tile
     * @param t2 Second tile
     */
    public static void swapCoordinates(Tile t1, Tile t2) {
        int x1 = t1.getX();
        int y1 = t1.getY();
        t1.setX(t2.getX());
        t1.setY(t2.getY());
        t2.setX(x1);
        t2.setY(y1);
    }

    /**
     * This method checks if first element is less than the other element
     *
     * @param v first element
     * @param w second element
     * @return true if the first element is less than the second element
     */
    static <T extends Comparable<T>> boolean less(T v, T w) {
        return v.compareTo(w) < 0;
    }

    /**
     * This method checks if first element is greater than the other element
     *
     * @param v first element
     * @param w second element
     * @return true if the first element is greater than the second element
     */
    static <T extends Comparable<T>> boolean greater(T v, T w) {
        return v.compareTo(w) > 0;
    }

    public static void rand(UserSettings userSettings, TiledImage image, OutputHandler outputHandler, AlgorithmProgressBar algorithmProgressBar) {
        double progress = 0;
        double increment = 1d / (image.getArray().length - 1);
        int delay = Math.max(4 * image.getArray().length / (userSettings.getFrameRate() * userSettings.getVideoDuration()), 1);
        Random rd = new Random();

        algorithmProgressBar.setAlgoName("Randomizing the image...");

        writeFreezedFrames(userSettings.getFrameRate() * 2, outputHandler, image);

        for (int i = image.getArray().length - 1; i > 0; i--) {
            if (i % delay == 0)
                writeFrame(outputHandler, image);
            // Pick a random index from 0 to i
            int j = rd.nextInt(i + 1);
            // Swap array[i] with the element at random index
            swap(image.getArray(), i, j);

            progress += increment;

            algorithmProgressBar.setProgress(progress);
        }
    }
}