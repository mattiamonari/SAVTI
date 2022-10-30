package JavaFXVersion.sorting;

import JavaFXVersion.AlgorithmProgressBar;
import JavaFXVersion.Tile;
import JavaFXVersion.TiledImage;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;

import java.io.IOException;

import static JavaFXVersion.utilities.FileUtilities.writeFrame;
import static JavaFXVersion.utilities.FileUtilities.writeFreezedFrames;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class GnomeSort extends AbstractSort {


    public GnomeSort(UserSettings userSettings, TiledImage image, ImageView imageView, AWTSequenceEncoder encoder, SeekableByteChannel out, AlgorithmProgressBar algorithmProgressBar) {
        super(userSettings, image, imageView, encoder, out, algorithmProgressBar);
    }

    @Override
    public void sort() {

        Platform.runLater(() -> setupEnv(imageView, image.getArray()));

        int i = 1;
        int n = image.getArray().length;
        while (i < n) {
            ++countComparison;
            if (i == 0 || SortUtils.greater(image.getArray()[i], image.getArray()[i - 1]))
                i++;
            else {
                countSwaps++;
                SortUtils.swap(image.getArray(), i, i - 1);
                i--;

                if (countSwaps % delay == 0) {
                    writeFrame(encoder, image, userSettings);
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

    }

    @Override
    protected void calculateNumberOfSwaps(Tile[] array) {

        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        int i = 1;
        int n = tmp.length;
        while (i < n) {
            if (i == 0 || SortUtils.greater(tmp[i], tmp[i - 1])) {
                i++;
            } else {
                ++countSwaps;
                Tile temp = tmp[i];
                tmp[i] = tmp[i - 1];
                tmp[--i] = temp;
            }
        }
        resetCoordinates(userSettings, array);
    }

    @Override
    public void run() {
        sort();
    }
}
