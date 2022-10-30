package JavaFXVersion;

import JavaFXVersion.sorting.*;
import JavaFXVersion.utilities.ErrorUtilities;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static JavaFXVersion.sorting.SortUtils.swap;
import static JavaFXVersion.utilities.FileUtilities.*;
import static JavaFXVersion.utilities.GUIUtilities.ableNodes;
import static JavaFXVersion.utilities.ImageUtilities.fillImage;
import static JavaFXVersion.utilities.ImageUtilities.splitImage;

public class MainWindow extends BorderPane {

    final String hoverButton = "-fx-background-color: #cd5c5c; \n-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);";
    //region Local variables' declaration
    SeekableByteChannel out = null;
    AWTSequenceEncoder encoder = null;
    UserSettings userSettings;
    SortAlgorithm algorithm;
    WritableImage i;
    JMetro theme;
    TiledImage image;
    int imageIndex = 0;

    ProgressBar progressBar;
    ProgressIndicator progressIndicator;
    HBox progressBox;
    //endregion

    //region FXML variables declaration
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
        algorithm = new BubbleSort(userSettings, image, imageView, encoder, out, null);

        image = new TiledImage();

        HBox.setHgrow(headerText, Priority.ALWAYS);

        BorderPane.setAlignment(headerText.getParent(), Pos.CENTER);

        addCssFiles();

        createComboBox();

        theme = new JMetro(this, Style.DARK);

        try {
            out = NIOUtils.writableFileChannel("C:\\Users\\andrea\\IdeaProjects\\sortingVisualization\\ext\\output.mp4");
            encoder = new AWTSequenceEncoder(out, Rational.R(25, 1));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
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

        randomizeButton.setOnAction(e -> {
            if (i != null) {
                randomizeButton.setDisable(true);
                splitImage(i, userSettings.getColsNumber(), userSettings.getRowsNumber(), image);
                Platform.runLater(() -> {
                    ableNodes(List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()), List.of());
                    rand(userSettings, image);
                    ableNodes(List.of(), List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()));
                    int width = (int) Math.round(this.getScene().getWidth() - ((VBox) cleanButton.getParent()).getWidth() - 20);
                    int height = (int) Math.round(((VBox) cleanButton.getParent()).getHeight() - 50);
                    fillImage(userSettings, image, imageView, width, height);
                });
                randomizeButton.setDisable(false);
            }
        });

        sortingButton.setOnAction(e -> Platform.runLater(() -> {
            if (checkSortingConditions()) {
                AlgorithmProgressBar al = new AlgorithmProgressBar("AlgoName");
                String choice = chooseAlgo.getValue();
                switch (choice) {
                    case "QuickSort" -> algorithm = new QuickSort(userSettings, image, imageView, encoder, out, al);
                    case "SelectionSort" ->
                            algorithm = new SelectionSort(userSettings, image, imageView, encoder, out, al);
                    case "BubbleSort" -> algorithm = new BubbleSort(userSettings, image, imageView, encoder, out, al);
                    case "InsertionSort" ->
                            algorithm = new InsertionSort(userSettings, image, imageView, encoder, out, al);
                    case "RadixSort" -> algorithm = new RadixSort(userSettings, image, imageView, encoder, out, al);
                    case "MergeSort" -> algorithm = new MergeSort(userSettings, image, imageView, encoder, out, al);
                    case "CocktailSort" ->
                            algorithm = new CocktailSort(userSettings, image, imageView, encoder, out, al);
                    case "GnomeSort" -> algorithm = new GnomeSort(userSettings, image, imageView, encoder, out, al);
                    case "CycleSort" -> algorithm = new CycleSort(userSettings, image, imageView, encoder, out, al);
                    default -> ErrorUtilities.SWW();
                }
                ableNodes(List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()), List.of());
                al.setAlgoName(choice);
                ((Group) imageView.getParent()).getChildren().add(al);
                imageView.setVisible(false);
                imageView.setManaged(false);
                new Thread(algorithm).start();
                /*
                imageView.setVisible(true);
                imageView.setManaged(true);
                ((Group) imageView.getParent()).getChildren().remove(progressBox);
                 */
            }
        }));

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
                int width = (int) Math.round(this.getScene().getWidth() - ((VBox) cleanButton.getParent()).getWidth() - 20);
                int height = (int) Math.round(((VBox) cleanButton.getParent()).getHeight() - 50);
                try {
                    i = SwingFXUtils.toFXImage(ImageIO.read(chosenFile), null);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);

                }
                image.setImage(i);
                //Set the intial precision to a default value
                userSettings.setChunkWidth((int) Math.round(image.getImage().getWidth() / 8));
                userSettings.setChunkHeight((int) Math.round(image.getImage().getHeight() / 8));
                userSettings.setRowsNumber((int) image.getImage().getHeight() / userSettings.getChunkHeight());
                userSettings.setColsNumber((int) image.getImage().getWidth() / userSettings.getChunkHeight());
                image.resizeArray(userSettings.getColsNumber() * userSettings.getRowsNumber());
                fillImage(i, imageView, width, height);
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

        ffmpegButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EXE", "*.exe"));
            fileChooser.setTitle("Path to ffmpeg");
            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (chosenFile != null) {
                userSettings.setFfmpegPath(chosenFile);
                ffmpegButton.setStyle("");
            }
        });

        ffprobeButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EXE", "*.exe"));
            fileChooser.setTitle("Path to ffprobe");
            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (chosenFile != null) {
                userSettings.setFfprobePath(chosenFile);
                ffprobeButton.setStyle("");
            }
        });

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
            if (userSettings.getOutputDirectory() != null)
                if (userSettings.getOutputDirectory().isDirectory()) {
                    try {
                        Desktop.getDesktop().open(userSettings.getOutputDirectory());
                    } catch (IOException e) {
                        ErrorUtilities.SWW();
                    }
                }
        });

        burstMode.setOnAction(e -> {
            ableNodes(List.of(randomizeButton, sortingButton, cleanButton, ffmpegButton, ffprobeButton, outputButton, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()), List.of());
            SeekableByteChannel bOut = null;
            AWTSequenceEncoder bEncoder = null;
            for (String s : chooseAlgo.getItems()) {
                try {
                    bOut = NIOUtils.writableFileChannel("C:\\Users\\andrea\\IdeaProjects\\sortingVisualization\\ext\\" + s + ".mp4");
                    bEncoder = new AWTSequenceEncoder(bOut, Rational.R(userSettings.getFrameRate(), 1));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                AlgorithmProgressBar al = new AlgorithmProgressBar("AlgoName");
                splitImage(i, userSettings.getColsNumber(), userSettings.getRowsNumber(), image);
                JavaFXVersion.sorting.SortUtils.rand(userSettings, image, bEncoder, bOut);
                switch (s) {
                    case "QuickSort" -> algorithm = new QuickSort(userSettings, image, imageView, bEncoder, bOut, al);
                    case "BubbleSort" -> algorithm = new BubbleSort(userSettings, image, imageView, bEncoder, bOut, al);
                    case "SelectionSort" ->
                            algorithm = new SelectionSort(userSettings, image, imageView, bEncoder, bOut, al);
                    case "InsertionSort" ->
                            algorithm = new InsertionSort(userSettings, image, imageView, bEncoder, bOut, al);
                    case "RadixSort" -> algorithm = new RadixSort(userSettings, image, imageView, bEncoder, bOut, al);
                    case "MergeSort" -> algorithm = new MergeSort(userSettings, image, imageView, bEncoder, bOut, al);
                    case "CocktailSort" ->
                            algorithm = new CocktailSort(userSettings, image, imageView, bEncoder, bOut, al);
                    case "GnomeSort" -> algorithm = new GnomeSort(userSettings, image, imageView, bEncoder, bOut, al);
                    case "CycleSort" -> algorithm = new CycleSort(userSettings, image, imageView, bEncoder, bOut, al);
                }
                Thread t = new Thread(algorithm);
                t.start();
            }
        });
    }

    private boolean checkSortingConditions() {

        if (!userSettings.verifyFfmpegPath()) {
            ffmpegButton.setStyle(hoverButton);
            ErrorUtilities.ffmpegPath();
            return false;
        } else if (!userSettings.verifyFfprobePath()) {
            ffprobeButton.setStyle(hoverButton);
            ErrorUtilities.ffprobePath();
            return false;
        } else if (!userSettings.verifyOutputPath()) {
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

    public void rand(UserSettings userSettings, TiledImage image) {
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

            if (!out.isOpen()) {
                try {
                    out = NIOUtils.writableFileChannel("C:\\Users\\andrea\\IdeaProjects\\sortingVisualization\\ext\\output.mp4");
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

    }
}

