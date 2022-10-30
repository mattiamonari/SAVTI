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
    Thread thread;
    long countComparison = 0, imageIndex = 1, countSwaps = 0;
    boolean running = true;
    ProgressBar progressBar;
    ProgressIndicator progressIndicator;
    HBox progressBox;
    double progress = 0;
    double increment, delay = 1;
    TiledImage image;

    AWTSequenceEncoder encoder;

    SeekableByteChannel out;

    public AbstractSort(UserSettings userSettings, TiledImage image, ImageView imageView,AWTSequenceEncoder encoder, SeekableByteChannel out) {
        this.userSettings = userSettings;
        this.image = image;
        this.imageView = imageView;
        this.encoder = encoder;
        this.out = out;
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

        progressBar = new ProgressBar(0);
        progressIndicator = new ProgressIndicator(0);
        progressBox = new HBox();
        increment = 1d / countSwaps;

        Platform.runLater(() -> {
            progressBox.getChildren().addAll(progressBar, progressIndicator);
            ((Group) imageView.getParent()).getChildren().add(progressBox);
            imageView.setVisible(false);
            imageView.setManaged(false);
            progressBar.setPrefWidth(1000);
            progressBar.setMinWidth(1000);
            progressBar.setPrefHeight(50);
            progressBar.setMinHeight(50);
            VBox.setMargin(progressBar, new Insets(10));
        });

        progressIndicator.progressProperty().bind(progressBar.progressProperty());

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

        imageView.setVisible(true);
        imageView.setManaged(true);
        ((Group) imageView.getParent()).getChildren().remove(progressBox);
        fillImage(userSettings, image, imageView, (int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        mainWindow.enableAll();
    }
}
