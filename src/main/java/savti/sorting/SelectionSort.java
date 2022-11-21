package savti.sorting;

import savti.*;
import javafx.scene.image.ImageView;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;

import java.io.IOException;

import static savti.utilities.FileUtilities.writeFrame;
import static savti.utilities.FileUtilities.writeFreezedFrames;
import static savti.utilities.ImageUtilities.resetCoordinates;

public class SelectionSort extends AbstractSort {
    public SelectionSort(UserSettings userSettings, TiledImage image, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, OutputHandler outputHandler) {
        super(userSettings, image, imageView, algorithmProgressBar,outputHandler);
    }

    @Override
    public void sort() {

        setupEnv(image.getArray());

        int size = image.getArray().length;
        for (int step = 0; step < size - 1; step++) {
            int min_idx = step;
            for (int k = step + 1; k < size; k++) {
                // To sort in descending order, change > to < in this line.
                // Select the minimum element in each loop.
                ++countComparison;
                if (SortUtils.greater(image.getArray()[min_idx], image.getArray()[k])) {
                    min_idx = k;
                }
            }
            // put min at the correct position
            SortUtils.swap(image.getArray(), step, min_idx);
            ++countSwaps;
            algorithmProgressBar.setProgress(progress += increment);
            if (countSwaps % delay == 0) {
                writeFrame(encoder, image, userSettings);
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
    public void calculateNumberOfSwaps(Tile[] array) {
        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);
        int size = tmp.length;
        for (int step = 0; step < size - 1; step++) {
            int min_idx = step;
            for (int k = step + 1; k < size; k++) {
                if (SortUtils.greater(tmp[min_idx], tmp[k])) {
                    min_idx = k;
                }
            }
            // put min at the correct position
            SortUtils.swap(tmp, step, min_idx);
            ++countSwaps;
        }
        resetCoordinates(userSettings, array);
    }

    @Override
    public void run() {
        sort();
    }
}
