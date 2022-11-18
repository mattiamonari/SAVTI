package savti;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import savti.utilities.ErrorUtilities;

import java.io.IOException;

public class MainMenu extends MenuBar {

    @FXML
    private MenuItem imageLoaderItem;
    @FXML
    private MenuItem songLoaderItem;
    @FXML
    private MenuItem advSett;

    public MainMenu() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            ErrorUtilities.FXMLLoadError();
        }
    }

    public MenuItem getImageLoaderItem() {
        return imageLoaderItem;
    }

    public MenuItem getSongLoaderItem() {
        return songLoaderItem;
    }

    public MenuItem getAdvSett() {
        return advSett;
    }

}
