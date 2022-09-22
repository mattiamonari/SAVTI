package JavaFXVersion.sorting;

import JavaFXVersion.FFMPEG;
import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static JavaFXVersion.utilities.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.utilities.FileUtilities.writeImage;
import static JavaFXVersion.utilities.ImageUtilities.fillImage;

abstract public class AbstractSort implements SortAlgorithm{
    final UserSettings userSettings;
    Thread thread;
    long countComparison = 0, imageIndex = 1, countSwaps = 0;
    boolean running = true;
    ProgressBar progressBar;
    ProgressIndicator progressIndicator;
    HBox progressBox;
    double progress = 0;
    double increment, delay = 1;
    int width;
    int height;

    public AbstractSort(UserSettings userSettings) {
        this.userSettings = userSettings;
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
        progressBox.getChildren().addAll(progressBar, progressIndicator);
        ((Group) imageView.getParent()).getChildren().add(progressBox);
        imageView.setVisible(false);
        imageView.setManaged(false);
        progressBar.setPrefWidth(1000);
        progressBar.setMinWidth(1000);
        progressBar.setPrefHeight(50);
        progressBar.setMinHeight(50);
        VBox.setMargin(progressBar, new Insets(10));

        progressIndicator.progressProperty().bind(progressBar.progressProperty());

        delay = Math.max(countSwaps / ((long) userSettings.getFrameRate() * userSettings.getVideoDuration()), 1);
        countSwaps = 0;
        width = (int) Math.max(array[0].getWidth(), 1);
        height = (int) Math.max(array[0].getHeight() , 1);

        imageIndex = userSettings.getStartingImageIndex();
    }

    void runFFMPEG(Tile[] array, ImageView imageView){
        writeImage(userSettings, array, width, height, imageIndex, countComparison, countSwaps, imageView.getImage().getWidth() / 125f);
        new FFMPEG(userSettings, progressBar);
        if (!userSettings.saveImage)
            deleteAllPreviousFiles(userSettings);

        if(userSettings.isOpenFile()) {
            File out = new File(userSettings.getOutputDirectory() + "\\" + userSettings.getOutName());
            try {
                Desktop.getDesktop().open(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //TODO SMARTER WAY
    void resumeProgram(ImageView imageView, MainWindow mainWindow, Tile[] array){
        imageView.setVisible(true);
        imageView.setManaged(true);
        ((Group) imageView.getParent()).getChildren().remove(progressBox);
        fillImage(userSettings, array, imageView, (int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        mainWindow.enableAll();
    }
}
