package JavaFXVersion.sorting;

import JavaFXVersion.FFMPEG;
import JavaFXVersion.MainWindow;
import JavaFXVersion.Tail;
import JavaFXVersion.UserSettings;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.FutureTask;

import static JavaFXVersion.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.FileUtilities.writeImage;

public class BubbleSort implements SortAlgorithm {
    //Random object used for lock the threads in this class
    //TODO Use the Lock class!
    private final UserSettings userSettings;
    Thread thread;
    int countComparison = 0, countSwaps = 0, imageIndex = 1;
    boolean running = true;

    public BubbleSort(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    @Override
    public void killTask() {
        running = false;
    }

    public boolean isThreadAlive() {
        return running;
    }

    @Override
    public void sort(Tail[] array, GridPane gridPane) {
        //We use a new thread to pause/resume its execution whenever we want
        running = true;
        deleteAllPreviousFiles(userSettings);
        calculateNumberOfSwaps(array);

        int delay = countSwaps / (userSettings.getFrameRate() * 15) + 1;
        countSwaps = 0;
        int width = (int) (array[0].getImage().getWidth() % 2 == 0 ? array[0].getImage().getWidth() :
                array[0].getImage().getWidth() - 1);
        int height = (int) (array[0].getImage().getHeight() % 2 == 0 ? array[0].getImage().getHeight() :
                array[0].getImage().getHeight() - 1);

        thread = new Thread(() -> {
            for (int size = array.length, i = 1; i < size; ++i) {

                if (running) {
                    boolean swapped = false;

                    for (int j = 0; j < size - i; ++j) {
                        countComparison++;
                        if (SortUtils.greater(array[j], array[j + 1])) {
                            countSwaps++;
                            SortUtils.swap(array, j, j + 1);
                            swapped = true;
                            if((countSwaps % delay) == 0)
                                writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
                        }
                    }
                    if (!swapped) {
                        break;
                    }
                }
            }
            writeImage(userSettings, array, width, height, imageIndex, countComparison, countSwaps);
            //?MAYBE JUST PASS userSettings?
            FFMPEG prc = new FFMPEG(userSettings.getFfmpegPath(), userSettings.getOutName(),
                    userSettings.getOutputDirectory(),
                    userSettings.getFrameRate(), userSettings.getMusic());
            deleteAllPreviousFiles(userSettings);
        });
        thread.start();
    }

    private void calculateNumberOfSwaps(Tail[] array) {
        Tail[] tmp = new Tail[array.length];
        System.arraycopy(array,0,tmp, 0, array.length);

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
    }

    private void createMediaView(GridPane gridPane) {
        Media media = new Media(new File(userSettings.getOutputDirectory().getAbsolutePath() + '\\' + userSettings.getOutName()).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView m = new MediaView(mediaPlayer);
        m.setMediaPlayer(mediaPlayer);
        m.setFitWidth(1200);
        BorderPane root = ((BorderPane) gridPane.getParent());
        root.setCenter(m);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        deleteAllPreviousFiles(userSettings);
    }

    @Override
    public <T extends Comparable<T>> T[] sort(T[] unsorted) {
        return null;
    }

    @Override
    public <T extends Comparable<T>> List<T> sort(List<T> unsorted) {
        return SortAlgorithm.super.sort(unsorted);
    }
}