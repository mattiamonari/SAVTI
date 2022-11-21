package savti.sorting;

import javafx.scene.Group;
import savti.*;
import javafx.scene.image.ImageView;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.SeekableByteChannel;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static savti.utilities.ImageUtilities.fillImageFromArray;

abstract public class AbstractSort implements SortAlgorithm {
    final UserSettings userSettings;
    final ImageView imageView;
    long countComparison = 0, imageIndex = 1, countSwaps = 0;
    double progress = 0;
    double increment, delay = 1;
    TiledImage image;

    AlgorithmProgressBar algorithmProgressBar;
    AWTSequenceEncoder encoder;
    OutputHandler outputHandler;
    SeekableByteChannel out;

    public AbstractSort(UserSettings userSettings, TiledImage image, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, OutputHandler outputHandler) {
        this.userSettings = userSettings;
        this.image = image;
        this.imageView = imageView;
        this.outputHandler = outputHandler;
        this.algorithmProgressBar = algorithmProgressBar;
    }

    protected abstract void calculateNumberOfSwaps(Tile[] array);

    void setupEnv(Tile[] array) {

        algorithmProgressBar.setAlgoName(this.getClass().getSimpleName());

        calculateNumberOfSwaps(array);

        increment = 1d / countSwaps;

        delay = Math.max(countSwaps / ((long) userSettings.getFrameRate() * userSettings.getVideoDuration()), 1);

        countSwaps = 0;

        imageIndex = userSettings.getStartingImageIndex();

        //TODO
        /*
        if (!userSettings.getOutputDirectory().isDirectory())
            if (!userSettings.getOutputDirectory().mkdir())
                ErrorUtilities.SWW();
         */

    }

    //TODO SMARTER WAY
    void resumeProgram(ImageView imageView, TiledImage image) {

        if (userSettings.isOpenFile()) {
            File out = new File(userSettings.getOutputDirectory() + "\\" + userSettings.getOutName());
            try {
                Desktop.getDesktop().open(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fillImageFromArray(image, imageView, (int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        imageView.setVisible(true);
        imageView.setManaged(true);
        ((Group) imageView.getParent()).getChildren().remove(algorithmProgressBar);
    }
}
