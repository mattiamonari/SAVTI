package savti.sorting;

import javafx.application.Platform;
import savti.*;
import javafx.scene.image.ImageView;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;

import java.io.IOException;

import static savti.utilities.FileUtilities.writeFrame;
import static savti.utilities.FileUtilities.writeFreezedFrames;
import static savti.utilities.ImageUtilities.resetCoordinates;

public class CocktailSort extends AbstractSort {

    public CocktailSort(UserSettings userSettings, TiledImage image, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, OutputHandler outputHandler) {
        super(userSettings, image, imageView, algorithmProgressBar,outputHandler);
    }

    @Override
    public void sort() {

        setupEnv(image.getArray());

        int n = image.getArray().length;
        int swap = 1;
        int beg = 0;
        int end = n - 1;
        int i;
        while (swap == 1) {
            swap = 0;
            for (i = beg; i < end; ++i) {
                ++countComparison;

                if (SortUtils.greater(image.getArray()[i], image.getArray()[i + 1])) {
                    ++countSwaps;
                    algorithmProgressBar.setProgress(progress += increment);
                    SortUtils.swap(image.getArray(), i, i + 1);

                    if (countSwaps % delay == 0)
                       writeFrame(outputHandler,image,userSettings,countSwaps,countComparison,10);
                    swap = 1;
                }
            }

            if (swap == 0)
                break;

            swap = 0;
            --end;

            for (i = end - 1; i >= beg; --i) {
                ++countComparison;
                if (SortUtils.greater(image.getArray()[i], image.getArray()[i + 1])) {
                    ++countSwaps;
                    algorithmProgressBar.setProgress(progress += increment);
                    SortUtils.swap(image.getArray(), i, i + 1);
                    if (countSwaps % delay == 0)
                       writeFrame(outputHandler,image,userSettings,countSwaps,countComparison,10);
                    swap = 1;
                }
            }
            ++beg;
        }

        writeFreezedFrames(userSettings.getFrameRate() * 2, outputHandler, image, userSettings, countSwaps, countComparison, (int) (imageView.getFitWidth() / 150f));
        outputHandler.closeOutputChannel();        outputHandler.closeOutputChannel();


        Platform.runLater(() -> resumeProgram(imageView, image));
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

