package savti.Command;

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
import savti.AlgorithmProgressBar;
import savti.TiledImage;
import savti.UserSettings;

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
    TiledImage image;
    UserSettings userSettings;
    SeekableByteChannel out;
    AWTSequenceEncoder encoder;
    @FXML
    ImageView imageView;
    AlgorithmProgressBar algorithmProgressBar;
    //I don't know if in this class is good to add all these buttons, review it!
    Button randomizeButton;

    //TODO constructor and comment
    @Override
    public void execute() {
        if (image.getImage() != null) {

            if (out == null || !out.isOpen()) {
                try {
                    new File(userSettings.getOutputDirectory().getPath()+ "\\" + userSettings.getOutName()).createNewFile();
                    out = NIOUtils.writableFileChannel(userSettings.getOutputDirectory().getAbsolutePath()+ "\\" + userSettings.getOutName());
                    encoder = new AWTSequenceEncoder(out, Rational.R(userSettings.getFrameRate(), 1));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            randomizeButton.setDisable(true);

            splitImage(image, userSettings.getColsNumber(), userSettings.getRowsNumber(), image);

            ableNodes(List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, burstMode, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()), List.of());
            ((Group) imageView.getParent()).getChildren().add(algorithmProgressBar);
            imageView.setVisible(false);
            imageView.setManaged(false);
            ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
            ListenableFuture<?> future = pool.submit(() -> rand(userSettings, image, encoder, algorithmProgressBar));
            //TODO CREATE METHOD
            future.addListener(() -> Platform.runLater(() -> {
                fillImageFromArray(image, imageView, (int) Math.round(this.getScene().getWidth() - ((VBox) cleanButton.getParent()).getWidth() - 20), (int) Math.round(((VBox) cleanButton.getParent()).getHeight() - 50));
                imageView.setVisible(true);
                imageView.setManaged(true);
                algorithmProgressBar.setProgress(0);
                ((Group) imageView.getParent()).getChildren().remove(algorithmProgressBar);
                ableNodes(List.of(), List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, burstMode, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()));
            }), MoreExecutors.directExecutor());
        }
    }
}
