package savti;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.controlsfx.control.ToggleSwitch;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.SeekableByteChannel;
import savti.command.*;
import savti.sorting.BubbleSort;
import savti.sorting.SortAlgorithm;
import savti.utilities.ErrorUtilities;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static savti.utilities.ImageUtilities.fillImage;

public class MainWindow extends BorderPane {

    //region Local variables' declaration
    //TODO CREATE A SINGLE CLASS SEEKABLE+AWT
    SeekableByteChannel out = null;
    AWTSequenceEncoder encoder = null;
    UserSettings userSettings;
    SortAlgorithm algorithm;
    JMetro theme;
    TiledImage image;

    //endregion

    //region FXML variables declaration
    @FXML
    ImageView imageView;
    @FXML
    Label headerText;
    @FXML
    ToggleSwitch darkMode;
    @FXML
    VBox menuVBox;

    MainVBox mainVBox;

    MainMenu mainMenu;

    AlgorithmProgressBar algorithmProgressBar;
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

        mainVBox = new MainVBox();

        mainMenu = new MainMenu();

        this.setLeft(mainVBox);

        Node tmp = menuVBox.getChildren().get(0);

        menuVBox.getChildren().set(0, mainMenu);

        menuVBox.getChildren().add(1, tmp);

        HBox.setHgrow(headerText, Priority.ALWAYS);

        BorderPane.setAlignment(headerText.getParent(), Pos.CENTER);

        addCssFiles();

        theme = new JMetro(this, Style.DARK);

        algorithmProgressBar = new AlgorithmProgressBar("AlgoName");
    }

    private void addCssFiles() {
        setStyle("-fx-base: black");
        this.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
    }
    private void setAdvancedSettingsListener() {
        mainMenu.getAdvSett().setOnAction(e -> {
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
    }
    private void loadImageListener() {
        mainMenu.getImageLoaderItem().setOnAction(e -> {
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
                fillImage(image, imageView, (int) Math.round(this.getScene().getWidth() - mainVBox.getWidth() - 20), (int) Math.round(mainVBox.getHeight() - 50));
            }
        });
    }

    private void loadSongListener() {
        mainMenu.getSongLoaderItem().setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
            fileChooser.setTitle("Open Song File");
            File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
            userSettings.setMusic(chosenFile);
        });
    }

    private void setDarkModeListener() {
        darkMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setStyle("-fx-base: black");
                theme.setStyle(Style.DARK);
            } else {
                setStyle("-fx-base: white");
                theme.setStyle(Style.LIGHT);
            }
        });
    }

    //Aggiunge i listener agli eventi dei nodi/elementi
    private void addEventListeners() {

        //Is this okay?
        mainVBox.getCleanButton().setOnAction(e -> new CleanImageCommand(image, userSettings, imageView).execute());
        mainVBox.getOutputButton().setOnAction(e -> new SetOutputPathCommand(userSettings, mainVBox).execute());
        //TODO IMPLEMENT A CLASS FOR OUT AND ENCODER TOGHETER, OTHERWISE IT WILL NOT WORK
        mainVBox.getSortingButton().setOnAction(e -> new SortImageCommand(mainVBox, image, encoder, out, userSettings, imageView, algorithm, algorithmProgressBar, mainMenu).execute());
        mainVBox.getRandomizeButton().setOnAction(e -> new RandomShuffleCommand(image, userSettings, out, encoder, imageView, algorithmProgressBar, mainVBox).execute());
        mainVBox.getBurstMode().setOnAction(e -> new BurstModeSortingCommand(mainVBox, image, userSettings, imageView, mainMenu).execute());
        mainVBox.getBurstMode().setOnMouseEntered(e -> new BurstModeToolTipComand(mainVBox.getBurstMode()).execute());
        mainVBox.getPathLabel().setOnAction(e -> new ClickToPathCommand(userSettings).execute());

        loadImageListener();
        loadSongListener();
        setDarkModeListener();
        setAdvancedSettingsListener();

    }
}
