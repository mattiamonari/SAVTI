package savti.command;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import savti.MainVBox;
import savti.TiledImage;
import savti.UserSettings;
import savti.utilities.ErrorUtilities;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static savti.utilities.ImageUtilities.fillImage;

/**
 * LoadImageCommand is used to create the command for the load image button.
 *
 * @author Daniele Gasparini && Mattia Monari
 * @version 2022.11.17
 */
public class LoadImageCommand extends Node implements Command {

    UserSettings userSettings;
    TiledImage image;
    @FXML
    ImageView imageView;

    MainVBox mainVBox;

    /**
     * Constructor for LoadImageCommand class.
     *
     * @param image        is the TiledImage that will be loaded.
     * @param userSettings are the settings that will be modified.
     * @param imageView    is the GUI that will display the image.
     * @param mainVBox     is used to calculate the optimal size for the image
     */
    public LoadImageCommand(TiledImage image, UserSettings userSettings, ImageView imageView, MainVBox mainVBox) {
        this.image = image;
        this.userSettings = userSettings;
        this.imageView = imageView;
        this.mainVBox = mainVBox;
    }

    @Override
    public void execute() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png"));
        fileChooser.setTitle("Open Resource File");
        File chosenFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (chosenFile != null) {
            try {
                image.setImage(SwingFXUtils.toFXImage(ImageIO.read(chosenFile), null));
            } catch (IOException ex) {
                ErrorUtilities.loadImageError();
            }
            //Set the intial precision to a default value
            userSettings.setChunkWidth((int) Math.round(image.getImage().getWidth() / 8));
            userSettings.setChunkHeight((int) Math.round(image.getImage().getHeight() / 8));
            userSettings.setRowsNumber((int) image.getImage().getHeight() / userSettings.getChunkHeight());
            userSettings.setColsNumber((int) image.getImage().getWidth() / userSettings.getChunkHeight());
            image.resizeArray(userSettings.getColsNumber() * userSettings.getRowsNumber());
            fillImage(image, imageView, (int) Math.round(imageView.getScene().getWidth() - mainVBox.getWidth() - 20), (int) Math.round(mainVBox.getHeight() - 30));
        }

    }
}
