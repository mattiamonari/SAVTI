package savti.Command;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class BurstModeToolTipComand implements Command {
    @FXML
    Button burstMode;
    public BurstModeToolTipComand(Button burstMode) {
        this.burstMode = burstMode;
    }
    @Override
    public void execute() {
        Tooltip t = new Tooltip("Questo bottone serve per fare un video di tutti gli algoritmi");
        t.setStyle(" -fx-font-size: 10pt; -fx-font-style: italic;");
        burstMode.setTooltip(t);
    }
}
