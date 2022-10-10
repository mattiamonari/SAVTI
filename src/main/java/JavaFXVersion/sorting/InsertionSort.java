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

import static JavaFXVersion.utilities.FileUtilities.writeFrame;
import static JavaFXVersion.utilities.FileUtilities.writeFreezedFrames;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class InsertionSort extends AbstractSort {

    public InsertionSort(UserSettings userSettings, TiledImage image, ImageView imageView, AWTSequenceEncoder encoder, SeekableByteChannel out) {
        super(userSettings, image, imageView, encoder, out);
    }

    @Override
    public void sort(ImageView imageView, TiledImage image, MainWindow mainWindow) {

        setupEnv(imageView, image.getArray());

        thread = new Thread(() -> {

            for (int i = 0; i < image.getArray().length; ++i) {

                int j = i;

                ++countComparison;
                //IS THIS CORRECT?
                while (j > 0 && SortUtils.greater(image.getArray()[j - 1], image.getArray()[j])) {
                    ++countSwaps;
                    SortUtils.swap(image.getArray(), j, j - 1);
                    j = j - 1;

                    if ((countSwaps % delay) == 0) {
                        writeFrame(encoder, image, userSettings, increment, progressBar);
                    }
                }
            }

            writeFreezedFrames(userSettings.getFrameRate() * 2, encoder, image, userSettings,increment,progressBar);

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

        for (int i = 0; i < tmp.length; ++i) {

            int j = i;

            while (j > 0 && SortUtils.greater(tmp[j - 1], tmp[j]) && running) {
                ++countSwaps;
                SortUtils.swap(tmp, j, j - 1);
                j = j - 1;
            }
            ++countComparison;
        }
        resetCoordinates(userSettings, array);
    }
}
