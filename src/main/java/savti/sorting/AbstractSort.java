package savti.sorting;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import savti.*;
import savti.utilities.ErrorUtilities;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static savti.utilities.ImageUtilities.fillImageFromArray;

public abstract class AbstractSort implements SortAlgorithm {
    final UserSettings userSettings;
    final ImageView imageView;
    long countComparison = 0;
    int countSwaps = 0;
    double progress = 0;
    double increment;
    double delay = 1;
    TiledImage image;

    AlgorithmProgressBar algorithmProgressBar;
    OutputHandler outputHandler;

    protected AbstractSort(UserSettings userSettings, TiledImage image, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, OutputHandler outputHandler) {
        this.userSettings = userSettings;
        this.image = image;
        this.imageView = imageView;
        this.outputHandler = outputHandler;
        this.algorithmProgressBar = algorithmProgressBar;
    }

    protected abstract void calculateNumberOfSwaps(Tile[] array);

    void setupEnv(Tile[] array) {

        algorithmProgressBar.setAlgoName(this.getClass().getSimpleName());

        calculateNumberOfSwaps(array);

        increment = 1d / countSwaps;

        delay = Math.max(countSwaps / ((long) userSettings.getFrameRate() * userSettings.getVideoDuration()), 1);

        countSwaps = 0;

        //------------TODO NOT IN HERE!!!---------------- check condition
        if (!userSettings.getOutputDirectory().isDirectory() && !userSettings.getOutputDirectory().mkdir())
            ErrorUtilities.somethingWentWrong();
    }

    //TODO SMARTER WAY
    void resumeProgram(ImageView imageView, TiledImage image) {

        if (userSettings.isOpenFile()) {
            File out = new File(userSettings.getOutputDirectory() + File.separator + userSettings.getOutName());
            try {
                Desktop.getDesktop().open(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fillImageFromArray(image, imageView, (int) imageView.getFitWidth(), (int) imageView.getFitHeight());
        imageView.setVisible(true);
        imageView.setManaged(true);
        ((Group) imageView.getParent()).getChildren().remove(algorithmProgressBar);
    }
}
