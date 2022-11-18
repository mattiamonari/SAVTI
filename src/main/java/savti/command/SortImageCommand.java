package savti.command;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.SeekableByteChannel;
import savti.*;
import savti.sorting.*;
import savti.utilities.ErrorUtilities;

import java.util.List;
import java.util.concurrent.Executors;

import static savti.utilities.GUIUtilities.ableNodes;
import static savti.utilities.ImageUtilities.fillImageFromArray;
/**
 * SortImageCommand is used to create the command to sort the image after the shuffle.
 *
 * @author: Daniele Gasparini && Mattia Monari
 * @version: 2022.11.17
 */
public class SortImageCommand implements Command{
    final static String hoverButton = "-fx-background-color: #cd5c5c; \n-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);";

    MainVBox mainVBox;
    TiledImage image;
    AWTSequenceEncoder encoder;
    SeekableByteChannel out;
    UserSettings userSettings;
    ImageView imageView;
    SortAlgorithm algorithm;
    AlgorithmProgressBar algorithmProgressBar;

    MainMenu mainMenu;

    public SortImageCommand(MainVBox mainVBox, TiledImage image, AWTSequenceEncoder encoder, SeekableByteChannel out, UserSettings userSettings, ImageView imageView, SortAlgorithm algorithm, AlgorithmProgressBar algorithmProgressBar, MainMenu mainMenu) {
        this.mainVBox = mainVBox;
        this.image = image;
        this.encoder = encoder;
        this.out = out;
        this.userSettings = userSettings;
        this.imageView = imageView;
        this.algorithm = algorithm;
        this.algorithmProgressBar = algorithmProgressBar;
        this.mainMenu = mainMenu;
    }

    public SortAlgorithm choiche() {
        String choice = mainVBox.getChooseAlgo().getValue();
        switch (choice) {
            case "QuickSort" ->
                    algorithm = new QuickSort(userSettings, image, imageView, algorithmProgressBar, encoder, out);
            case "SelectionSort" ->
                    algorithm = new SelectionSort(userSettings, image, imageView, algorithmProgressBar, encoder, out);
            case "BubbleSort" ->
                    algorithm = new BubbleSort(userSettings, image, imageView, algorithmProgressBar, encoder, out);
            case "InsertionSort" ->
                    algorithm = new InsertionSort(userSettings, image, imageView, algorithmProgressBar, encoder, out);
            case "RadixSort" ->
                    algorithm = new RadixSort(userSettings, image, imageView, algorithmProgressBar, encoder, out);
            case "MergeSort" ->
                    algorithm = new MergeSort(userSettings, image, imageView, algorithmProgressBar, encoder, out);
            case "CocktailSort" ->
                    algorithm = new CocktailSort(userSettings, image, imageView, algorithmProgressBar, encoder, out);
            case "GnomeSort" ->
                    algorithm = new GnomeSort(userSettings, image, imageView, algorithmProgressBar, encoder, out);
            case "CycleSort" ->
                    algorithm = new CycleSort(userSettings, image, imageView, algorithmProgressBar, encoder, out);
            default -> ErrorUtilities.SWW();
        }
        return algorithm;
    }
    @Override
    public void execute() {
        if (checkSortingConditions()) {
            SortAlgorithm algorithm = choiche();
            mainVBox.disableOrEnableAll(true);
            ableNodes(List.of(mainMenu.getImageLoaderItem().getStyleableNode(), mainMenu.getSongLoaderItem().getStyleableNode()), List.of());
            ((Group) imageView.getParent()).getChildren().add(algorithmProgressBar);
            imageView.setVisible(false);
            imageView.setManaged(false);
            ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
            ListenableFuture<?> future = pool.submit(algorithm);
            //TODO CREATE METHOD
            future.addListener(() -> Platform.runLater(() -> {
                fillImageFromArray(image, imageView, (int) Math.round(imageView.getScene().getWidth() - mainVBox.getWidth() - 20), (int) Math.round(mainVBox.getHeight() - 50));
                imageView.setVisible(true);
                imageView.setManaged(true);
                ((Group) imageView.getParent()).getChildren().remove(algorithmProgressBar);
                mainVBox.disableOrEnableAll(false);
                ableNodes(List.of(), List.of(mainMenu.getImageLoaderItem().getStyleableNode(), mainMenu.getSongLoaderItem().getStyleableNode()));
            }), MoreExecutors.directExecutor());
        }
    }


    private boolean checkSortingConditions() {

        if (!userSettings.verifyOutputPath()) {
            mainVBox.getOutputButton().setStyle(hoverButton);
            ErrorUtilities.outputPath();
            return false;
        } else if (image == null || image.isArrayEmpty()) {
            ErrorUtilities.noImageError();
            return false;
        } else if (image.isAlreadyOrdere()) {
            ErrorUtilities.alreadyOrderedImage();
            return false;
        }
        return true;
    }
}
