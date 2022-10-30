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

public class CocktailSort extends AbstractSort {

    public CocktailSort(UserSettings userSettings, TiledImage image, ImageView imageView, AWTSequenceEncoder encoder, SeekableByteChannel out, AlgorithmProgressBar algorithmProgressBar) {
        super(userSettings, image, imageView, encoder, out, algorithmProgressBar);
    }

    @Override
    public void sort() {

        Platform.runLater(() -> setupEnv(imageView, image.getArray()));

        int n = image.getArray().length;
        int swap = 1;
        int beg = 0;
        int end = n - 1;
        int i;
        while (swap == 1) {
            swap = 0;
            for (i = beg; i < end; ++i) {
                if (!running)
                    break;
                ++countComparison;

                if (SortUtils.greater(image.getArray()[i], image.getArray()[i + 1])) {
                    ++countSwaps;
                    SortUtils.swap(image.getArray(), i, i + 1);

                    if (countSwaps % delay == 0)
                        writeFrame(encoder, image, userSettings);
                    swap = 1;
                }
            }

            if (swap == 0)
                break;

            swap = 0;
            --end;

            for (i = end - 1; i >= beg; --i) {
                if (!running)
                    break;
                ++countComparison;
                if (SortUtils.greater(image.getArray()[i], image.getArray()[i + 1])) {
                    ++countSwaps;
                    SortUtils.swap(image.getArray(), i, i + 1);
                    if (countSwaps % delay == 0)
                        writeFrame(encoder, image, userSettings);
                    swap = 1;
                }
            }
            ++beg;
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

        int n = tmp.length;
        int swap = 1;
        int beg = 0;
        int end = n - 1;
        int i;
        while (swap == 1) {
            swap = 0;
            for (i = beg; i < end; ++i) {
                if (SortUtils.greater(tmp[i], tmp[i + 1])) {
                    ++countSwaps;
                    SortUtils.swap(tmp, i, i + 1);
                    swap = 1;
                }
            }

            if (swap == 0)
                break;

            swap = 0;
            --end;
            for (i = end - 1; i >= beg; --i) {
                if (SortUtils.greater(tmp[i], tmp[i + 1])) {
                    ++countSwaps;
                    SortUtils.swap(tmp, i, i + 1);
                    swap = 1;
                }
            }
            ++beg;
        }
        resetCoordinates(userSettings, array);
    }

    @Override
    public void run() {
        sort();
    }
}

