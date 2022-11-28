import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import savti.MainWindow;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainWindowTest extends ApplicationTest {

    private MainWindow mainWindow;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("TESTS");
        stage.setX(Screen.getPrimary().getVisualBounds().getMinX());
        stage.setY(Screen.getPrimary().getVisualBounds().getMinY());
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        mainWindow = new MainWindow(stage);
        stage.setScene(new Scene(mainWindow));
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.show();
        stage.toFront();
    }
    @Test
    public void checkLabelTexts() {
        BorderPane rootNode = (BorderPane) mainWindow.getScene().getRoot();
        Label headerText = from(rootNode).lookup("#headerText").query();
        assertEquals("Welcome to SAVTI", headerText.getText());
    }

    @Test
    public void darkModeTest() {
        BorderPane rootNode = (BorderPane) mainWindow.getScene().getRoot();
        ToggleSwitch toggleSwitch = from(rootNode).lookup("#darkMode").query();
        clickOn(toggleSwitch.lookup(".thumb"));
        System.out.println();
        assertEquals("-fx-base: white", rootNode.getStyle());
        clickOn(toggleSwitch.lookup(".thumb"));
        assertEquals("-fx-base: black", rootNode.getStyle());
    }

    @Test
    public void randomizeTest() {
        BorderPane rootNode = (BorderPane) mainWindow.getScene().getRoot();

    }


}
