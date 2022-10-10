package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.TiledImage;
import javafx.scene.image.ImageView;

public interface SortAlgorithm {

    /*********
     Methood that i added. Improve them!
     **********/
    void killTask();

    boolean isThreadAlive();

    void sort(ImageView imageView, TiledImage image, MainWindow mainWindow);
}