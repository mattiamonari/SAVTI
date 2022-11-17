package savti.Command;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.DirectoryChooser;
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
    @FXML
    private Hyperlink pathLabel;
    private Button outputButton;
    /**
     * Constructor for SetOutputCommand class.
     * @param userSettings are the settings that fill be changed after the load of the song
     * @param pathlabel is the hyperlink to get the output path on your device.
     * @param outputButton is the output button that will be disabled after the execution of the command
     */
    public SetOutputPathCommand(UserSettings userSettings, Hyperlink pathlabel,Button outputButton){
        this.userSettings = userSettings;
        this.pathLabel = pathlabel;
        this.outputButton = outputButton;
    }
    @Override
    public void execute() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose where to save your images!");
        File chosenDirectory = directoryChooser.showDialog(getScene().getWindow());
        if (chosenDirectory != null) {
            userSettings.setOutputDirectory(chosenDirectory);
            pathLabel.setText("Path to output: " + userSettings.getOutputDirectory().toString());
            outputButton.setStyle("");
        }
    }
}
