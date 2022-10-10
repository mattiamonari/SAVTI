package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.TiledImage;
import JavaFXVersion.UserSettings;
import JavaFXVersion.utilities.ErrorUtilities;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import static JavaFXVersion.sorting.SortUtils.less;
import static JavaFXVersion.sorting.SortUtils.swap;
import static JavaFXVersion.utilities.FileUtilities.writeFrame;
import static JavaFXVersion.utilities.FileUtilities.writeFreezedFrames;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class QuickSort extends AbstractSort {

    ImageView imageView;

    TiledImage image;

    public QuickSort(UserSettings userSettings, TiledImage image, ImageView imageView, AWTSequenceEncoder encoder, SeekableByteChannel out) {
        super(userSettings, image, imageView, encoder, out);
    }

    @Override
    public void sort(ImageView imageView, TiledImage image, MainWindow mainWindow) {

        setupEnv(imageView, image.getArray());

        this.imageView = imageView;
        this.image = image;

        thread = new Thread(() -> {

            doSort(image.getArray(), 0, image.getArray().length - 1, true);

            writeFreezedFrames(userSettings.getFrameRate() * 2, encoder, image, userSettings, increment, progressBar);

            try {
                encoder.finish();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            NIOUtils.closeQuietly(out);

            Platform.runLater(() -> resumeProgram(imageView, mainWindow, image));
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

        countComparison++;
        if (left < right) {
            int pivot = randomPartition(array, left, right, write);
            doSort(array, left, pivot - 1, write);
            doSort(array, pivot, right, write);
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
            writeFrame(encoder, image, userSettings, increment, progressBar);
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
            while (less(array[left], pivot)) {
                countComparison++;
                ++left;
            }
            while (less(pivot, array[right])) {
                countComparison++;
                --right;
            }
            if (left <= right) {
                countSwaps++;
                if (countSwaps % delay == 0 && write)
                    writeFrame(encoder, image, userSettings, increment, progressBar);
                swap(array, left, right);
                ++left;
                --right;
            }
        }
        return left;
    }
}

