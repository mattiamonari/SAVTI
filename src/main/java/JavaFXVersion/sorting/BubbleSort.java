package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.TiledImage;
import JavaFXVersion.UserSettings;
import JavaFXVersion.utilities.ErrorUtilities;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static JavaFXVersion.utilities.FileUtilities.*;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class BubbleSort extends AbstractSort {

    public BubbleSort(UserSettings userSettings, TiledImage image, ImageView imageView, AWTSequenceEncoder encoder, SeekableByteChannel out) {
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

    @Override
    public void sort(ImageView imageView, TiledImage image, MainWindow mainWindow) {
        //We use a new thread to pause/resume its execution whenever we want

        //Platform.runLater(() -> setupEnv(imageView, image.getArray()));

//        thread = new Thread(() -> {
//            thread.setPriority(Thread.MAX_PRIORITY);

            //------------NOT IN HERE!!!----------------
            if (!userSettings.getOutputDirectory().isDirectory())
                if (!userSettings.getOutputDirectory().mkdir())
                    ErrorUtilities.SWW();
            //-------------------------------------------

            for (int size = image.getArray().length, i = 1; i < size; ++i) {
                boolean swapped = false;
                for (int j = 0; j < size - i; ++j) {
                    countComparison++;

                    /*      SWAP SECTION     */
                    if (SortUtils.greater(image.getArray()[j], image.getArray()[j + 1])) {
                        countSwaps++;
                        SortUtils.swap(image.getArray(), j, j + 1);
                        swapped = true;

                        /*      FRAMEWRITING SECTION     */
                        if (countSwaps % delay == 0)
                            writeFrame(encoder, image, userSettings);

                    }
                }
                if (!swapped) {
                    break;
                }
            }

            writeFreezedFrames(userSettings.getFrameRate() * 2, encoder, image, userSettings);

            try {
                encoder.finish();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            NIOUtils.closeQuietly(out);

            //runFFMPEG(image.getArray(), imageView);

//            Platform.runLater(() -> resumeProgram(imageView, mainWindow, image));
//        });
//        thread.start();
    }

    @Override
    protected void calculateNumberOfSwaps(Tile[] array) {
        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);

        for (int size = tmp.length, i = 1; i < size; ++i) {
            boolean swapped = false;

            for (int j = 0; j < size - i; ++j) {
                if (SortUtils.greater(tmp[j], tmp[j + 1])) {
                    countSwaps++;
                    SortUtils.swap(tmp, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        resetCoordinates(userSettings, array);
    }

}