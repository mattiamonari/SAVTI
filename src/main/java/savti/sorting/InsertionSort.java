package savti.sorting;

import savti.AlgorithmProgressBar;
import savti.Tile;
import savti.TiledImage;
import savti.UserSettings;
import javafx.scene.image.ImageView;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;

import java.io.IOException;

import static savti.utilities.FileUtilities.writeFrame;
import static savti.utilities.FileUtilities.writeFreezedFrames;
import static savti.utilities.ImageUtilities.resetCoordinates;

public class InsertionSort extends AbstractSort {

    public InsertionSort(UserSettings userSettings, TiledImage image, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, AWTSequenceEncoder encoder, SeekableByteChannel out) {
        super(userSettings, image, imageView, algorithmProgressBar, encoder, out);
    }

    @Override
    public void sort() {

        setupEnv(image.getArray());

        for (int i = 0; i < image.getArray().length; ++i) {

            int j = i;

            ++countComparison;
            //IS THIS CORRECT?
            while (j > 0 && SortUtils.greater(image.getArray()[j - 1], image.getArray()[j])) {
                ++countSwaps;
                algorithmProgressBar.setProgress(progress += increment);
                SortUtils.swap(image.getArray(), j, j - 1);
                j = j - 1;

                if ((countSwaps % delay) == 0) {
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

        for (int i = 0; i < tmp.length; ++i) {

            int j = i;

            while (j > 0 && SortUtils.greater(tmp[j - 1], tmp[j])) {
                ++countSwaps;
                SortUtils.swap(tmp, j, j - 1);
                j = j - 1;
            }
            ++countComparison;
        }
        resetCoordinates(userSettings, array);
    }

    @Override
    public void run() {
        sort();
    }
}
