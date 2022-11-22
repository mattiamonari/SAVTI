package savti.command;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.SeekableByteChannel;
import savti.*;
import savti.sorting.*;
import savti.utilities.ErrorUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static savti.sorting.SortUtils.rand;
import static savti.utilities.GUIUtilities.ableNodes;
import static savti.utilities.ImageUtilities.splitImage;
/**
 * BurstModeSortingCommand is used to sort image using burst mode.
 *
 * @author Daniele Gasparini && Mattia Monari
 * @version 2022.11.22
 */
public class BurstModeSortingCommand implements Command{

    MainVBox mainVBox;
    TiledImage image;
    AWTSequenceEncoder encoder;
    SeekableByteChannel out;
    UserSettings userSettings;
    ImageView imageView;
    OutputHandler outputHandler;
    MainMenu mainMenu;
    SortAlgorithm sortAlgorithm = null;
    public BurstModeSortingCommand(MainVBox mainVBox, TiledImage image, UserSettings userSettings, ImageView imageView, MainMenu mainMenu) {
        this.mainVBox = mainVBox;
        this.image = image;
        this.userSettings = userSettings;
        this.imageView = imageView;
        this.mainMenu = mainMenu;
        this.outputHandler = new OutputHandler();
    }


    @Override
    public void execute() {
        new Thread(() -> {

            VBox progessBarContainer = new VBox();
            ObservableList<SortAlgorithm> algoList = FXCollections.observableList(new ArrayList<>());
            TiledImage cloned;
            ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8));

            setupEnvForBurstMode(progessBarContainer, algoList);

            for (String s : mainVBox.getChooseAlgo().getItems()) {

                outputHandler.initializeHandler(userSettings.getOutputDirectory().getPath(), s, userSettings.getFrameRate());

                AlgorithmProgressBar algorithmProgressBar = new AlgorithmProgressBar("AlgoName");


                splitImage(image, userSettings.getColsNumber(), userSettings.getRowsNumber(), image);

                rand(userSettings, image, outputHandler, algorithmProgressBar);

                try {
                    cloned = image.clone();
                } catch (CloneNotSupportedException ex) {
                    throw new RuntimeException(ex);
                }
                //Controlla se puoi mettere algorithmProgressBar come field al posto che passarla ad ogni iterazione
                sortAlgorithm = choice(algorithmProgressBar);

                createFutureTaskForBurstAlgo(pool, progessBarContainer, algoList, algorithmProgressBar, sortAlgorithm);

                //TODO Encoder + Out ? Why not? Or even moving to xuggle??


                //TODO CHANGE HOW THE BURSTMODE WORKS
            }

        }).start();
    }
    public SortAlgorithm choice(AlgorithmProgressBar algorithmProgressBar) {
        String choice = mainVBox.getChooseAlgo().getValue();
        SortAlgorithm algorithm = null;
        switch (choice) {
            case "QuickSort" ->
                    algorithm = new QuickSort(userSettings, image, imageView, algorithmProgressBar,outputHandler);
            case "SelectionSort" ->
                    algorithm = new SelectionSort(userSettings, image, imageView, algorithmProgressBar,outputHandler);
            case "BubbleSort" ->
                    algorithm = new BubbleSort(userSettings, image, imageView, algorithmProgressBar,outputHandler);
            case "InsertionSort" ->
                    algorithm = new InsertionSort(userSettings, image, imageView, algorithmProgressBar,outputHandler);
            case "RadixSort" ->
                    algorithm = new RadixSort(userSettings, image, imageView, algorithmProgressBar,outputHandler);
            case "MergeSort" ->
                    algorithm = new MergeSort(userSettings, image, imageView, algorithmProgressBar,outputHandler);
            case "CocktailSort" ->
                    algorithm = new CocktailSort(userSettings, image, imageView, algorithmProgressBar,outputHandler);
            case "GnomeSort" ->
                    algorithm = new GnomeSort(userSettings, image, imageView, algorithmProgressBar,outputHandler);
            case "CycleSort" ->
                    algorithm = new CycleSort(userSettings, image, imageView, algorithmProgressBar,outputHandler);
            default -> ErrorUtilities.SWW();
        }
        return algorithm;
    }

    private void setupEnvForBurstMode(VBox progessBarContainer, ObservableList<SortAlgorithm> algoList) {
        mainVBox.disableOrEnableAll(true);
        ableNodes(List.of(mainMenu.getImageLoaderItem().getStyleableNode(), mainMenu.getSongLoaderItem().getStyleableNode()), List.of());
        Platform.runLater(() -> ((Group) imageView.getParent()).getChildren().add(progessBarContainer));
        imageView.setVisible(false);
        imageView.setManaged(false);
        algoList.addListener((ListChangeListener<SortAlgorithm>) c -> {
            if (c.getList().size() == 0) {
                imageView.setVisible(true);
                imageView.setManaged(true);
                Platform.runLater(() -> ((Group) imageView.getParent()).getChildren().remove(progessBarContainer));
            }
        });
    }


    //TODO I DON'T LIKE IT
    private void createFutureTaskForBurstAlgo(ListeningExecutorService pool, VBox progessBarContainer, ObservableList<SortAlgorithm> algoList, AlgorithmProgressBar algorithmProgressBar, SortAlgorithm sortAlgorithm) {
        ListenableFuture<SortAlgorithm> future = (ListenableFuture<SortAlgorithm>) pool.submit(sortAlgorithm);
        algoList.add(sortAlgorithm);
        Platform.runLater(() -> progessBarContainer.getChildren().add(algorithmProgressBar));

        future.addListener(() -> {
            algoList.remove(sortAlgorithm);
            Platform.runLater(() -> {
                progessBarContainer.getChildren().remove(algorithmProgressBar);
                mainVBox.disableOrEnableAll(false);
                ableNodes(List.of(), List.of(mainMenu.getImageLoaderItem().getStyleableNode(), mainMenu.getSongLoaderItem().getStyleableNode()));
            });
        }, MoreExecutors.directExecutor());
    }


}
