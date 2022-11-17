package savti.Command;

import javafx.scene.Node;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
/**
 * SetDarkModeCommand is used to create the command to set the theme to Dark.
 *
 * @author: Daniele Gasparini && Mattia Monari
 * @version: 2022.11.17
 */

public class SetDarkModeCommand extends Node implements Command {
    JMetro theme;
    /**
     * Constructor for AbsoluteCommand class.
     * @param theme is the GUI theme that will be changed after the execution.
     */
    public SetDarkModeCommand(JMetro theme) {
        this.theme = theme;
    }

    @Override
    public void execute() {
        (observable, oldValue, newValue) -> {
            //Non capisco in questa logica il newValue come faccia ad essere un booleano
            if (newValue) {
                setStyle("-fx-base: black");
                theme.setStyle(Style.DARK);
            } else {
                setStyle("-fx-base: white");
                theme.setStyle(Style.LIGHT);
            }
        }
    }
}