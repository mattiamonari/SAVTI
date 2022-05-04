package JavaFXVersion;

import JavaFXVersion.sorting.BubbleSort;
import JavaFXVersion.sorting.QuickSort;
import JavaFXVersion.sorting.SortAlgorithm;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainWindow extends BorderPane {


    //region Local variables decleration
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
    Button backToTheStart;
    @FXML
    Button cleanButton;
    @FXML
    Slider precisionSlider;
    @FXML
    Label sliderValue;

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
        // enable the marks
        precisionSlider.setShowTickMarks(true);

        // enable the Labels
        precisionSlider.setShowTickLabels(true);

        initComponents();

        loadAndSplitImage("res/bigimage.jpg");

        addEventListeners();


    }

    //carico l'immagine
    private void loadAndSplitImage(String pathname) {
        BufferedImage capture = null;
        try {
            capture = ImageIO.read(new File(pathname));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage dimg = Scalr.resize(capture, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, 1250, 700);

        i = SwingFXUtils.toFXImage(dimg, null);
        CHUNK_WIDTH = (int) (dimg.getWidth() / PRECISION);
        CHUNK_HEIGHT = (int) (dimg.getHeight() / PRECISION);

        gridPane.setPadding(Insets.EMPTY);


        try {
            removeAllTails();
            splitImage(i, PRECISION,PRECISION);
            fillImage(CHUNK_WIDTH,CHUNK_HEIGHT,PRECISION,PRECISION);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //carico l'immagine overloaddato per usare il File restituito dalla finestra di dialogo
    private void loadAndSplitImage(File file) {

        BufferedImage capture = null;

        try {
            capture = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage dimg = Scalr.resize(capture, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, 1250, 700);

        Image i = SwingFXUtils.toFXImage(dimg, null);
        CHUNK_WIDTH = (int) (dimg.getWidth() / PRECISION);
        CHUNK_HEIGHT = (int) (dimg.getHeight() / PRECISION);

        gridPane.setPrefSize(i.getWidth(),i.getHeight());
        gridPane.setMaxSize(i.getWidth(),i.getHeight());
        gridPane.setPadding(Insets.EMPTY);


        try {
            removeAllTails();
            splitImage(i, PRECISION,PRECISION);
            fillImage(CHUNK_WIDTH,CHUNK_HEIGHT,PRECISION,PRECISION);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Aggiunge i listener agli eventi dei nodi/elementi
    private void addEventListeners() {

        cleanButton.setOnAction(e-> {
            removeAllTails();
            Arrays.fill(main , null);
        });

        randomizeButton.setOnAction(e -> {
            algorithm.killTask();
            if(Arrays.stream(main).allMatch(Objects::isNull) && !(i ==null)){
                splitImage(i, PRECISION, PRECISION);
            }
            removeAllTails();
            rand(main); //shuffle(writableimages)
            fillImage(CHUNK_WIDTH,CHUNK_HEIGHT,PRECISION,PRECISION );
        });

        backToTheStart.setOnAction(e -> Platform.runLater(() -> {
            //Ha senso farlo sempre?
            if (tg.getSelectedToggle() != null) {
                if (((RadioButton) tg.getSelectedToggle()).getText().equals("QuickSort"))
                    algorithm = new QuickSort();
                else
                    algorithm = new BubbleSort();
            }
            //Se tutti gli oggetti del vettore main sono diversi da NULL, e non c'è già un SortingThread attivo
            // faccio partire l'ordinamento
            if(!algorithm.isThreadAlive() && !Arrays.stream(main).allMatch(Objects::isNull))
                algorithm.sort(main,gridPane);
        }));

        imageLoaderItem.setOnAction(e->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );
            fileChooser.setTitle("Open Resource File");
            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (chosenFile != null) {
                loadAndSplitImage(chosenFile);
            }
        });

        precisionSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            sliderValue.setText(String.valueOf(Math.floor((Double) newValue)));
            PRECISION = (int) Math.floor((Double) newValue)/2;
            main = new Tail[(int)Math.pow(PRECISION, 2)];
            CHUNK_WIDTH = (int) (i.getWidth() / PRECISION);
            CHUNK_HEIGHT = (int) (i.getHeight() / PRECISION);
        });

    }

    private void removeAllTails() {
        gridPane.getChildren().removeAll(gridPane.getChildren());
    }

    //Creazione dei componenti
    private void initComponents() {

        algorithm = new BubbleSort();

        tg = new ToggleGroup();

        PRECISION = 8;

        main = new Tail[64];

        createRadioButtons();
    }

    //region Utilities methods

    private void createRadioButtons(){

        VBox r = new VBox();

        // create radiobuttons
        RadioButton r1 = new RadioButton("QuickSort");
        RadioButton r2 = new RadioButton("BubbleSort");

        VBox.setMargin(r1, new Insets(10));
        VBox.setMargin(r2, new Insets(10));

        // add radiobuttons to toggle group
        r1.setToggleGroup(tg);
        r2.setToggleGroup(tg);

        r.getChildren().add(r1);
        r.getChildren().add(r2);

        r2.setSelected(true);

        VBox leftVbox = (VBox) (cleanButton.getParent());
        leftVbox.getChildren().add(r);

    }

    /**
     * Divide un'immagine in tante WritableImage, le quali verranno poi inserite nel vettore main
     * @param oldImage Immagine da dividere
     * @param rows Numero di righe in cui l'immagine verrà divisa
     * @param cols Numero di colonne in cui l'immagine verrà divisa
     */
    public void splitImage(Image oldImage, int rows, int cols) {

        int chunkWidth = (int) oldImage.getWidth() / cols;
        int chunkHeight = (int) oldImage.getHeight() / rows;
        PixelReader reader = oldImage.getPixelReader();
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                main[x*rows + y] = new Tail(new WritableImage(reader, x*chunkWidth, y*chunkHeight, chunkWidth , chunkHeight),
                        x*rows + y, x, y);
            }
        }
    }

    /**
     * Questa funzione aggiunge al GridPane le varie Tail prendendole dal vettore globale main
     * @param chunkWidth La larghezza in pixel dei chunk
     * @param chunkHeight L'altezza in pixel dei chunk
     * @param rows Il numero di colonne in cui è stata divisa l'immagine
     * @param cols Il numero di righe in cui è stata divisa l'immagine
     */
    public void fillImage(int chunkWidth, int chunkHeight, int rows, int cols){
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                main[x*rows+y].setPreserveRatio(true);
                main[x*rows+y].setFitHeight(chunkHeight);
                main[x*rows+y].setFitWidth(chunkWidth);
                main[x*rows+y].setOpacity(0.8);
                //Qua aggiungiamo la tail presa dal vettore
                //Nella griglia nella riga x e alla colonna y
                gridPane.add(main[x*rows+y], x,y);
            }
        }
    }

    /**
     * Questa funzione randomizza un vettore di Tail utilizzato la classe Random
     * @param array Il vettore da randomizzare
     */
    static void rand(Tail[] array) {
        // Creating object for Random class
        Random rd = new Random();

        // Starting from the last element and swapping one by one.
        for (int i = array.length - 1; i > 0; i--) {

            // Pick a random index from 0 to i
            int j = rd.nextInt(i+1);

            // Swap array[i] with the element at random index
            Tail temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    //endregion


}

