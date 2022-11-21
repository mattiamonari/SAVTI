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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static savti.sorting.SortUtils.rand;
import static savti.utilities.GUIUtilities.ableNodes;
import static savti.utilities.ImageUtilities.splitImage;

public class BurstModeSortingCommand implements Command{

    MainVBox mainVBox;
    TiledImage image;
    AWTSequenceEncoder encoder;
    SeekableByteChannel out;
    UserSettings userSettings;
    ImageView imageView;

    MainMenu mainMenu;

    public BurstModeSortingCommand(MainVBox mainVBox, TiledImage image, UserSettings userSettings, ImageView imageView, MainMenu mainMenu) {
        this.mainVBox = mainVBox;
        this.image = image;
        this.userSettings = userSettings;
        this.imageView = imageView;
        this.mainMenu = mainMenu;
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

                OutputHandler outputHandler = new OutputHandler();

                outputHandler.initializeHandler(userSettings.getOutputDirectory().getPath(), s, userSettings.getFrameRate());

                AlgorithmProgressBar algorithmProgressBar = new AlgorithmProgressBar("AlgoName");
                SortAlgorithm sortAlgorithm = null;

                splitImage(image, userSettings.getColsNumber(), userSettings.getRowsNumber(), image);

                rand(userSettings, image, outputHandler, algorithmProgressBar);

                try {
                    cloned = image.clone();
                } catch (CloneNotSupportedException ex) {
                    throw new RuntimeException(ex);
                }

                switch (s) {
                    case "QuickSort" ->
                            sortAlgorithm = new QuickSort(userSettings, cloned, imageView, algorithmProgressBar,outputHandler);
                    case "BubbleSort" ->
                            sortAlgorithm = new BubbleSort(userSettings, cloned, imageView, algorithmProgressBar,outputHandler);
                    case "SelectionSort" ->
                            sortAlgorithm = new SelectionSort(userSettings, cloned, imageView, algorithmProgressBar,outputHandler);
                    case "InsertionSort" ->
                            sortAlgorithm = new InsertionSort(userSettings, cloned, imageView, algorithmProgressBar,outputHandler);
                    case "RadixSort" ->
                            sortAlgorithm = new RadixSort(userSettings, cloned, imageView, algorithmProgressBar,outputHandler);
                    case "MergeSort" ->
                            sortAlgorithm = new MergeSort(userSettings, cloned, imageView, algorithmProgressBar,outputHandler);
                    case "CocktailSort" ->
                            sortAlgorithm = new CocktailSort(userSettings, cloned, imageView, algorithmProgressBar,outputHandler);
                    case "GnomeSort" ->
                            sortAlgorithm = new GnomeSort(userSettings, cloned, imageView, algorithmProgressBar,outputHandler);
                    case "CycleSort" ->
                            sortAlgorithm = new CycleSort(userSettings, cloned, imageView, algorithmProgressBar,outputHandler);
                }

                createFutureTaskForBurstAlgo(pool, progessBarContainer, algoList, algorithmProgressBar, sortAlgorithm);

                //TODO Encoder + Out ? Why not? Or even moving to xuggle??


                //TODO CHANGE HOW THE BURSTMODE WORKS
            }

        }).start();
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
