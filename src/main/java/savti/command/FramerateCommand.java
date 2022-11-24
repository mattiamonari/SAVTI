package savti.command;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import savti.UserSettings;
import savti.utilities.ColorUtilities;

public class FramerateCommand implements Command {

    Number newValue;
    Slider framerateSlider;
    Label framerateValue;
    UserSettings userSettings;

    public FramerateCommand(Number newValue, Slider framerateSlider, Label framerateValue, UserSettings userSettings) {
        this.newValue = newValue;
        this.framerateSlider = framerateSlider;
        this.framerateValue = framerateValue;
        this.userSettings = userSettings;
    }

    @Override
    public void execute() {
        Node thumb = framerateSlider.lookup(".thumb");
        thumb.setStyle("-fx-background-color: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 60f) + ";");
        framerateValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 60f) + ";");
        userSettings.setFrameRate((int) Math.floor((Double) newValue));
        framerateValue.setText(String.valueOf(Math.floor((Double) newValue)));
    }
}
