package JavaFXVersion;

import JavaFXVersion.sorting.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static JavaFXVersion.ImageUtilities.fillImage;
import static JavaFXVersion.ImageUtilities.splitImage;
import static JavaFXVersion.sorting.SortAlgorithm.rand;

//TODO : WE DON'T NEED NO MORE TO MAKE THE ANIMATION. SIMPLY 

public class MainWindow extends BorderPane {
    //region Local variables' declaration
    //? Question is, do we really need all these variables?
    int CHUNK_WIDTH;
    int CHUNK_HEIGHT;
    UserSettings userSettings;
    Tail[] main;
    SortAlgorithm algorithm;
    Image i;
    //endregion
    //region FXML variables declaration
    @FXML
    GridPane gridPane;
    //?? Probably better substitute with buttons
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
    //endregion

    public MainWindow(Stage primaryStage) {
        //Load FXML for the scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainwindow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
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
        main = new Tail[64];
        //display output path
        headerText.setText("Video path: ");

        createComboBox();
    }

    //? Should we move loadAndSplitImage inside ImageUtilities class?
    //carico l'immagine overloaddato per usare il File restituito dalla finestra di dialogo
    private void loadAndSplitImage(File file, int width, int height) {
        BufferedImage capture = null;
        try {
            capture = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert capture != null;
        BufferedImage dimg = Scalr.resize(capture, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, width, height);
        if (dimg.getHeight() > height)
            dimg = Scalr.resize(capture, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, width, height);
        i = SwingFXUtils.toFXImage(dimg, null);
        CHUNK_WIDTH = dimg.getWidth() / userSettings.getPrecision();
        CHUNK_HEIGHT = dimg.getHeight() / userSettings.getPrecision();
        gridPane.setPrefSize(i.getWidth(), i.getHeight());
        gridPane.setMaxSize(i.getWidth(), i.getHeight());
        gridPane.setPadding(Insets.EMPTY);
        removeAllTails();
        splitImage(i, userSettings.getPrecision(), userSettings.getPrecision(), main);
        fillImage(CHUNK_WIDTH, CHUNK_HEIGHT, userSettings.getPrecision(), userSettings.getPrecision(), main, gridPane);
    }

    //Aggiunge i listener agli eventi dei nodi/elementi
    private void addEventListeners() {
        cleanButton.setOnAction(e -> {
            removeAllTails();
            Arrays.fill(main, null);
            i = null;
        });

        randomizeButton.setOnAction(e -> {
            if (i != null) {
                splitImage(i, userSettings.getPrecision(), userSettings.getPrecision(), main);
                removeAllTails();
                rand(main);
                fillImage(CHUNK_WIDTH, CHUNK_HEIGHT, userSettings.getPrecision(), userSettings.getPrecision(), main, gridPane);
            }
        });

        sortingButton.setOnAction(e -> Platform.runLater(() -> {
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
                        }
                    }
                    //Se tutti gli oggetti del vettore main sono diversi da NULL, e non c'è già un SortingThread attivo
                    // faccio partire l'ordinamento
                    disableAll();
                    algorithm.sort(main, gridPane);
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Immagine non valida");
                    errorAlert.setContentText("Selezionare un'immagine da ordinare");
                    errorAlert.showAndWait();
                }
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                if (!verifyFfmpegPath()) {
                    ffmpegButton.setUnderline(true);
                    errorAlert.setHeaderText("Ffmpeg path non valido");
                    errorAlert.setContentText("Selezionare un percorso valido per il Ffmpeg");
                    errorAlert.showAndWait();
                } else if (!verifyFfprobePath()) {
                    ffprobeButton.setUnderline(true);
                    errorAlert.setHeaderText("Ffprobe path non valido");
                    errorAlert.setContentText("Selezionare un percorso valido per il Ffprobe");
                    errorAlert.showAndWait();
                } else if (!verifyOutputPath()) {
                    outputButton.setUnderline(true);
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
                int width = (int) Math.round(this.getScene().getWidth() - ((VBox) cleanButton.getParent()).getWidth());
                int height = (int) Math.round(((VBox) cleanButton.getParent()).getHeight() - 20);
                loadAndSplitImage(chosenFile, width, height);
            }
        });

        changeoutputItem.setOnAction(e -> {
            TextInputDialog output = new TextInputDialog();
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

        saveimageItem.setOnAction((event -> userSettings.setSaveImage(saveimageItem.isSelected())));


        outputButton.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose where to save your images!");
            File chosenDirectory = directoryChooser.showDialog(getScene().getWindow());
            if (chosenDirectory != null) {
                userSettings.setOutputDirectory(chosenDirectory);
                headerText.setText("Video path: " + userSettings.getOutputDirectory().toString());
                outputButton.setUnderline(false);
            }
        });

        ffmpegButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EXE", "*.exe"));
            fileChooser.setTitle("Path to ffmpeg");
            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (chosenFile != null) {
                userSettings.setFfmpegPath(chosenFile);
                ffmpegButton.setUnderline(false);
            }
        });

        ffprobeButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EXE", "*.exe"));
            fileChooser.setTitle("Path to ffprobe");
            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (chosenFile != null) {
                userSettings.setFfprobePath(chosenFile);
                ffprobeButton.setUnderline(false);
            }
        });

        pauseButton.setOnAction((e -> {
            algorithm.killTask();
            enableAll();
        }));

        precisionSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            precisionValue.setText(String.valueOf(Math.floor((Double) newValue)));
            userSettings.setPrecision((int) Math.floor((Double) newValue) / 2);
            main = new Tail[(int) Math.pow(userSettings.getPrecision(), 2)];
            CHUNK_WIDTH = (int) (i.getWidth() / userSettings.getPrecision());
            CHUNK_HEIGHT = (int) (i.getHeight() / userSettings.getPrecision());
        });

        framerateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            framerateValue.setText(String.valueOf(Math.floor((Double) newValue)));
            userSettings.setFrameRate((int) Math.floor((Double) newValue));
        });

        videodurationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            videodurationValue.setText(String.valueOf(Math.floor((Double) newValue)));
            userSettings.setVideoDuration((int) Math.floor((Double) newValue));
        });

        openVideo.setOnAction((event -> userSettings.setOpenFile(openVideo.isSelected())));

    }

    private void createComboBox() {

        List<String> algos = Arrays.asList("BubbleSort", "QuickSort", "SelectionSort", "InsertionSort", "RadixSort", "MergeSort", "CocktailSort", "GnomeSort");

        chooseAlgo.setItems(FXCollections.observableList(algos));

        chooseAlgo.setValue("BubbleSort");

    }

    private void removeAllTails() {
        gridPane.getChildren().removeAll(gridPane.getChildren());
    }

    private void disableAll() {
        randomizeButton.setDisable(true);
        sortingButton.setDisable(true);
        pauseButton.setDisable(false);
        cleanButton.setDisable(true);
        ffmpegButton.setDisable(true);
        outputButton.setDisable(true);
        precisionSlider.setDisable(true);
        videodurationSlider.setDisable(true);
        ffprobeButton.setDisable(true);
    }

    private void enableAll() {
        randomizeButton.setDisable(false);
        sortingButton.setDisable(false);
        pauseButton.setDisable(true);
        cleanButton.setDisable(false);
        ffmpegButton.setDisable(false);
        outputButton.setDisable(false);
        precisionSlider.setDisable(false);
        videodurationSlider.setDisable(false);
        ffprobeButton.setDisable(false);
    }

    private boolean verifyFfmpegPath() {
        if (userSettings.getFfmpegPath() == null || !userSettings.getFfmpegPath().toString().endsWith("ffmpeg.exe"))
            return false;

        else
            return true;
    }

    private boolean verifyFfprobePath() {
        if (userSettings.getFfprobePath() == null || !userSettings.getFfprobePath().toString().endsWith("ffprobe.exe"))
            return false;
        else
            return true;
    }

    private boolean verifyOutputPath() {
        if (userSettings.getOutputDirectory() == null)
            return false;
        else
            return true;
    }
    //region Utilities methods
    //endregion
}

