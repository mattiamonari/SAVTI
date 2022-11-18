package savti.command;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import savti.TiledImage;
import savti.UserSettings;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static savti.utilities.ImageUtilities.fillImage;

/**
 * LoadImageCommand is used to create the command for the load image button.
 *
 * @author: Daniele Gasparini && Mattia Monari
 * @version: 2022.11.17
 */
public class LoadImageCommand extends Node implements Command {

    UserSettings userSettings;
    TiledImage image;
    @FXML
    ImageView imageView;
    Button cleanButton;
    /**
     * Constructor for LoadImageCommand class.
     * @param image is the TiledImage that will be loaded.
     * @param userSettings are the settings that will be modified.
     * @param imageView is the GUI that will display the image.
     * @param cleanButton is used to get the width of the parent class.
     */
    public LoadImageCommand(TiledImage image,UserSettings userSettings,javafx.scene.image.ImageView imageView,Button cleanButton) {
        this.image = image;
        this.userSettings = userSettings;
        this.imageView = imageView;
        this.cleanButton = cleanButton;
    }

    @Override
    public void execute() {
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
            fillImage(image, imageView, (int) Math.round(this.getScene().getWidth() - ((VBox) cleanButton.getParent()).getWidth() - 20), (int) Math.round(((VBox) cleanButton.getParent()).getHeight() - 50));
        }

    }
}
