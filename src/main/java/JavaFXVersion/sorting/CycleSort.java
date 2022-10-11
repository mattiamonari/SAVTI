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

import static JavaFXVersion.sorting.SortUtils.less;
import static JavaFXVersion.utilities.FileUtilities.writeFrame;
import static JavaFXVersion.utilities.FileUtilities.writeFreezedFrames;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class CycleSort extends AbstractSort {

    public CycleSort(UserSettings userSettings, TiledImage image, ImageView imageView, AWTSequenceEncoder encoder, SeekableByteChannel out) {
        super(userSettings, image, imageView, encoder, out);
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
    public void sort(ImageView imageView, TiledImage image, MainWindow mainWindow) {

        //setupEnv(imageView, image.getArray());

        thread = new Thread(() -> {

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
                    if (countSwaps % delay == 0) {
                        writeFrame(encoder, image, userSettings);
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
                        item = replace(image.getArray(), pos, item);
                        if (countSwaps % delay == 0) {
                            writeFrame(encoder, image, userSettings);
                        }
                    }
                }
            }

            writeFreezedFrames(userSettings.getFrameRate() * 2, encoder, image, userSettings);

            try {
                encoder.finish();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            NIOUtils.closeQuietly(out);
            //Platform.runLater(() -> resumeProgram(imageView, mainWindow, image));
        });
        thread.start();
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
}

