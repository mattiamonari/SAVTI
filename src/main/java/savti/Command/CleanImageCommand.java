package savti.Command;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import savti.TiledImage;
import savti.UserSettings;

import static savti.utilities.FileUtilities.deleteAllPreviousFiles;

/**
 * CleanImageCommand is used to create the command for the clean image button.
 *
 * @author: Daniele Gasparini && Mattia Monari
 * @version: 2022.11.17
 */
public class CleanImageCommand implements Command{
    TiledImage image;
    UserSettings userSettings;
    @FXML
    ImageView imageView;
    /**
     * Constructor for the CleanImageCommand class.
     * @param image is the image that will be removed.
     * @param userSettings are the user settings that will be reset.
     * @param imageView is the node used to paint the image in the GUI that will be cleared.
     */
    public CleanImageCommand(TiledImage image, UserSettings userSettings, javafx.scene.image.ImageView imageView) {
        this.image = image;
        this.userSettings = userSettings;
        this.imageView = imageView;
    }
    @Override
    public void execute() {
        image.clearImage();
        imageView.setImage(null);
        deleteAllPreviousFiles(userSettings);
    }
}
