package savti.command;

import javafx.stage.FileChooser;
import savti.MainWindow;
import savti.UserSettings;

import java.io.File;

/**
 * LoadSongCommand is used to create the command to load songs.
 *
 * @author Daniele Gasparini && Mattia Monari
 * @version 2022.11.17
 */
public class LoadSongCommand implements Command {
    MainWindow mainWindow;
    UserSettings userSettings;

    /**
     * Constructor for LoadSongCommand class.
     *
     * @param userSettings are the settings that fill be changed after the load of the song
     */
    public LoadSongCommand(UserSettings userSettings, MainWindow mainWindow) {
        this.userSettings = userSettings;
        this.mainWindow = mainWindow;
    }

    @Override
    public void execute() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
        fileChooser.setTitle("Open Song File");
        File chosenFile = fileChooser.showOpenDialog(mainWindow.getScene().getWindow());
        userSettings.setMusic(chosenFile);
    }
}
