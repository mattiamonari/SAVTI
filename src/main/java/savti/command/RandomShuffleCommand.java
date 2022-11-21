package savti.command;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import savti.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

import static savti.sorting.SortUtils.rand;
import static savti.utilities.GUIUtilities.ableNodes;
import static savti.utilities.ImageUtilities.fillImageFromArray;
import static savti.utilities.ImageUtilities.splitImage;
/**
 * RandomShuffleCommand is used to create the command to shuffle the image.
 *
 * @author: Daniele Gasparini && Mattia Monari
 * @version: 2022.11.17
 */

public class RandomShuffleCommand implements Command{
    OutputHandler outputHandler;
    TiledImage image;
    UserSettings userSettings;

    ImageView imageView;
    AlgorithmProgressBar algorithmProgressBar;
    MainVBox mainVBox;

    public RandomShuffleCommand(TiledImage image, UserSettings userSettings, OutputHandler outputHandler, ImageView imageView, AlgorithmProgressBar algorithmProgressBar, MainVBox mainVBox) {
        this.image = image;
        this.userSettings = userSettings;
        this.outputHandler = outputHandler;
        this.imageView = imageView;
        this.algorithmProgressBar = algorithmProgressBar;
        this.mainVBox = mainVBox;
    }

    //TODO constructor and comment
    @Override
    public void execute() {
        if (image.getImage() != null) {

            outputHandler.initializeHandler(userSettings.getOutputDirectory().getPath(), userSettings.getOutName(), userSettings.getFrameRate());

            mainVBox.disableOrEnableAll(true);

            splitImage(image, userSettings.getColsNumber(), userSettings.getRowsNumber(), image);

            ((Group) imageView.getParent()).getChildren().add(algorithmProgressBar);
            imageView.setVisible(false);
            imageView.setManaged(false);
            ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
            ListenableFuture<?> future = pool.submit(() -> rand(userSettings, image, outputHandler, algorithmProgressBar));
            //TODO CREATE METHOD
            future.addListener(() -> Platform.runLater(() -> {
                fillImageFromArray(image, imageView, (int) Math.round(imageView.getScene().getWidth() - mainVBox.getWidth() - 20), (int) Math.round(mainVBox.getHeight() - 30));
                imageView.setVisible(true);
                imageView.setManaged(true);
                algorithmProgressBar.setProgress(0);
                ((Group) imageView.getParent()).getChildren().remove(algorithmProgressBar);
                mainVBox.disableOrEnableAll(false);
            }), MoreExecutors.directExecutor());
        }
    }
}
