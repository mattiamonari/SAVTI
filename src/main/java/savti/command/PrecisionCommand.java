package savti.command;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import savti.TiledImage;
import savti.UserSettings;
import savti.utilities.ColorUtilities;

public class PrecisionCommand implements Command {

    Number newValue;
    UserSettings userSettings;
    TiledImage image;
    Label precisionValue;
    Slider precisionSlider;

    public PrecisionCommand(Number newValue, UserSettings userSettings, TiledImage image, Label precisionValue, Slider precisionSlider) {
        this.newValue = newValue;
        this.userSettings = userSettings;
        this.image = image;
        this.precisionValue = precisionValue;
        this.precisionSlider = precisionSlider;
    }

    @Override
    public void execute() {
        Node thumb = precisionSlider.lookup(".thumb");
        thumb.setStyle("-fx-background-color: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 100f) + ";");
        precisionValue.setText(String.valueOf(Math.floor((Double) newValue)));
        precisionValue.setStyle("-fx-text-fill: #" + ColorUtilities.getHexFromValue(newValue.intValue() / 100f) + ";");
        if (image.getImage() != null) {
            userSettings.setChunkWidth((int) Math.round(image.getImage().getWidth() / newValue.intValue()));
            userSettings.setChunkHeight((int) Math.round(image.getImage().getHeight() / newValue.intValue()));
            userSettings.setRowsNumber((int) image.getImage().getHeight() / userSettings.getChunkHeight());
            userSettings.setColsNumber((int) image.getImage().getWidth() / userSettings.getChunkHeight());
            image.resizeArray(userSettings.getColsNumber() * userSettings.getRowsNumber());
        }
    }
}
