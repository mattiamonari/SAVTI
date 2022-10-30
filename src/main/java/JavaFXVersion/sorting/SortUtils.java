package JavaFXVersion.sorting;

import JavaFXVersion.Tile;
import JavaFXVersion.TiledImage;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static JavaFXVersion.utilities.FileUtilities.*;
import static JavaFXVersion.utilities.GUIUtilities.ableNodes;
import static JavaFXVersion.utilities.ImageUtilities.fillImage;
import static JavaFXVersion.utilities.ImageUtilities.splitImage;

public final class SortUtils {
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

    static void replace(Tile[] array, int idx, Tile newTile) {
        swapCoordinates(array[idx], newTile);
        array[idx] = newTile;
    }

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

    public static <T extends Comparable<T>> void reverse(T[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            T temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }

    public static void rand(UserSettings userSettings, TiledImage image, AWTSequenceEncoder encoder, SeekableByteChannel out) {
        int delay = Math.max(4 * image.getArray().length / (userSettings.getFrameRate() * userSettings.getVideoDuration()), 1);
        Random rd = new Random();

        if(!out.isOpen())
        {
            try {
                out = NIOUtils.writableFileChannel("C:\\Users\\andrea\\IdeaProjects\\sortingVisualization\\ext\\output.mp4");
                encoder = new AWTSequenceEncoder(out, Rational.R(userSettings.getFrameRate(), 1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        writeFreezedFrames(userSettings.getFrameRate() * 2, encoder, image, userSettings);

        for (int i = image.getArray().length - 1; i > 0; i--) {
            if(i % delay == 0)
                writeFrame(encoder, image, userSettings);
            // Pick a random index from 0 to i
            int j = rd.nextInt(i + 1);
            // Swap array[i] with the element at random index
            swap(image.getArray(), i, j);
        }
    }
}