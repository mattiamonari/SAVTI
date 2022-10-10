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

import static JavaFXVersion.sorting.SortUtils.replace;
import static JavaFXVersion.utilities.FileUtilities.writeFrame;
import static JavaFXVersion.utilities.FileUtilities.writeFreezedFrames;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class RadixSort extends AbstractSort {

    boolean write;
    ImageView imageView;


    public RadixSort(UserSettings userSettings, TiledImage image, ImageView imageView, AWTSequenceEncoder encoder, SeekableByteChannel out) {
        super(userSettings, image, imageView, encoder, out);
    }

    @Override
    public void sort(ImageView imageView, TiledImage image, MainWindow mainWindow) {

        setupEnv(imageView, image.getArray());

        thread = new Thread(() -> {
            write = true;


            // Get maximum element
            int size = image.getArray().length;
            Tile max = getMax(image.getArray(), size);

            // Apply counting sort to sort elements based on place value.
            for (int place = 1; max.currentPosition / place > 0; place *= 10) {
                countingSort(image.getArray(), size, place);
            }

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

    Tile getMax(Tile[] array, int n) {
        Tile max = array[0];
        for (int i = 1; i < n; i++) {
            ++countComparison;
            if (SortUtils.greater(array[i], max))
                max = array[i];
        }
        return max;
    }

    void countingSort(Tile[] array, int size, int place) {
        Tile[] output = new Tile[size + 1];
        Tile max = array[0];
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
            replace(array, i, output[i]);
            if (countSwaps % delay == 0 && write) {
                writeFrame(encoder, image, userSettings, increment, progressBar);
            }

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
            countingSort(tmp, size, place);

        resetCoordinates(userSettings, array);
    }
}
