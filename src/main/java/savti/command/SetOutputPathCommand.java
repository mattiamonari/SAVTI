package savti.command;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.DirectoryChooser;
import savti.MainVBox;
import savti.UserSettings;

import java.io.File;
/**
 * SetOutputPathCommand is used to the set the output path of the video that is created by the program.
 *
 * @author: Daniele Gasparini && Mattia Monari
 * @version: 2022.11.17
 */
public class SetOutputPathCommand extends Node implements Command{
    private UserSettings userSettings;
    private MainVBox mainVBox;
    /**
     * Constructor for SetOutputCommand class.
     * @param userSettings are the settings that fill be changed after the load of the song
     * @param mainVBox is the VBox containing all the nodes on the left of the main window
     */
    public SetOutputPathCommand(UserSettings userSettings, MainVBox mainVBox){
        this.userSettings = userSettings;
        this.mainVBox = mainVBox;
    }
    @Override
    public void execute() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose where to save your images!");
        File chosenDirectory = directoryChooser.showDialog(getScene().getWindow());
        if (chosenDirectory != null) {
            userSettings.setOutputDirectory(chosenDirectory);
            mainVBox.getPathLabel().setText("Path to output: " + userSettings.getOutputDirectory().toString());
            mainVBox.getOutputButton().setStyle("");
        }
    }
}
