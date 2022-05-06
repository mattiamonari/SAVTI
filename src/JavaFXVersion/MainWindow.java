package JavaFXVersion;

import JavaFXVersion.sorting.BubbleSort;
import JavaFXVersion.sorting.QuickSort;
import JavaFXVersion.sorting.SortAlgorithm;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static JavaFXVersion.ImageUtilities.fillImage;
import static JavaFXVersion.ImageUtilities.splitImage;
import static JavaFXVersion.sorting.SortAlgorithm.rand;

public class MainWindow extends BorderPane {
    //region Local variables decleration
    //? Question is, do we really need all these variables?
    int PRECISION;
    int CHUNK_WIDTH;
    int CHUNK_HEIGHT;
    Tail[] main;
    SortAlgorithm algorithm;
    Image i;
    ToggleGroup tg;
    //endregion
    //region FXML variables declaration
    //L'annotazione @FXML segnala che la variabile sotto dichiarata è presente nel file .fxml
    //Ovvero nella gui da noi creata
    @FXML
    GridPane gridPane;
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
    Button stopSorting;
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
        initComponents();
        loadAndSplitImage("res/bigimage.jpg");
        addEventListeners();
    }

    //Creazione dei componenti
    private void initComponents() {
        algorithm = new BubbleSort();
        tg = new ToggleGroup();
        PRECISION = 8;
        main = new Tail[64];
        createRadioButtons();
        // enable the marks
        precisionSlider.setShowTickMarks(true);
        // enable the Labels
        precisionSlider.setShowTickLabels(true);
    }

    //? Should we move loadAndSplitImage inside ImageUtilities class?
    //carico l'immagine
    private void loadAndSplitImage(String pathname) {
        BufferedImage capture = null;
        try {
            capture = ImageIO.read(new File(pathname));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage dimg = Scalr.resize(capture , Scalr.Method.ULTRA_QUALITY , Scalr.Mode.FIT_TO_HEIGHT , 1250 , 700);
        i = SwingFXUtils.toFXImage(dimg , null);
        CHUNK_WIDTH = dimg.getWidth() / PRECISION;
        CHUNK_HEIGHT = dimg.getHeight() / PRECISION;
        gridPane.setPadding(Insets.EMPTY);
        try {
            removeAllTails();
            splitImage(i , PRECISION , PRECISION , main);
            fillImage(CHUNK_WIDTH , CHUNK_HEIGHT , PRECISION , PRECISION , main , gridPane);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //carico l'immagine overloaddato per usare il File restituito dalla finestra di dialogo
    private void loadAndSplitImage(File file, int width) {
        BufferedImage capture = null;
        try {
            capture = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage dimg = Scalr.resize(capture , Scalr.Method.ULTRA_QUALITY , Scalr.Mode.FIT_TO_WIDTH , width,700);
        i = SwingFXUtils.toFXImage(dimg , null);
        CHUNK_WIDTH = dimg.getWidth() / PRECISION;
        CHUNK_HEIGHT = dimg.getHeight() / PRECISION;
        gridPane.setPrefSize(i.getWidth() , i.getHeight());
        gridPane.setMaxSize(i.getWidth() , i.getHeight());
        gridPane.setPadding(Insets.EMPTY);
        try {
            removeAllTails();
            splitImage(i , PRECISION , PRECISION , main);
            fillImage(CHUNK_WIDTH , CHUNK_HEIGHT , PRECISION , PRECISION , main , gridPane);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
            if (!(i == null)) {
                algorithm.killTask();
                splitImage(i , PRECISION , PRECISION , main);
                removeAllTails();
                rand(main); //shuffle(writableimages)
                fillImage(CHUNK_WIDTH , CHUNK_HEIGHT , PRECISION , PRECISION , main , gridPane);
            }
        });

        sortingButton.setOnAction(e -> Platform.runLater(() -> {
            if (!algorithm.isThreadAlive() && !Arrays.stream(main).allMatch(Objects::isNull)) {

                //disableButtons();

                //Ha senso farlo sempre?
                if (tg.getSelectedToggle() != null) {
                    if (((RadioButton) tg.getSelectedToggle()).getText().equals("QuickSort"))
                        algorithm = new QuickSort();
                    else
                        algorithm = new BubbleSort();
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

//        stopSorting.setOnAction(e -> {
//            algorithm.killTask();
//            enableButtons();
//        });

        precisionSlider.valueProperty().addListener((observable , oldValue , newValue) -> {
            sliderValue.setText(String.valueOf(Math.floor((Double) newValue)));
            PRECISION = (int) Math.floor((Double) newValue) / 2;
            main = new Tail[(int) Math.pow(PRECISION , 2)];
            CHUNK_WIDTH = (int) (i.getWidth() / PRECISION);
            CHUNK_HEIGHT = (int) (i.getHeight() / PRECISION);
        });
    }

    private void createRadioButtons() {
        VBox r = new VBox();
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

    private void disableButtons(){
        sortingButton.setDisable(true);
        cleanButton.setDisable(true);
        randomizeButton.setDisable(true);
        precisionSlider.setDisable(true);
        stopSorting.setDisable(false);
    }

    private void enableButtons(){
        precisionSlider.setDisable(false);
        sortingButton.setDisable(false);
        cleanButton.setDisable(false);
        randomizeButton.setDisable(false);
        stopSorting.setDisable(true);
    }

    //region Utilities methods
    //endregion
}

