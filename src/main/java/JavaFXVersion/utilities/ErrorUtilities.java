package JavaFXVersion.utilities;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

public class ErrorUtilities {
    public static void noImageError() {
        Alert errorAlert = new Alert(ERROR , "Image is not valid" , ButtonType.OK);
        errorAlert.setHeaderText("Image is not valid");
        errorAlert.setContentText("Select an image to shuffle first!");
        errorAlert.showAndWait();
    }

    public static void alreadyOrderedImage() {
        Alert alert = new Alert(INFORMATION , "The image is already ordered. Shuffle it first!" , ButtonType.OK);
        alert.showAndWait();
    }

    public static void SWW() {
        Alert errorAlert = new Alert(ERROR , "Something went wrong" , ButtonType.OK);
        errorAlert.setHeaderText("We don't know what happened, but we're sorry.");
        errorAlert.showAndWait();
    }
}
