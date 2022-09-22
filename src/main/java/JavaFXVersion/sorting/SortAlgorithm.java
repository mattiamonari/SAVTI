package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.scene.image.ImageView;

import java.util.Random;

import static JavaFXVersion.sorting.SortUtils.swap;
import static JavaFXVersion.utilities.FileUtilities.deleteAllPreviousFiles;
import static JavaFXVersion.utilities.FileUtilities.writeImage;

public interface SortAlgorithm {
    static void rand(Tile[] array, UserSettings userSettings) {
        int delay = Math.max(2 * array.length / (userSettings.getFrameRate() * userSettings.getVideoDuration()), 1);
        // Creating object for Random class
        Random rd = new Random();
        deleteAllPreviousFiles(userSettings);
        int imageindex = 0;
        // Starting from the last element and swapping one by one.
        for (int i = array.length - 1; i > 0; i--) {
            if(i%delay == 0)
                writeImage(userSettings, array, (int) array[0].getWidth(), (int) array[0].getHeight(), "final" + imageindex++);

            // Pick a random index from 0 to i
            int j = rd.nextInt(i + 1);
            // Swap array[i] with the element at random index
            swap(array, i, j);
        }
        userSettings.setStartingImageIndex(imageindex);
    }
    /*********
     Methood that i added. Improve them!
     **********/
    void killTask();
    boolean isThreadAlive();
    void sort(ImageView imageView, Tile[] array, MainWindow mainWindow);
}