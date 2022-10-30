package JavaFXVersion.sorting;

import JavaFXVersion.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.SeekableByteChannel;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static JavaFXVersion.utilities.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.utilities.ImageUtilities.fillImage;

abstract public class AbstractSort implements SortAlgorithm {
    final UserSettings userSettings;
    final ImageView imageView;
    long countComparison = 0, imageIndex = 1, countSwaps = 0;
    boolean running = true;
    double progress = 0;
    double increment, delay = 1;
    TiledImage image;

    AlgorithmProgressBar algorithmProgressBar;
    AWTSequenceEncoder encoder;

    SeekableByteChannel out;

    public AbstractSort(UserSettings userSettings, TiledImage image, ImageView imageView,AWTSequenceEncoder encoder, SeekableByteChannel out, AlgorithmProgressBar algorithmProgressBar) {
        this.userSettings = userSettings;
        this.image = image;
        this.imageView = imageView;
        this.encoder = encoder;
        this.out = out;
        this.algorithmProgressBar = algorithmProgressBar;
    }

    @Override
    public void killTask() {
        running = false;
    }

    @Override
    public boolean isThreadAlive() {
        return running;
    }

    protected abstract void calculateNumberOfSwaps(Tile[] array);

    void setupEnv(ImageView imageView, Tile[] array) {

        running = true;

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
    void resumeProgram(ImageView imageView, MainWindow mainWindow, TiledImage image) {

        if (userSettings.isOpenFile()) {
            File out = new File(userSettings.getOutputDirectory() + "\\" + userSettings.getOutName());
            try {
                Desktop.getDesktop().open(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fillImage(userSettings, image, imageView, (int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        mainWindow.enableAll();
    }
}
