package JavaFXVersion;

import JavaFXVersion.sorting.*;
import JavaFXVersion.utilities.ColorUtilities;
import JavaFXVersion.utilities.ErrorUtilities;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.controlsfx.control.ToggleSwitch;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

import static JavaFXVersion.sorting.SortAlgorithm.rand;
import static JavaFXVersion.utilities.ImageUtilities.fillImage;
import static JavaFXVersion.utilities.ImageUtilities.splitImage;

public class MainWindow extends BorderPane {
    //region Local variables' declaration
    //? Question is, do we really need all these variables?
    final String hoverButton = "-fx-background-color: #cd5c5c; \n-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);";
    int CHUNK_WIDTH;
    int CHUNK_HEIGHT;
    UserSettings userSettings;
    Tile[] main;
    SortAlgorithm algorithm;
    Image i;
    JMetro theme;
    //endregion

    //region FXML variables declaration
    @FXML
    GridPane gridPane;
    @FXML
    StackPane container;
    @FXML
    Group group;
    @FXML
    MenuItem imageLoaderItem;
    @FXML
    MenuItem songLoaderItem;
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
    CheckMenuItem openVideo;
    @FXML
    ComboBox<String> chooseAlgo;
    @FXML
    CheckMenuItem saveimageItem;
    @FXML
    MenuItem changeoutputItem;
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
        //Load the image in the gridPane splitting it
        //loadAndSplitImage(new File("bigimage.jpg"), 1200, 800);
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

    //carico l'immagine overloaddato per usare il File restituito dalla finestra di dialogo
    private void loadAndSplitImage(File file, int width, int height) {
        BufferedImage capture = null;
        BufferedImage dimg = null;
        try {
            capture = ImageIO.read(file);
            if(capture != null)
                dimg = Scalr.resize(capture, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, width-20, height);
            else {
                ErrorUtilities.loadImageError();
                return;
            }
        } catch (IOException e) {
            ErrorUtilities.loadImageError();
            return;
        }
        if (dimg.getHeight() > height)
            dimg = Scalr.resize(capture, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, width, height-30);
        i = SwingFXUtils.toFXImage(dimg, null);
        CHUNK_WIDTH = dimg.getWidth() / userSettings.getPrecision();
        CHUNK_HEIGHT = dimg.getHeight() / userSettings.getPrecision();
        userSettings.setChunkWidth(CHUNK_WIDTH);
        userSettings.setChunkHeight(CHUNK_HEIGHT);
        removeAllTiles();
        splitImage(i, userSettings.getPrecision(), userSettings.getPrecision(), main);
        fillImage(CHUNK_WIDTH, CHUNK_HEIGHT, userSettings.getPrecision(), userSettings.getPrecision(), main, gridPane);

        container.setPrefSize(width, height);
        container.setMaxSize(width, height);
        container.setPadding(Insets.EMPTY);
        StackPane.setAlignment(group, Pos.CENTER);
    }

    //Aggiunge i listener agli eventi dei nodi/elementi
    private void addEventListeners() {

        cleanButton.setOnAction(e -> {
            removeAllTiles();
            Arrays.fill(main, null);
            i = null;
        });

        randomizeButton.setOnAction(e -> {
            if (i != null) {
                splitImage(i, userSettings.getPrecision(), userSettings.getPrecision(), main);
                removeAllTiles();
                rand(main);
                fillImage(CHUNK_WIDTH, CHUNK_HEIGHT, userSettings.getPrecision(), userSettings.getPrecision(), main, gridPane);
            }
        });

        sortingButton.setOnAction(e -> Platform.runLater(() -> {
            //TODO create a function!!!!!!
            if (verifyFfmpegPath() && verifyOutputPath() && verifyFfprobePath()) {
                if (!Arrays.stream(main).allMatch(Objects::isNull)) {
                    if (chooseAlgo.getValue() != null) {
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
                            case "BitonicSort" -> algorithm = new BitonicSort(userSettings);
                        }
                    }
                    List<Tile> sorted = new ArrayList<Tile>(List.of(main));
                    Collections.sort(sorted);
                    if(sorted.equals(List.of(main)))
                        ErrorUtilities.alreadyOrderedImage();
                    else {
                        disableAll();
                        algorithm.sort(main, gridPane, this);
                    }
                } else {
                    ErrorUtilities.noImageError();
                }
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                if (!verifyFfmpegPath()) {
                    ffmpegButton.setStyle(hoverButton);
                    errorAlert.setHeaderText("Ffmpeg path non valido");
                    errorAlert.setContentText("Selezionare un percorso valido per il Ffmpeg");
                    errorAlert.showAndWait();
                } else if (!verifyFfprobePath()) {
                    ffprobeButton.setStyle(hoverButton);
                    errorAlert.setHeaderText("Ffprobe path non valido");
                    errorAlert.setContentText("Selezionare un percorso valido per il Ffprobe");
                    errorAlert.showAndWait();
                } else if (!verifyOutputPath()) {
                    outputButton.setStyle(hoverButton);
                    errorAlert.setHeaderText("Output path non valido");
                    errorAlert.setContentText("Selezionare un percorso valido per l'output");
                    errorAlert.showAndWait();
                }
            }
        }));

        imageLoaderItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"));
            fileChooser.setTitle("Open Resource File");
            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (chosenFile != null) {
                int width = (int) Math.round(this.getScene().getWidth() - ((VBox) cleanButton.getParent()).getWidth() - 20);
                int height = (int) Math.round(((VBox) cleanButton.getParent()).getHeight() - 50);
                loadAndSplitImage(chosenFile, width, height);
            }
        });

        changeoutputItem.setOnAction(e -> {
            TextInputDialog output = new TextInputDialog();
            output.getDialogPane().getStylesheets().add(getClass().getResource("grid.css").toExternalForm());
            output.setTitle("Scegli il nome del file video!");
            output.setContentText("Nome file:");
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

        pauseButton.setOnAction(e -> {
            algorithm.killTask();
            enableAll();
        });

        precisionSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Node thumb = precisionSlider.lookup(".thumb");
            thumb.setStyle("-fx-background-color: #" + ColorUtilities.getHexFromValue(newValue.intValue()/50f) + ";");
            precisionValue.setText(String.valueOf(Math.floor((Double) newValue)));
            precisionValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue(newValue.intValue()/50f) +
                    ";");
            userSettings.setPrecision((int) Math.floor((Double) newValue));
            main = new Tile[(int) Math.pow(userSettings.getPrecision() , 2)];
            if(i != null)
            {
                CHUNK_WIDTH = (int) (i.getWidth() / userSettings.getPrecision());
                CHUNK_HEIGHT = (int) (i.getHeight() / userSettings.getPrecision());
                userSettings.setChunkWidth(CHUNK_WIDTH);
                userSettings.setChunkHeight(CHUNK_HEIGHT);
            }
        });

        framerateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Node thumb = framerateSlider.lookup(".thumb");
            thumb.setStyle("-fx-background-color: #" + ColorUtilities.getHexFromValue(newValue.intValue()/60f) +
                    ";");
            framerateValue.setText(String.valueOf(Math.floor((Double) newValue)));
            framerateValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue(newValue.intValue()/60f) + ";");
            userSettings.setFrameRate((int) Math.floor((Double) newValue) / 2);
            framerateValue.setText(String.valueOf(Math.floor((Double) newValue)));
            userSettings.setFrameRate((int) Math.floor((Double) newValue));
        });

        videodurationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Node thumb = videodurationSlider.lookup(".thumb");
            thumb.setStyle("-fx-background-color: #" + ColorUtilities.getHexFromValue(newValue.intValue()/30f) + ";");
            videodurationValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue(newValue.intValue()/30f) + ";");
            videodurationValue.setText(String.valueOf(Math.floor((Double) newValue)));
            userSettings.setVideoDuration((int) Math.floor((Double) newValue));
        });

        openVideo.setOnAction(event -> userSettings.setOpenFile(openVideo.isSelected()));

        darkMode.selectedProperty().addListener((observable , oldValue , newValue) -> {
            if(newValue)
            {
                setStyle("-fx-base: black");
                theme.setStyle(Style.DARK);
            }
            else
            {
                setStyle("-fx-base: white");
                theme.setStyle(Style.LIGHT);
            }
        });

        pathLabel.setOnAction(event -> {
            if(userSettings.getOutputDirectory() != null)
                if(userSettings.getOutputDirectory().isDirectory())
                {
                    try {
                        Desktop.getDesktop().open(userSettings.getOutputDirectory());
                    } catch (IOException e) {
                        ErrorUtilities.SWW();
                    }
                }
        });

    }

    private void createComboBox() {
        List<String> algos = Arrays.asList("BubbleSort", "QuickSort", "SelectionSort", "InsertionSort", "RadixSort", "MergeSort", "CocktailSort", "GnomeSort", "BitonicSort");
        chooseAlgo.setItems(FXCollections.observableList(algos));
        chooseAlgo.setValue("BubbleSort");
    }

    private void removeAllTiles() {
        gridPane.getChildren().removeAll(gridPane.getChildren());
    }

    private void disableAll() {
        randomizeButton.setDisable(true);
        sortingButton.setDisable(true);
        cleanButton.setDisable(true);
        ffmpegButton.setDisable(true);
        outputButton.setDisable(true);
        precisionSlider.setDisable(true);
        videodurationSlider.setDisable(true);
        ffprobeButton.setDisable(true);
        framerateSlider.setDisable(true);
        imageLoaderItem.setDisable(true);
        songLoaderItem.setDisable(true);

        pauseButton.setDisable(false);
    }

    public void enableAll() {
        randomizeButton.setDisable(false);
        sortingButton.setDisable(false);
        cleanButton.setDisable(false);
        ffmpegButton.setDisable(false);
        outputButton.setDisable(false);
        precisionSlider.setDisable(false);
        videodurationSlider.setDisable(false);
        ffprobeButton.setDisable(false);
        framerateSlider.setDisable(false);
        imageLoaderItem.setDisable(false);
        songLoaderItem.setDisable(false);

        pauseButton.setDisable(true);
    }

    private boolean verifyFfmpegPath() {
        return userSettings.getFfmpegPath() != null && userSettings.getFfmpegPath().toString().endsWith("ffmpeg.exe");
    }

    private boolean verifyFfprobePath() {
        return userSettings.getFfprobePath() != null && userSettings.getFfprobePath().toString().endsWith("ffprobe.exe");
    }

    private boolean verifyOutputPath() {
        return userSettings.getOutputDirectory() != null;
    }

    private void updateLabels(){

        //FRAMERATE SLIDER
        double newValue = framerateSlider.getValue();
        framerateValue.setText(String.valueOf(Math.floor((Double) newValue)));
        framerateValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue((float) (newValue/60f)) + ";");
        framerateValue.setText(String.valueOf(Math.floor((Double) newValue)));

        //PRECISION SLIDER
        newValue = precisionSlider.getValue();
        precisionValue.setText(String.valueOf(Math.floor((Double) newValue)));
        precisionValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue((float) (newValue/50f)) + ";");

        //DURATION SLIDER
        newValue = videodurationSlider.getValue();
        videodurationValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue((float) (newValue/30f)) + ";");
        videodurationValue.setText(String.valueOf(Math.floor((Double) newValue)));
    }
    //region Utilities methods
    //endregion
}

