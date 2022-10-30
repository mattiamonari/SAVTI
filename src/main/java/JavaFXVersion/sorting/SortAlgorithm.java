package JavaFXVersion.sorting;

import JavaFXVersion.MainWindow;
import JavaFXVersion.TiledImage;
import javafx.scene.image.ImageView;

public interface SortAlgorithm extends Runnable {

    /*********
     Methood that i added. Improve them!
     **********/
    void killTask();

    boolean isThreadAlive();

    void sort();
}