package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.scene.image.ImageView;

import static JavaFXVersion.utilities.FileUtilities.writeImage;
import static JavaFXVersion.utilities.ImageUtilities.resetCoordinates;

public class InsertionSort extends AbstractSort {

    public InsertionSort(UserSettings userSettings) {
        super(userSettings);
    }

    @Override
    public void sort(ImageView imageView, Tile[] array, MainWindow mainWindow) {

        setupEnv(imageView, array);

        thread = new Thread(() -> {
            for (int i = 0; i < array.length; ++i) {

                int j = i;

                while (j > 0 && SortUtils.greater(array[j - 1], array[j]) && running) {
                    ++countComparison;
                    ++countSwaps;
                    SortUtils.swap(array, j, j - 1);
                    j = j - 1;
                    if((countSwaps % delay) == 0){
                        writeImage(userSettings, array, width, height, imageIndex++, countComparison, countSwaps,imageView.getFitWidth() / 150f);
                    }
                    progressBar.setProgress(progress+=increment);
                }
                ++countComparison;
                if(!running)
                    break;
            }
            runFFMPEG(array, imageView);
        });
        thread.start();
    }


    @Override
    protected void calculateNumberOfSwaps(Tile[] array) {
        Tile[] tmp = new Tile[array.length];
        System.arraycopy(array, 0, tmp, 0, array.length);

        for (int i = 0; i < tmp.length; ++i) {

            int j = i;

            while (j > 0 && SortUtils.greater(tmp[j - 1], tmp[j]) && running) {
                ++countSwaps;
                SortUtils.swap(tmp, j, j - 1);
                j = j - 1;
            }
            ++countComparison;
        }
        resetCoordinates(userSettings, array);
    }
}
