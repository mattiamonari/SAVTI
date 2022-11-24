package savti.command;

import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import savti.UserSettings;

import java.util.Optional;

public class ChangeOutputNameCommand implements Command {

    UserSettings userSettings;
    Label currentOutName;

    public ChangeOutputNameCommand(UserSettings userSettings, Label currentOutName) {
        this.userSettings = userSettings;
        this.currentOutName = currentOutName;
    }

    @Override
    public void execute() {
        TextInputDialog output = new TextInputDialog();
        output.getDialogPane().getStyleClass().add(".dialog-pane");
        output.getDialogPane().getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
        output.setContentText("Filename: ");
        Optional<String> out = output.showAndWait();
        if (out.isPresent()) {
            if (out.get().endsWith(".mp4"))
                userSettings.setOutName(out.get());
            else
                userSettings.setOutName(out.get() + ".mp4");
        }
        currentOutName.setText("Current output name: " + userSettings.getOutName());
    }
}
