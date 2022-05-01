package JavaFXVersion;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import JavaFXVersion.sorting.BubbleSort;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MainWindow extends BorderPane {


    //region Local variables decleration
    int SCRAPING_COLUMN = 16;
    int SCAPING_ROWS = 16;
    int CHUNK_WIDTH;
    int CHUNK_HEIGHT;
    Tail[] main = new Tail[256];

    //Button = JButton di Java Swing
    Button b;
    Button backToTheStart;
    Button clean;
    //endregion

    //region FXML variables declaration

    //L'annotazione @FXML segnala che la variabile sotto dichiarata è presente nel file .fxml
    //Ovvero nella gui da noi creata
    @FXML
    GridPane gridPane;
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

        loadAndSplitImage();

        addEventListeners();

    }

    //carico l'immagine
    private void loadAndSplitImage() {
        BufferedImage capture = null;
        try {
            capture = ImageIO.read(new File("C:\\Users\\mmatt\\Documents\\bigimage.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image i = SwingFXUtils.toFXImage(capture, null);
        CHUNK_WIDTH = (int) (i.getWidth() / SCRAPING_COLUMN);
        CHUNK_HEIGHT = (int) (i.getHeight() / SCAPING_ROWS);

        gridPane.setPrefSize(i.getWidth(),i.getHeight());
        gridPane.setMaxSize(i.getWidth(),i.getHeight());
        gridPane.setPadding(Insets.EMPTY);


        try {
            splitImage(i, SCAPING_ROWS,SCRAPING_COLUMN);
            fillImage(CHUNK_WIDTH,CHUNK_HEIGHT,SCAPING_ROWS,SCRAPING_COLUMN);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Aggiunge i listener agli eventi dei nodi/elementi
    private void addEventListeners() {

        clean.setOnAction(e-> gridPane.getChildren().removeAll(gridPane.getChildren()));

        b.setOnAction(e -> {
            gridPane.getChildren().removeAll(gridPane.getChildren());
            rand(main); //shuffle(writableimages)
            fillImage(CHUNK_WIDTH,CHUNK_HEIGHT,SCAPING_ROWS,SCRAPING_COLUMN);
        });

        backToTheStart.setOnAction(e -> Platform.runLater(() -> {
            new BubbleSort().sort(main,gridPane);
        }));
    }

    //Creazione dei componenti
    private void initComponents() {
        b = new Button("Randomizza l'immagine");
        backToTheStart = new Button("Ricomponi l'immagine");
        clean = new Button("Cancella l'immagine");

        //aggiunto Bg per debug
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        this.setLeft(b);
        this.setRight(backToTheStart);
        this.setBottom(clean);
    }

    //region Utilities methods

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
                main[x*rows+y].setFitHeight(chunkHeight/4);
                main[x*rows+y].setFitWidth(chunkWidth/4);
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

