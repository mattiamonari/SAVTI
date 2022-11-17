package savti.Command;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
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
    public SortAlgorithm choiche() {
        String choice = chooseAlgo.getValue();
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
            return algorithm;
        }
    }
    //lascio fare a te qua tia che conosci il codice e sai come modificare nel caso, visto che qua chiami tutti i bottoni usati
    @Override
    public void execute() {
        SortAlgorithm algorithm = choiche();
        ableNodes(List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, burstMode, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()), List.of());
        ((Group) imageView.getParent()).getChildren().add(algorithmProgressBar);
        imageView.setVisible(false);
        imageView.setManaged(false);
        ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        ListenableFuture<?> future = pool.submit(algorithm);
        //TODO CREATE METHOD
        future.addListener(() -> Platform.runLater(() -> {
            fillImageFromArray(image, imageView, (int) Math.round(this.getScene().getWidth() - ((VBox) cleanButton.getParent()).getWidth() - 20), (int) Math.round(((VBox) cleanButton.getParent()).getHeight() - 50));
            imageView.setVisible(true);
            imageView.setManaged(true);
            ((Group) imageView.getParent()).getChildren().remove(algorithmProgressBar);
            ableNodes(List.of(), List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()));
        }), MoreExecutors.directExecutor());
    }

    }
}
