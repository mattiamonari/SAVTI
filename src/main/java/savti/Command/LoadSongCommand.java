package savti.Command;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import savti.UserSettings;

import java.io.File;
import java.net.UnknownServiceException;

/**
 * LoadSongCommand is used to create the command to load songs.
 *
 * @author: Daniele Gasparini && Mattia Monari
 * @version: 2022.11.17
 */
public class LoadSongCommand extends Node implements Command{
    UserSettings userSettings ;
    /**
     * Constructor for LoadSongCommand class.
     * @param userSettings are the settings that fill be changed after the load of the song
     */
    public LoadSongCommand(UserSettings userSettings) {
        this.userSettings = userSettings;
    }
    @Override
    public void execute() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
        fileChooser.setTitle("Open Song File");
        File chosenFile = fileChooser.showOpenDialog(getScene().getWindow());
        userSettings.setMusic(chosenFile);
    }
}
