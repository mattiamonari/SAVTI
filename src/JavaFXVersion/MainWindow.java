package JavaFXVersion;

import JavaFXVersion.sorting.BubbleSort;
import JavaFXVersion.sorting.QuickSort;
import JavaFXVersion.sorting.SortAlgorithm;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static JavaFXVersion.ImageUtilities.fillImage;
import static JavaFXVersion.ImageUtilities.splitImage;
import static JavaFXVersion.sorting.SortAlgorithm.rand;

//IMPORTANT TODO:
//TODO : WE DON'T NEED NO MORE TO MAKE THE ANIMATION. SIMPLY 

public class MainWindow extends BorderPane {

    //region Local variables decleration
    //? Question is, do we really need all these variables?
    int CHUNK_WIDTH;
    int CHUNK_HEIGHT;
    UserSettings userSettings;
    Tail[] main;
    SortAlgorithm algorithm;
    Image i;
    ToggleGroup tg;
    //endregion

    //region FXML variables declaration
    @FXML
    GridPane gridPane;
    //?? Probably better substitute with buttons
    @FXML
    MenuItem settingsItem;
    @FXML
    MenuItem imageLoaderItem;
    @FXML
    Button randomizeButton;
    @FXML
    Button sortingButton;
    @FXML
    Button cleanButton;
    @FXML
    Slider precisionSlider;
    @FXML
    Label sliderValue;
    @FXML
    Button ffmpegButton;
    @FXML
    Button outputButton;
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

        //Istantiating and initializating non-JavaFX variables
        initComponents();

        //Load the image in the gridPane splitting it
        loadAndSplitImage(new File("res/bigimage.jpg"),1200);

        //Add event listeners for the components
        addEventListeners();

    }

    //Creazione dei componenti
    private void initComponents() {
        //Create a default UserSettings object
        userSettings = new UserSettings();
        //We choose bubblesort as default algorithm
        algorithm = new BubbleSort(userSettings);
        //By default, the image is splitted in 8x8 grid
        main = new Tail[64];
        //?Radio buttons for the algorithm choice, can we use something else like a window?
        createRadioButtons();
    }

    //? Should we move loadAndSplitImage inside ImageUtilities class?
    //carico l'immagine overloaddato per usare il File restituito dalla finestra di dialogo
    private void loadAndSplitImage(File file, int width) {
        BufferedImage capture = null;
        try {
            capture = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert capture != null;
        BufferedImage dimg = Scalr.resize(capture , Scalr.Method.ULTRA_QUALITY , Scalr.Mode.FIT_TO_WIDTH , width,700);
        i = SwingFXUtils.toFXImage(dimg , null);
        CHUNK_WIDTH = dimg.getWidth() / userSettings.getPrecision();
        CHUNK_HEIGHT = dimg.getHeight() / userSettings.getPrecision();
        gridPane.setPrefSize(i.getWidth() , i.getHeight());
        gridPane.setMaxSize(i.getWidth() , i.getHeight());
        gridPane.setPadding(Insets.EMPTY);
        removeAllTails();
        splitImage(i , userSettings.getPrecision() , userSettings.getPrecision() , main);
        fillImage(CHUNK_WIDTH , CHUNK_HEIGHT , userSettings.getPrecision() , userSettings.getPrecision() , main , gridPane);
    }

    //Aggiunge i listener agli eventi dei nodi/elementi
    //TODO Risolvere vari bug relativi a combinazione di tasti e variazioni di paramteri (slider) durante l'esecuzione degli algoritmi
    private void addEventListeners() {

        cleanButton.setOnAction(e -> {
            removeAllTails();
            Arrays.fill(main , null);
            i = null;
        });

        randomizeButton.setOnAction(e -> {
            if (i != null) {
                algorithm.killTask();
                splitImage(i , userSettings.getPrecision() , userSettings.getPrecision() , main);
                removeAllTails();
                rand(main); //shuffle(writableimages)
                fillImage(CHUNK_WIDTH , CHUNK_HEIGHT , userSettings.getPrecision() , userSettings.getPrecision() , main , gridPane);
            }
        });

        sortingButton.setOnAction(e -> Platform.runLater(() -> {
            if (!algorithm.isThreadAlive() && !Arrays.stream(main).allMatch(Objects::isNull)) {

                //Ha senso farlo sempre?
                if (tg.getSelectedToggle() != null) {
                    if (((RadioButton) tg.getSelectedToggle()).getText().equals("QuickSort"))
                        algorithm = new QuickSort(userSettings);
                    else
                        algorithm = new BubbleSort(userSettings);
                }
                //Se tutti gli oggetti del vettore main sono diversi da NULL, e non c'è già un SortingThread attivo
                // faccio partire l'ordinamento
                algorithm.sort(main, gridPane);
            }
        }));

        imageLoaderItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG" , "*.jpg") , new FileChooser.ExtensionFilter("PNG" , "*.png"));
            fileChooser.setTitle("Open Resource File");

            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (chosenFile != null) {
                int width = (int) Math.round(this.getScene().getWidth() - ((VBox)cleanButton.getParent()).getWidth());
                loadAndSplitImage(chosenFile, width);
            }
        });

        //settingsItem
        outputButton.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose where to save your images!");

            File chosenDirectory = directoryChooser.showDialog(getScene().getWindow());
            if (chosenDirectory != null) {
                userSettings.setOutputDirectory(chosenDirectory);
            }
        });

        ffmpegButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EXE" , "*.exe"));
            fileChooser.setTitle("Path to ffmpeg");

            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (chosenFile != null) {
                userSettings.setFfmpegPath(chosenFile);
            }
        });

        precisionSlider.valueProperty().addListener((observable , oldValue , newValue) -> {
            sliderValue.setText(String.valueOf(Math.floor((Double) newValue)));
            userSettings.setPrecision((int) Math.floor((Double) newValue) / 2);
            main = new Tail[(int) Math.pow(userSettings.getPrecision() , 2)];
            CHUNK_WIDTH = (int) (i.getWidth() / userSettings.getPrecision());
            CHUNK_HEIGHT = (int) (i.getHeight() / userSettings.getPrecision());
        });
    }

    private void createRadioButtons() {
        VBox r = new VBox();
        tg = new ToggleGroup();

        // create radiobuttons
        RadioButton r1 = new RadioButton("QuickSort");
        RadioButton r2 = new RadioButton("BubbleSort");
        VBox.setMargin(r1 , new Insets(10));
        VBox.setMargin(r2 , new Insets(10));

        // add radiobuttons to toggle group
        r1.setToggleGroup(tg);
        r2.setToggleGroup(tg);
        r.getChildren().add(r1);
        r.getChildren().add(r2);
        r2.setSelected(true);

        VBox leftVbox = (VBox) (cleanButton.getParent());
        leftVbox.getChildren().add(r);
    }

    private void removeAllTails() {
        gridPane.getChildren().removeAll(gridPane.getChildren());
    }

    //region Utilities methods
    //endregion
}

