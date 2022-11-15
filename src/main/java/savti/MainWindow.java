package savti;

import savti.sorting.*;
import savti.utilities.ErrorUtilities;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.controlsfx.control.ToggleSwitch;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import static savti.sorting.SortUtils.rand;
import static savti.utilities.FileUtilities.deleteAllPreviousFiles;
import static savti.utilities.GUIUtilities.ableNodes;
import static savti.utilities.ImageUtilities.*;

public class MainWindow extends BorderPane {

    //region Local variables' declaration
    final String hoverButton = "-fx-background-color: #cd5c5c; \n-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);";

    SeekableByteChannel out = null;
    AWTSequenceEncoder encoder = null;
    UserSettings userSettings;
    SortAlgorithm algorithm;
    //TODO We don't need this variable
    JMetro theme;
    TiledImage image;

    AlgorithmProgressBar algorithmProgressBar;
    //endregion

    //region FXML variables declaration
    //TODO Should i create my own class?
    @FXML
    ImageView imageView;
    @FXML
    MenuItem imageLoaderItem;
    @FXML
    MenuItem songLoaderItem;
    @FXML
    MenuItem advSett;
    @FXML
    Button randomizeButton;
    @FXML
    Button sortingButton;
    @FXML
    Button cleanButton;
    @FXML
    Button ffmpegButton;
    @FXML
    Button outputButton;
    @FXML
    ComboBox<String> chooseAlgo;
    @FXML
    Button ffprobeButton;
    @FXML
    Label headerText;
    @FXML
    ToggleSwitch darkMode;
    @FXML
    Hyperlink pathLabel;
    @FXML
    Button burstMode;
    //endregion

    public MainWindow(Stage primaryStage) {
        //Load FXML for the scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            ErrorUtilities.FXMLLoadError();
        }
        //Instantiating and initializing non-JavaFX variables
        initComponents();
        //Add event listeners for the components
        addEventListeners();
    }

    //Creazione dei componenti
    private void initComponents() {
        //Create a default UserSettings object
        userSettings = new UserSettings();
        //We choose bubblesort as default algorithm
        algorithm = new BubbleSort(userSettings, image, imageView, null, encoder, out);

        image = new TiledImage();

        HBox.setHgrow(headerText, Priority.ALWAYS);

        BorderPane.setAlignment(headerText.getParent(), Pos.CENTER);

        addCssFiles();

        createComboBox();

        theme = new JMetro(this, Style.DARK);

        algorithmProgressBar = new AlgorithmProgressBar("AlgoName");
    }

    private void addCssFiles() {
        setStyle("-fx-base: black");
        randomizeButton.getParent().getStylesheets().add("/css/main.css");
        this.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
    }


    //Aggiunge i listener agli eventi dei nodi/elementi
    private void addEventListeners() {

        cleanButton.setOnAction(e -> {
            image.clearImage();
            imageView.setImage(null);
            deleteAllPreviousFiles(userSettings);
        });

        advSett.setOnAction(e -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.initOwner(this.getScene().getWindow());
            stage.getIcons().add(new Image("icon.png"));
            Scene scene = new Scene(new AdvancedSettings(stage, userSettings, theme, image));
            stage.setTitle("Advanced Settings");
            stage.setScene(scene);
            stage.showAndWait();
        });

        imageLoaderItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png"));
            fileChooser.setTitle("Open Resource File");
            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (chosenFile != null) {
                try {
                    image.setImage(SwingFXUtils.toFXImage(ImageIO.read(chosenFile), null));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);

                }
                //Set the intial precision to a default value
                userSettings.setChunkWidth((int) Math.round(image.getImage().getWidth() / 8));
                userSettings.setChunkHeight((int) Math.round(image.getImage().getHeight() / 8));
                userSettings.setRowsNumber((int) image.getImage().getHeight() / userSettings.getChunkHeight());
                userSettings.setColsNumber((int) image.getImage().getWidth() / userSettings.getChunkHeight());
                image.resizeArray(userSettings.getColsNumber() * userSettings.getRowsNumber());
                fillImage(image, imageView, (int) Math.round(this.getScene().getWidth() - ((VBox) cleanButton.getParent()).getWidth() - 20), (int) Math.round(((VBox) cleanButton.getParent()).getHeight() - 50));
            }
        });

        songLoaderItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
            fileChooser.setTitle("Open Song File");
            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            userSettings.setMusic(chosenFile);
        });

        outputButton.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose where to save your images!");
            File chosenDirectory = directoryChooser.showDialog(getScene().getWindow());
            if (chosenDirectory != null) {
                userSettings.setOutputDirectory(chosenDirectory);
                pathLabel.setText("Path to output: " + userSettings.getOutputDirectory().toString());
                outputButton.setStyle("");
            }
        });

//        ffmpegButton.setOnAction(e -> {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EXE", "*.exe"));
//            fileChooser.setTitle("Path to ffmpeg");
//            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
//            if (chosenFile != null) {
//                userSettings.setFfmpegPath(chosenFile);
//                ffmpegButton.setStyle("");
//            }
//        });
//
//        ffprobeButton.setOnAction(e -> {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EXE", "*.exe"));
//            fileChooser.setTitle("Path to ffprobe");
//            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
//            if (chosenFile != null) {
//                userSettings.setFfprobePath(chosenFile);
//                ffprobeButton.setStyle("");
//            }
//        });

        darkMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setStyle("-fx-base: black");
                theme.setStyle(Style.DARK);
            } else {
                setStyle("-fx-base: white");
                theme.setStyle(Style.LIGHT);
            }
        });

        pathLabel.setOnAction(event -> {
            if (userSettings.isADirectory())
                    try {
                        Desktop.getDesktop().open(userSettings.getOutputDirectory());
                    } catch (IOException e) {
                        ErrorUtilities.SWW();
                    }
        });

        randomizeButton.setOnAction(e -> {

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
        });

        sortingButton.setOnAction(e -> Platform.runLater(() -> {

            if (checkSortingConditions()) {
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
                }
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
        }));

        burstMode.setOnMouseEntered(e -> {
            Tooltip t = new Tooltip("Questo bottone serve per fare un video di tutti gli algoritmi");
            t.setStyle(" -fx-font-size: 10pt; -fx-font-style: italic;");
            burstMode.setTooltip(t);
        });

        burstMode.setOnAction(e -> {

            new Thread(() -> {

                VBox progessBarContainer = new VBox();
                ObservableList<SortAlgorithm> algoList = FXCollections.observableList(new ArrayList<>());
                TiledImage cloned;
                ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8));

                setupEnvForBurstMode(progessBarContainer, algoList);

                for (String s : chooseAlgo.getItems()) {

                    try {
                        out = NIOUtils.writableFileChannel("D:\\IdeaProjects\\sortingVisualization\\ext\\" + s + "+.mp4");
                        encoder = new AWTSequenceEncoder(out, Rational.R(userSettings.getFrameRate(), 1));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    AlgorithmProgressBar algorithmProgressBar = new AlgorithmProgressBar("AlgoName");
                    SortAlgorithm sortAlgorithm = null;

                    splitImage(image, userSettings.getColsNumber(), userSettings.getRowsNumber(), image);

                    new Thread(() -> rand(userSettings, image, encoder, algorithmProgressBar)).start();

                    try {
                        cloned = image.clone();
                    } catch (CloneNotSupportedException ex) {
                        throw new RuntimeException(ex);
                    }

                    switch (s) {
                        case "QuickSort" ->
                                sortAlgorithm = new QuickSort(userSettings, cloned, imageView, algorithmProgressBar, encoder, out);
                        case "BubbleSort" ->
                                sortAlgorithm = new BubbleSort(userSettings, cloned, imageView, algorithmProgressBar, encoder, out);
                        case "SelectionSort" ->
                                sortAlgorithm = new SelectionSort(userSettings, cloned, imageView, algorithmProgressBar, encoder, out);
                        case "InsertionSort" ->
                                sortAlgorithm = new InsertionSort(userSettings, cloned, imageView, algorithmProgressBar, encoder, out);
                        case "RadixSort" ->
                                sortAlgorithm = new RadixSort(userSettings, cloned, imageView, algorithmProgressBar, encoder, out);
                        case "MergeSort" ->
                                sortAlgorithm = new MergeSort(userSettings, cloned, imageView, algorithmProgressBar, encoder, out);
                        case "CocktailSort" ->
                                sortAlgorithm = new CocktailSort(userSettings, cloned, imageView, algorithmProgressBar, encoder, out);
                        case "GnomeSort" ->
                                sortAlgorithm = new GnomeSort(userSettings, cloned, imageView, algorithmProgressBar, encoder, out);
                        case "CycleSort" ->
                                sortAlgorithm = new CycleSort(userSettings, cloned, imageView, algorithmProgressBar, encoder, out);
                    }

                    createFutureTaskForBurstAlgo(pool, progessBarContainer, algoList, algorithmProgressBar, sortAlgorithm);

                    //TODO Encoder + Out ? Why not? Or even moving to xuggle??


                    //TODO ADD THE FUTURECALLBACK.
                    //TODO CHANGE HOW THE BURSTMODE WORKS
                    //TODO ADD THE FUNCTIONS IN BUBBLESORT TO EVERY OTHER ALGORITHM
                    //TODO REORDER ALGORITHMPROGRESSBARCLASS
                }

            }).start();
        });
    }
    private void setupEnvForBurstMode(VBox progessBarContainer, ObservableList<SortAlgorithm> algoList) {
        ableNodes(List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, burstMode, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()), List.of());
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
                ableNodes(List.of(), List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, burstMode, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()));
            });
        }, MoreExecutors.directExecutor());
    }

    private boolean checkSortingConditions() {

        if (!userSettings.verifyOutputPath()) {
            outputButton.setStyle(hoverButton);
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

    private void createComboBox() {
        List<String> algos = Arrays.asList("BubbleSort", "CocktailSort", "CycleSort", "GnomeSort", "InsertionSort", "MergeSort", "QuickSort", "RadixSort", "SelectionSort");
        chooseAlgo.setItems(FXCollections.observableList(algos));
        chooseAlgo.setValue("BubbleSort");
    }

    //TODO SMARTER WAY
    public void enableAll() {
        ableNodes(List.of(), List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()));
    }

    /*public void rand(UserSettings userSettings, TiledImage image) {
        int delay = Math.max(4 * image.getArray().length / (userSettings.getFrameRate() * userSettings.getVideoDuration()), 1);
        // Creating object for Random class
        Random rd = new Random();
        deleteAllPreviousFiles(userSettings);
        progressBar = new ProgressBar(0);
        progressIndicator = new ProgressIndicator(0);
        progressBox = new HBox();
        double increment = 1d / image.getArray().length;
        progressBox.getChildren().addAll(progressBar, progressIndicator);
        ((Group) imageView.getParent()).getChildren().add(progressBox);
        imageView.setVisible(false);
        imageView.setManaged(false);
        progressBar.setPrefWidth(1000);
        progressBar.setMinWidth(1000);
        progressBar.setPrefHeight(50);
        progressBar.setMinHeight(50);
        VBox.setMargin(progressBar, new Insets(10));

        progressIndicator.progressProperty().bind(progressBar.progressProperty());

        new Thread(() -> {

            if (out == null || !out.isOpen()) {
                try {
                    out = NIOUtils.writableFileChannel("D:\\IdeaProjects\\sortingVisualization\\ext\\output.mp4");
                    encoder = new AWTSequenceEncoder(out, Rational.R(userSettings.getFrameRate(), 1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            writeFreezedFrames(userSettings.getFrameRate() * 2, encoder, image, userSettings, increment, progressBar);

            for (int i = image.getArray().length - 1; i > 0; i--) {
                writeFrame(encoder, image, userSettings, increment, progressBar);
                // Pick a random index from 0 to i
                int j = rd.nextInt(i + 1);
                // Swap array[i] with the element at random index
                swap(image.getArray(), i, j);
            }

            Platform.runLater(() -> {
                imageView.setVisible(true);
                imageView.setManaged(true);
                ((Group) imageView.getParent()).getChildren().remove(progressBox);
                fillImage(userSettings, image, imageView, (int) imageView.getFitWidth(), (int) imageView.getFitHeight());
                userSettings.setStartingImageIndex(imageIndex);
                imageIndex = 0;
            });

        }).start();

    }*/
}
