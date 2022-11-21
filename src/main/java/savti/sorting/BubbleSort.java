package savti.sorting;

import javafx.application.Platform;
import savti.*;
import savti.utilities.ErrorUtilities;
import javafx.scene.image.ImageView;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static savti.utilities.FileUtilities.writeFrame;
import static savti.utilities.FileUtilities.writeFreezedFrames;
import static savti.utilities.ImageUtilities.resetCoordinates;

public class BubbleSort extends AbstractSort {

    public BubbleSort(UserSettings userSettings, TiledImage image, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, OutputHandler outputHandler) {
        super(userSettings, image, imageView, algorithmProgressBar,outputHandler);
    }

    @Override
    public void sort() {
        //We use a new thread to pause/resume its execution whenever we want

        setupEnv(image.getArray());

        //------------TODO NOT IN HERE!!!----------------
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
                    algorithmProgressBar.setProgress(progress += increment);
                    SortUtils.swap(image.getArray(), j, j + 1);
                    swapped = true;

                    /*      FRAMEWRITING SECTION     */
                    if (countSwaps % delay == 0)
                       writeFrame(outputHandler,image,userSettings,countSwaps,countComparison,10);

                }
            }
            if (!swapped) {
                break;
            }
        }

        writeFreezedFrames(userSettings.getFrameRate() * 2, outputHandler, image, userSettings, countSwaps, countComparison, (int) (imageView.getFitWidth() / 150f));
        outputHandler.closeOutputChannel();

        //TODO WHY I DON'T USE IT?
        Platform.runLater(() -> resumeProgram(imageView, image));
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

    @Override
    public void run() {
        sort();
    }
}