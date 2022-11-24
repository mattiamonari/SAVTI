package savti.command;

import savti.UserSettings;
import savti.utilities.ErrorUtilities;

import java.awt.*;
import java.io.IOException;

/**
 * ClickToPathCommand is used to create the command to reach the path on their device.
 *
 * @author Daniele Gasparini && Mattia Monari
 * @version 2022.11.17
 */

public class ClickToPathCommand implements Command {
    /**
     * Constructor for ClickToPathCommand class.
     *
     * @param userSettings are the settings that fill be changed after the load of the song
     */

    private final UserSettings userSettings;

    public ClickToPathCommand(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    @Override
    public void execute() {
        if (userSettings.isOutputDirectory())
            try {
                Desktop.getDesktop().open(userSettings.getOutputDirectory());
            } catch (IOException e) {
                ErrorUtilities.somethingWentWrong();
            }
    }
}
