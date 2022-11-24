package savti;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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
            ErrorUtilities.fxmlLoadError();
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
