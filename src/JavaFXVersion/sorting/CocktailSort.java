package JavaFXVersion.sorting;

import JavaFXVersion.FFMPEG;
import JavaFXVersion.Tail;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static JavaFXVersion.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.FileUtilities.writeImage;

public class CocktailSort implements SortAlgorithm {

    private final UserSettings userSettings;
    Thread thread;
    int countComparison = 0, countSwaps = 0, imageIndex = 0;
    boolean running = true;
    private ProgressBar progressBar;
    private double progress = 0;
    private double increment;

    public CocktailSort(UserSettings userSettings) {
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

    @Override
    public void sort(Tail[] array, GridPane gridPane) {
        running = true;
        deleteAllPreviousFiles(userSettings);
        calculateNumberOfSwaps(array);

        progressBar = new ProgressBar(0);
        increment = 1d / countSwaps;
        ((BorderPane) gridPane.getParent()).setBottom(progressBar);
        gridPane.setVisible(false);
        progressBar.setPrefWidth(gridPane.getWidth());
        progressBar.setMinWidth(gridPane.getWidth());
        progressBar.setPrefHeight(50);
        progressBar.setMinHeight(50);
        BorderPane.setAlignment(progressBar, Pos.CENTER);
        BorderPane.setMargin(progressBar, new Insets(0, 0, 10, 0));

        int delay = countSwaps / (userSettings.getFrameRate() * 15) + 1;
        countSwaps = 0;
        int width = (int) (array[0].getImage().getWidth() % 2 == 0 ? array[0].getImage().getWidth() :
                array[0].getImage().getWidth() - 1);
        int height = (int) (array[0].getImage().getHeight() % 2 == 0 ? array[0].getImage().getHeight() :
                array[0].getImage().getHeight() - 1);

        thread = new Thread(() -> {
            int n = array.length;
            int swap = 1;
            int beg = 0;
            int end = n - 1;
            int i;
            while (swap == 1) {
                swap = 0;  
  
/* Loop similar to bubble sort to compare and swap array elements starting   
  
from left to right */
                for (i = beg; i < end; ++i) {
                    if(running == false)
                        break;
                    ++countComparison;
                    if (SortUtils.greater(array[i], array[i + 1])) {
                        ++countSwaps;
                        SortUtils.swap(array, i, i + 1);
                        if ((countSwaps % delay) == 0)
                            writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
                        progressBar.setProgress(progress += increment);
                        swap = 1;
                    }
                }

                if (swap == 0)
                    break;

                swap = 0;  
          
        /* decrease the 'end' point by one position.   
        It is because the item at the last position is at its   
  
correct position */
                --end;  
           
        /* This loop starts from right to left to perform the   
  
same comparison as in the previous loop */
                for (i = end - 1; i >= beg; --i) {
                    if(running == false)
                        break;
                    ++countComparison;
                    if (SortUtils.greater(array[i], array[i + 1])) {
                        ++countSwaps;
                        SortUtils.swap(array, i, i + 1);
                        if ((countSwaps % delay) == 0)
                            writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
                        progressBar.setProgress(progress += increment);
                        swap = 1;
                    }
                }  
  
        /* increase the beg point by one position.   
        It is because the item at the first position is at its   
correct position */
                ++beg;
            }
            writeImage(userSettings, array, width, height, imageIndex, countComparison, countSwaps);
            //?MAYBE JUST PASS userSettings?
            FFMPEG prc = new FFMPEG(userSettings.getFfmpegPath(), userSettings.getOutName(),
                    userSettings.getOutputDirectory(),
                    userSettings.getFrameRate(), userSettings.getMusic());
            deleteAllPreviousFiles(userSettings);

            if (userSettings.isOpenFile()) {
                File out = new File(userSettings.getOutputDirectory() + "\\" + userSettings.getOutName());
                try {
                    Desktop.getDesktop().open(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(() -> {
                gridPane.setVisible(true);
                ((BorderPane) gridPane.getParent()).getChildren().remove(progressBar);
            });
        });

        thread.start();
    }

    private void calculateNumberOfSwaps(Tail[] array) {
        Tail[] tmp = new Tail[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);

        int n = tmp.length;
        int swap = 1;
        int beg = 0;
        int end = n - 1;
        int i;
        while (swap == 1) {
            swap = 0;

/* Loop similar to bubble sort to compare and swap array elements starting

from left to right */
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

        /* decrease the 'end' point by one position.
        It is because the item at the last position is at its

correct position */
            --end;

        /* This loop starts from right to left to perform the

same comparison as in the previous loop */
            for (i = end - 1; i >= beg; --i) {
                if (SortUtils.greater(tmp[i], tmp[i + 1])) {
                    ++countSwaps;
                    SortUtils.swap(tmp, i, i + 1);
                    swap = 1;
                }
            }

        /* increase the beg point by one position.
        It is because the item at the first position is at its
correct position */
            ++beg;
        }
    }

    @Override
    public <T extends Comparable<T>> T[] sort(T[] unsorted) {
        return null;
    }
}
