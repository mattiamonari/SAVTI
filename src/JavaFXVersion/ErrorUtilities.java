package JavaFXVersion;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import static javafx.scene.control.Alert.AlertType.*;

public class ErrorUtilities {

    public static void noImageError(){

        Alert alert = new Alert(CONFIRMATION, "Non è presente nessuna immagine!! Caricala prima.", ButtonType.YES,
                ButtonType.NO,
                ButtonType.CANCEL);
        alert.showAndWait();
    }

}
