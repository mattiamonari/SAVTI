package savti.sorting;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import savti.*;

import static savti.utilities.FileUtilities.writeFrame;
import static savti.utilities.FileUtilities.writeFreezedFrames;
import static savti.utilities.ImageUtilities.resetCoordinates;

public class InsertionSort extends AbstractSort {

    public InsertionSort(UserSettings userSettings, TiledImage image, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, OutputHandler outputHandler) {
        super(userSettings, image, imageView, algorithmProgressBar, outputHandler);
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
                progress += increment;
                algorithmProgressBar.setProgress(progress);
                SortUtils.swap(image.getArray(), j, j - 1);
                j = j - 1;

                if ((countSwaps % delay) == 0) {
                    writeFrame(outputHandler, image, userSettings, countSwaps, countComparison, (int)(image.getImage().getWidth() / 100f));
                }
            }
        }

        writeFreezedFrames(userSettings.getFrameRate() * 2, outputHandler, image, userSettings, countSwaps, countComparison, (int) (image.getImage().getWidth() / 100f));
        outputHandler.closeOutputChannel();


        Platform.runLater(() -> resumeProgram(imageView, image));

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
