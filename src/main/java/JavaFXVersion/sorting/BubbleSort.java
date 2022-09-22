package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import static JavaFXVersion.utilities.FileUtilities.writeImage;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class BubbleSort extends AbstractSort{
    //Random object used for lock the threads in this class

    public BubbleSort(UserSettings userSettings) {
        super(userSettings);
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
    public void sort(ImageView imageView, Tile[] array, MainWindow mainWindow) {
        //We use a new thread to pause/resume its execution whenever we want

        setupEnv(imageView, array);

        thread = new Thread(() -> {
            thread.setPriority(Thread.MAX_PRIORITY);

            writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps, imageView.getImage().getWidth() / 100f);

            for (int size = array.length, i = 1; i < size; ++i) {

                if (running) {
                    boolean swapped = false;

                    for (int j = 0; j < size - i; ++j) {
                        countComparison++;
                        if (SortUtils.greater(array[j], array[j + 1])) {
                            countSwaps++;
                            SortUtils.swap(array, j, j + 1);
                            swapped = true;
                            if (countSwaps % delay == 0)
                                writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps,imageView.getImage().getWidth() / 100f);

                            progressBar.setProgress(progress += increment);
                        }
                    }
                    if (!swapped) {
                        break;
                    }
                }
            }
            runFFMPEG(array, imageView);
            Platform.runLater(() -> resumeProgram(imageView, mainWindow, array));
        });
        thread.start();
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
}