package savti;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class MainVBox extends VBox {

    JMetro jMetro;
    @FXML
    private Button randomizeButton;
    @FXML
    private Button sortingButton;
    @FXML
    private Button cleanButton;
    @FXML
    private Button outputButton;
    @FXML
    private ComboBox<String> chooseAlgo;
    @FXML
    private Hyperlink pathLabel;
    @FXML
    private Button burstMode;

    public MainVBox() {
        //Load FXML for the scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainVBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        createComboBox();
        setStyle("-fx-base: black");
        jMetro = new JMetro(this, Style.DARK);

        burstMode.setTooltip(new Tooltip("Questo bottone serve per fare un video di tutti gli algoritmi"));
        this.setId("mainVBox");
    }


    /**
     * Adds all the possible algorithms to the combobox using an immutable list of strings.
     */
    private void createComboBox() {
        List<String> algos = Arrays.asList("BubbleSort", "CocktailSort", "CycleSort", "GnomeSort", "InsertionSort", "MergeSort", "QuickSort", "RadixSort", "SelectionSort");
        chooseAlgo.setItems(FXCollections.observableList(algos));
        chooseAlgo.setValue("BubbleSort");
    }

    /**
     * @param disable If true, disable all the button in the main window, if true enable them all.
     */
    public void disableOrEnableAll(boolean disable) {
        randomizeButton.setDisable(disable);
        sortingButton.setDisable(disable);
        cleanButton.setDisable(disable);
        outputButton.setDisable(disable);
        chooseAlgo.setDisable(disable);
        pathLabel.setDisable(disable);
        burstMode.setDisable(disable);
    }

    public Button getRandomizeButton() {
        return randomizeButton;
    }

    public Button getSortingButton() {
        return sortingButton;
    }

    public Button getCleanButton() {
        return cleanButton;
    }

    public Button getOutputButton() {
        return outputButton;
    }

    public ComboBox<String> getChooseAlgo() {
        return chooseAlgo;
    }

    public Hyperlink getPathLabel() {
        return pathLabel;
    }

    public Button getBurstMode() {
        return burstMode;
    }


    public void setLightStyle() {
        setStyle("-fx-base: white");
        jMetro.setStyle(Style.LIGHT);
    }

    public void setDarkStyle() {
        setStyle("-fx-base: black");
        jMetro.setStyle(Style.DARK);
    }
}
