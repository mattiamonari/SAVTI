package savti;

import savti.utilities.ColorUtilities;
import savti.utilities.ErrorUtilities;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class AdvancedSettings extends BorderPane {

    UserSettings userSettings;
    JMetro theme;
    TiledImage image;

    @FXML
    Hyperlink pathLabel;
    @FXML
    CheckBox openVideoBox;
    @FXML
    CheckBox saveImageBox;
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            ErrorUtilities.FXMLLoadError();
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

    private void addEventListeners() {

        changeOutputName.setOnAction((e -> {
            TextInputDialog output = new TextInputDialog();
            output.getDialogPane().getStyleClass().add(".dialog-pane");
            output.getDialogPane().getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
            output.setContentText("Filename: ");
            Optional<String> out = output.showAndWait();
            if (out.isPresent()) {
                if (out.get().endsWith(".mp4"))
                    userSettings.setOutName(out.get());
                else
                    userSettings.setOutName(out.get() + ".mp4");
            }
            updateNameLabel();
        }));

        saveImageBox.selectedProperty().addListener(event -> userSettings.setSaveImage(saveImageBox.isSelected()));

        precisionSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Node thumb = precisionSlider.lookup(".thumb");
            thumb.setStyle("-fx-background-color: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 100f) + ";");
            precisionValue.setText(String.valueOf(Math.floor((Double) newValue)));
            precisionValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 100f) + ";");
            if (image.getImage() != null) {
                userSettings.setChunkWidth((int) Math.round(image.getImage().getWidth() / newValue.intValue()));
                userSettings.setChunkHeight((int) Math.round(image.getImage().getHeight() / newValue.intValue()));
                userSettings.setRowsNumber((int) image.getImage().getHeight() / userSettings.getChunkHeight());
                userSettings.setColsNumber((int) image.getImage().getWidth() / userSettings.getChunkHeight());
                image.resizeArray(userSettings.getColsNumber() * userSettings.getRowsNumber());
            }
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
            userSettings.setVideoDuration((int) Math.floor(2f * newValue.doubleValue()));
        });

        openVideoBox.selectedProperty().addListener(event -> userSettings.setOpenFile(openVideoBox.isSelected()));

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

        saveImageBox.setSelected(userSettings.getSaveImage());

        openVideoBox.setSelected(userSettings.isOpenFile());
    }
}

