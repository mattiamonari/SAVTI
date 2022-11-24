package savti;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import savti.command.ChangeOutputNameCommand;
import savti.command.FramerateCommand;
import savti.command.PrecisionCommand;
import savti.command.VideoDurationCommand;
import savti.utilities.ColorUtilities;
import savti.utilities.ErrorUtilities;

import java.io.IOException;

public class AdvancedSettings extends BorderPane {

    UserSettings userSettings;
    JMetro theme;
    TiledImage image;

    @FXML
    Hyperlink pathLabel;
    @FXML
    CheckBox openVideoBox;
    @FXML
    Button changeOutputName;
    @FXML
    Slider precisionSlider;
    @FXML
    Slider framerateSlider;
    @FXML
    Slider videodurationSlider;
    @FXML
    Label precisionValue;
    @FXML
    Label framerateValue;
    @FXML
    Label videodurationValue;
    @FXML
    Label currentOutName;

    public AdvancedSettings(Stage primaryStage, UserSettings userSettings, JMetro theme, TiledImage image) {
        //Load FXML for the scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AdvancedSettings.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            ErrorUtilities.fxmlLoadError();
        }

        this.theme = new JMetro(this, theme.getStyle());
        this.userSettings = userSettings;
        this.image = image;

        initComponents();

    }

    private void initComponents() {
        if (this.theme.getStyle() == Style.DARK)
            setStyle("-fx-base: black");
        else
            setStyle("-fx-base: white");

        pathLabel.setText("Path to output: " + userSettings.getOutputDirectory().toString());

        updateNameLabel();

        updateSlidersAndBoxes();

        addEventListeners();

        addTooltips();
    }

    private void updateNameLabel() {
        currentOutName.setText("Current output name: " + userSettings.getOutName());
    }

    private void addTooltips() {
        precisionSlider.setTooltip(new Tooltip("This value represent, in percentage, how much the image will be divided.\nA 100% value means that each single tile will be 2x2px."));
    }

    //Fare stesso lavoro che abbiamo fatto con MainWindow?
    private void addEventListeners() {

        changeOutputName.setOnAction(e -> new ChangeOutputNameCommand(userSettings, currentOutName).execute());

        precisionSlider.valueProperty().addListener((observable, oldValue, newValue) -> new PrecisionCommand(newValue, userSettings, image, precisionValue, precisionSlider).execute());


        framerateSlider.valueProperty().addListener((observable, oldValue, newValue) -> new FramerateCommand(newValue, framerateSlider, framerateValue, userSettings).execute());

        videodurationSlider.valueProperty().addListener((observable, oldValue, newValue) -> new VideoDurationCommand(newValue, videodurationSlider, videodurationValue, userSettings).execute());

        openVideoBox.selectedProperty().addListener(event -> userSettings.setOpenFile(openVideoBox.isSelected()));

        pathLabel.setOnAction(event -> userSettings.openOutputDirectory());

    }


    private void updateSlidersAndBoxes() {

        //FRAMERATE SLIDER
        framerateSlider.setValue(userSettings.getFrameRate());
        double newValue = framerateSlider.getValue();
        framerateValue.setText(String.valueOf(Math.floor(newValue)));
        framerateValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue((float) (newValue / 60f)) + ";");
        framerateValue.setText(String.valueOf(Math.floor(newValue)));

        //PRECISION SLIDER
        if (image.getImage() != null)
            precisionSlider.setValue(image.getImage().getWidth() / userSettings.getChunkWidth());
        else
            precisionSlider.setValue(8d);
        newValue = precisionSlider.getValue();
        precisionValue.setText(String.valueOf(Math.floor(newValue)));
        precisionValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue((float) (newValue / 100f)) + ";");

        //DURATION SLIDER
        framerateSlider.setValue(userSettings.getVideoDuration());
        newValue = videodurationSlider.getValue();
        videodurationValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue((float) (newValue / 30f)) + ";");
        videodurationValue.setText(String.valueOf(Math.floor(newValue)));

        openVideoBox.setSelected(userSettings.isOpenFile());
    }
}

