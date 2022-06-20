package JavaFXVersion.sorting;

import JavaFXVersion.FFMPEG;
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
import java.io.IOException;
import java.util.concurrent.FutureTask;

import static JavaFXVersion.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.FileUtilities.writeImage;
import static JavaFXVersion.sorting.SortUtils.*;

public class QuickSort implements SortAlgorithm {


    private final UserSettings userSettings;
    FutureTask<Void> future;
    Thread thread;
    int i = 0;
    int countSwaps, countComparison;

    public QuickSort(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    public boolean isThreadAlive(){
        if(thread != null){
            return thread.isAlive();
        }
        return false;
    }

    @Override
    public void killTask() {
        if (thread != null) {
            if (thread.isAlive()) {
                thread.interrupt();
                future.cancel(true);
            }
        }
    }

    @Override
    public void sort(Tail[] array , GridPane gridPane) {
        deleteAllPreviousFiles(userSettings);
        thread = new Thread(() -> {
            long start = System.nanoTime();
            doSort(array,0,array.length - 1, gridPane);
            long end = System.nanoTime();
            System.out.println(Math.floorDiv(end-start, 1000000));
            new FFMPEG(userSettings.getFfmpegPath() , userSettings.getOutName() , userSettings.getOutputDirectory(),
                    30);
            Platform.runLater(() -> {
                createMediaView(gridPane);
            });
        });
        thread.start();
    }

    /**
     * This method implements the Generic Quick Sort
     *
     * @param array The array to be sorted Sorts the array in increasing order
     */
    @Override
    public <T extends Comparable<T>> T[] sort(T[] array) {
        return array;
    }


    private <T extends Comparable<T>> void doSort(Tail[] array, int left, int right, GridPane gridPane) {
        countComparison++;
        if (left < right) {
            int pivot = randomPartition(array, left, right, gridPane);
            int width = (int) ( array[0].getImage().getWidth() % 2 == 0 ? array[0].getImage().getWidth() :
                                array[0].getImage().getWidth() - 1);
            int height = (int) (  array[0].getImage().getHeight() % 2 == 0 ?  array[0].getImage().getHeight() :
                    array[0].getImage().getHeight() - 1);
            writeImage(userSettings, array , width , height, i++);
            doSort(array, left, pivot - 1, gridPane);
            writeImage(userSettings, array , width , height, i++);
            doSort(array, pivot, right, gridPane);
        }
    }

    /**
     * Ramdomize the array to avoid the basically ordered sequences
     *
     * @param array The array to be sorted
     * @param left The first index of an array
     * @param right The last index of an array
     * @return the partition index of the array
     */
    private <T extends Comparable<T>> int randomPartition(Tail[] array, int left, int right, GridPane gridPane) {
        int randomIndex = left + (int) (Math.random() * (right - left + 1));

        countSwaps++;
        swap(array, randomIndex, right);

        return partition(array, left, right, gridPane);
    }

    /**
     * This method finds the partition index for an array
     *
     * @param array The array to be sorted
     * @param left The first index of an array
     * @param right The last index of an array Finds the partition index of an
     * array
     */
    private <T extends Comparable<T>> int partition(Tail[] array, int left, int right, GridPane gridPane) {
        int mid = (left + right) >>> 1;
        Tail pivot = array[mid];

        while (left <= right) {
            countComparison++;
            while (less(array[left], pivot)) {
                countComparison++;
                ++left;
            }
            while (less(pivot, array[right])) {
                countComparison++;
                --right;
            }
            countComparison++;
            if (left <= right) {
                int finalLeft = left;
                int finalRight = right;

                countSwaps++;
                swap(array, left, right);
                ++left;
                --right;
            }
        }
        return left;
    }

    private void createMediaView(GridPane gridPane) {
        Media media = new Media(new File(userSettings.getOutputDirectory().getAbsolutePath() + '\\' + userSettings.getOutName()).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView m = new MediaView(mediaPlayer);
        m.setMediaPlayer(mediaPlayer);
        m.setFitWidth(1000);
        BorderPane root = ((BorderPane) gridPane.getParent());
        root.setCenter(m);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        deleteAllPreviousFiles(userSettings);
    }

}