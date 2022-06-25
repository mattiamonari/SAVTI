package JavaFXVersion.sorting;

import JavaFXVersion.FFMPEG;
import JavaFXVersion.Tail;
import JavaFXVersion.UserSettings;
import javafx.scene.layout.GridPane;

import static JavaFXVersion.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.FileUtilities.writeImage;

public class InsertionSort implements SortAlgorithm {

    private final UserSettings userSettings;
    Thread thread;
    int countComparison = 0, countSwaps = 0, imageIndex = 0;
    boolean running = true;

    public InsertionSort(UserSettings userSettings) {
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
        int delay = countSwaps / (userSettings.getFrameRate() * 15) + 1;
        countSwaps = 0;
        int width = (int) (array[0].getImage().getWidth() % 2 == 0 ? array[0].getImage().getWidth() :
                array[0].getImage().getWidth() - 1);
        int height = (int) (array[0].getImage().getHeight() % 2 == 0 ? array[0].getImage().getHeight() :
                array[0].getImage().getHeight() - 1);

        thread = new Thread(() -> {
            for (int i = 0; i < array.length; ++i) {

                int j = i;

                while (j > 0 && SortUtils.greater(array[j - 1], array[j]) && running == true) {
                    ++countComparison;
                    ++countSwaps;
                    SortUtils.swap(array, j, j - 1);
                    j = j - 1;
                    if((countSwaps % delay) == 0){
                        writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps);
                    }
                }
                ++countComparison;
                if(running == false)
                    break;
            }
            writeImage(userSettings, array, width, height, imageIndex, countComparison, countSwaps);

            FFMPEG prc = new FFMPEG(userSettings.getFfmpegPath(), userSettings.getOutName(),
                    userSettings.getOutputDirectory(),
                    userSettings.getFrameRate(), userSettings.getMusic());
            deleteAllPreviousFiles(userSettings);
        });
        thread.start();
    }

    @Override
    public <T extends Comparable<T>> T[] sort(T[] unsorted) {
        return null;
    }


    private void calculateNumberOfSwaps(Tail[] array) {
        Tail[] tmp = new Tail[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);

        for (int i = 0; i < tmp.length; ++i) {

            int j = i;

            while (j > 0 && SortUtils.greater(tmp[j - 1], tmp[j]) && running == true) {
                ++countSwaps;
                SortUtils.swap(tmp, j, j - 1);
                j = j - 1;
            }
            ++countComparison;
        }

    }
}
