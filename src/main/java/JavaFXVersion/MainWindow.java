package JavaFXVersion;

import JavaFXVersion.sorting.*;
import JavaFXVersion.utilities.ColorUtilities;
import JavaFXVersion.utilities.ErrorUtilities;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.controlsfx.control.ToggleSwitch;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static JavaFXVersion.sorting.SortAlgorithm.rand;
import static JavaFXVersion.utilities.GUIUtilities.ableNodes;
import static JavaFXVersion.utilities.ImageUtilities.*;

public class MainWindow extends BorderPane {

    //region Local variables' declaration
    final String hoverButton = "-fx-background-color: #cd5c5c; \n-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);";
    UserSettings userSettings;
    Tile[] main;
    SortAlgorithm algorithm;
    WritableImage i;
    JMetro theme;
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
    Slider precisionSlider;
    @FXML
    Label precisionValue;
    @FXML
    Button ffmpegButton;
    @FXML
    Button outputButton;
    @FXML
    Button pauseButton;
    @FXML
    Slider framerateSlider;
    @FXML
    Label framerateValue;
    @FXML
    Slider videodurationSlider;
    @FXML
    Label videodurationValue;
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
        algorithm = new BubbleSort(userSettings);
        //By default, the image is split in 8x8 grid
        main = new Tile[64];
        //display output path

        HBox.setHgrow(headerText, Priority.ALWAYS);

        BorderPane.setAlignment(headerText.getParent(), Pos.CENTER);

        addCssFiles();

        createComboBox();

        updateLabels();

        theme = new JMetro(this, Style.DARK);
    }

    private void addCssFiles() {
        setStyle("-fx-base: black");
        randomizeButton.getParent().getStylesheets().add("/css/main.css");
        this.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
    }

    //Aggiunge i listener agli eventi dei nodi/elementi
    private void addEventListeners() {

        cleanButton.setOnAction(e -> {
            Arrays.fill(main, null);
            i = null;
        });

        randomizeButton.setOnAction(e -> {
            if (i != null) {
                splitImage(i, userSettings.getColsNumber(), userSettings.getRowsNumber(), main);
                Thread t = new Thread(() -> {
                    ableNodes(List.of(randomizeButton,sortingButton,cleanButton,ffmpegButton, ffprobeButton, outputButton, precisionSlider, videodurationSlider, framerateSlider, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()), List.of(pauseButton));
                    rand(main, userSettings);
                    ableNodes(List.of(pauseButton), List.of(randomizeButton,sortingButton,cleanButton,ffmpegButton, ffprobeButton, outputButton, precisionSlider, videodurationSlider, framerateSlider, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()));
                    int width = (int) Math.round(this.getScene().getWidth() - ((VBox) cleanButton.getParent()).getWidth() - 20);
                    int height = (int) Math.round(((VBox) cleanButton.getParent()).getHeight() - 50);
                    fillImage(userSettings, main, imageView, width, height);
                });
                t.setPriority(Thread.MAX_PRIORITY);
                t.start();
            }
        });

        sortingButton.setOnAction(e -> Platform.runLater(() -> {
            if (checkSortingConditions()) {
                String choice = chooseAlgo.getValue();
                switch (choice) {
                    case "QuickSort" -> algorithm = new QuickSort(userSettings);
                    case "SelectionSort" -> algorithm = new SelectionSort(userSettings);
                    case "BubbleSort" -> algorithm = new BubbleSort(userSettings);
                    case "InsertionSort" -> algorithm = new InsertionSort(userSettings);
                    case "RadixSort" -> algorithm = new RadixSort(userSettings);
                    case "MergeSort" -> algorithm = new MergeSort(userSettings);
                    case "CocktailSort" -> algorithm = new CocktailSort(userSettings);
                    case "GnomeSort" -> algorithm = new GnomeSort(userSettings);
                    case "CycleSort" -> algorithm = new CycleSort(userSettings);
                    default -> ErrorUtilities.SWW();
                }
                ableNodes(List.of(randomizeButton,sortingButton,cleanButton,ffmpegButton, ffprobeButton, outputButton, precisionSlider, videodurationSlider, framerateSlider, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()), List.of(pauseButton));
                algorithm.sort(imageView, main, this);
            }
        }));

        advSett.setOnAction(e -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.getScene().getWindow());
            Scene scene = new Scene(new AdvancedSettings(stage, userSettings, theme), 600, 400);
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
                userSettings.setRowsNumber((int) Math.round(i.getWidth() * (precisionSlider.valueProperty().floatValue() / 200f)));
                userSettings.setColsNumber((int) Math.round(i.getHeight() * (precisionSlider.valueProperty().floatValue() / 200f)));
                userSettings.setChunkHeight((int) i.getHeight() / userSettings.getRowsNumber());
                userSettings.setChunkWidth((int) i.getWidth() / userSettings.getColsNumber());
                main = new Tile[userSettings.getColsNumber() * userSettings.getRowsNumber()];
                fillImage(i, imageView, width, height);
            }
        });

        changeoutputItem.setOnAction(e -> {
            TextInputDialog output = new TextInputDialog();
            output.setTitle("Choose the name of the output file.");
            output.setContentText("Filename: ");
            Optional<String> out = output.showAndWait();
            if (out.isPresent()) {
                if (out.get().endsWith(".mp4"))
                    userSettings.setOutName(out.get());
                else
                    userSettings.setOutName(out.get() + ".mp4");
            }
        });

        songLoaderItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
            fileChooser.setTitle("Open Song File");
            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            userSettings.setMusic(chosenFile);
        });

        saveimageItem.setOnAction(event -> userSettings.setSaveImage(saveimageItem.isSelected()));

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

        //TODO
        pauseButton.setOnAction(e -> {
            algorithm.killTask();
            ableNodes(List.of(pauseButton), List.of(randomizeButton,sortingButton,cleanButton,ffmpegButton, ffprobeButton, outputButton, precisionSlider, videodurationSlider, framerateSlider, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()));
        });

        precisionSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Node thumb = precisionSlider.lookup(".thumb");
            thumb.setStyle("-fx-background-color: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 100f) + ";");
            precisionValue.setText(String.valueOf(Math.floor((Double) newValue)));
            precisionValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 100f) + ";");
            if (i != null) {
                userSettings.setRowsNumber((int) Math.round(i.getWidth() * (precisionSlider.valueProperty().floatValue() / 200f)));
                userSettings.setColsNumber((int) Math.round(i.getHeight() * (precisionSlider.valueProperty().floatValue() / 200f)));
                userSettings.setChunkHeight((int) i.getHeight() / userSettings.getRowsNumber());
                userSettings.setChunkWidth((int) i.getWidth() / userSettings.getColsNumber());
            }
            main = new Tile[userSettings.getColsNumber() * userSettings.getRowsNumber()];
        });

        framerateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Node thumb = framerateSlider.lookup(".thumb");
            thumb.setStyle("-fx-background-color: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 60f) +
                    ";");
            framerateValue.setText(String.valueOf(Math.floor((Double) newValue)));
            framerateValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 60f) + ";");
            userSettings.setFrameRate((int) Math.floor((Double) newValue) / 2);
            framerateValue.setText(String.valueOf(Math.floor((Double) newValue)));
            userSettings.setFrameRate((int) Math.floor((Double) newValue));
        });

        videodurationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Node thumb = videodurationSlider.lookup(".thumb");
            thumb.setStyle("-fx-background-color: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 30f) + ";");
            videodurationValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 30f) + ";");
            videodurationValue.setText(String.valueOf(Math.floor((Double) newValue)));
            userSettings.setVideoDuration((int) Math.floor((Double) newValue));
        });

        openVideo.setOnAction(event -> userSettings.setOpenFile(openVideo.isSelected()));

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

    }

    private boolean checkSortingConditions() {

            if (!userSettings.verifyFfmpegPath()) {
                ffmpegButton.setStyle(hoverButton);
                ErrorUtilities.ffmpegPath();
                return false;
            }
            else if (!userSettings.verifyFfprobePath()) {
                ffprobeButton.setStyle(hoverButton);
                ErrorUtilities.ffprobePath();
                return false;
            }
            else if (!userSettings.verifyOutputPath()) {
                outputButton.setStyle(hoverButton);
                ErrorUtilities.outputPath();
                return false;
            }
            else if (Arrays.stream(main).allMatch(Objects::isNull) || i == null) {
                ErrorUtilities.noImageError();
                return false;
            }

            List<Tile> sorted = new ArrayList<>(List.of(main));
            Collections.sort(sorted);
            if (sorted.equals(List.of(main))) {
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

    private void updateLabels() {

        //FRAMERATE SLIDER
        double newValue = framerateSlider.getValue();
        framerateValue.setText(String.valueOf(Math.floor(newValue)));
        framerateValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue((float) (newValue / 60f)) + ";");
        framerateValue.setText(String.valueOf(Math.floor(newValue)));

        //PRECISION SLIDER
        newValue = precisionSlider.getValue();
        precisionValue.setText(String.valueOf(Math.floor(newValue)));
        precisionValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue((float) (newValue / 50f)) + ";");

        //DURATION SLIDER
        newValue = videodurationSlider.getValue();
        videodurationValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue((float) (newValue / 30f)) + ";");
        videodurationValue.setText(String.valueOf(Math.floor(newValue)));
    }

    //TODO SMARTER WAY
    public void enableAll() {
        ableNodes(List.of(pauseButton), List.of(randomizeButton,sortingButton,cleanButton,ffmpegButton, ffprobeButton, outputButton, precisionSlider, framerateSlider, videodurationSlider, imageLoaderItem.getStyleableNode(), songLoaderItem.getStyleableNode()));
    }
}

