package savti.command;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import savti.UserSettings;
import savti.utilities.ColorUtilities;

public class VideoDurationCommand implements Command {
    Number newValue;

    Slider videoDurationSlider;
    Label videoDurationLabel;

    UserSettings userSettings;

    public VideoDurationCommand(Number newValue, Slider videoDurationSlider, Label videoDurationLabel, UserSettings userSettings) {
        this.videoDurationLabel = videoDurationLabel;
        this.videoDurationSlider = videoDurationSlider;
        this.newValue = newValue;
        this.userSettings = userSettings;
    }

    @Override
    public void execute() {
        Node thumb = videoDurationSlider.lookup(".thumb");
        thumb.setStyle("-fx-background-color: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 30f) + ";");
        videoDurationLabel.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 30f) + ";");
        videoDurationLabel.setText(String.valueOf(Math.floor((Double) newValue)));
        userSettings.setVideoDuration((int) Math.floor(2f * newValue.doubleValue()));
    }
}
