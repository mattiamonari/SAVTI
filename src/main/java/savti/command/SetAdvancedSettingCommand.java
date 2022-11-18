package savti.command;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import savti.AdvancedSettings;
import savti.TiledImage;
import savti.UserSettings;
/**
 * SetAdvanceSettings is used to create the command for the settings of advanced instruction.
 *
 * @author: Daniele Gasparini && Mattia Monari
 * @version: 2022.11.17
 */
public class SetAdvancedSettingCommand extends Node implements Command{
    UserSettings userSettings;
    TiledImage image;
    JMetro theme;
    /**
     * Constructor for AbsoluteCommand class.
     * @param image is the image on which will be applied the settings.
     * @param userSettings are the settings parameters that will be changed.
     * @param theme is the theme of the GUI.
     */
    public SetAdvancedSettingCommand(TiledImage image, UserSettings userSettings,JMetro theme) {
        this.image = image;
        this.userSettings = userSettings;
        this.theme = theme;
    }
    @Override
    public void execute() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(this.getScene().getWindow());
        stage.getIcons().add(new Image("icon.png"));
        Scene scene = new Scene(new AdvancedSettings(stage, userSettings, theme, image));
        stage.setTitle("Advanced Settings");
        stage.setScene(scene);
        stage.showAndWait();
    }
}
