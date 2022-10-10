package JavaFXVersion.utilities;

import JavaFXVersion.Tile;
import JavaFXVersion.TiledImage;
import JavaFXVersion.UserSettings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import static JavaFXVersion.utilities.FileUtilities.writeImage;

public class ImageUtilities {

    //? Could it be better to return a Tile[] vector?

    /**
     * Divide un'immagine in tante WritableImage, le quali verranno poi inserite nel vettore array
     *
     * @param oldImage Immagine da dividere
     * @param rows     Numero di righe in cui l'immagine verrà divisa
     * @param cols     Numero di colonne in cui l'immagine verrà divisa
     */
    public static void splitImage(Image oldImage, int cols, int rows, TiledImage newImage) {
        int chunkWidth = (int) oldImage.getWidth() / cols;
        int chunkHeight = (int) oldImage.getHeight() / rows;
        PixelReader reader = oldImage.getPixelReader();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                newImage.setTileAtPosition(new Tile(new WritableImage(reader, x * chunkWidth, y * chunkHeight, chunkWidth, chunkHeight), y * cols + x, x, y), y * cols + x);
            }
        }
    }

    public static void fillImage(UserSettings userSettings, TiledImage image, ImageView imageView, int width, int height) {
        File f = writeImage(userSettings, image.getArray(), (int) image.getArray()[0].getWidth(), (int) image.getArray()[0].getHeight(), "TMP");

        Image tmp = new Image(f.getPath());
        imageView.setImage(tmp);
        if (tmp.getHeight() / height > tmp.getWidth() / width)
            imageView.setFitWidth(width);
        else
            imageView.setFitHeight(height);

    }

    public static void fillImage(Image image, ImageView imageView, int width, int height) {
        imageView.setPreserveRatio(true);
        if (image.getHeight() / height > image.getWidth() / width)
            imageView.setFitHeight(height);
        else
            imageView.setFitWidth(width);

        imageView.setImage(image);
    }

    public static void resetCoordinates(UserSettings userSettings, Tile[] array) {
        for (int i = 0; i < userSettings.getRowsNumber(); i++) {
            for (int j = 0; j < userSettings.getColsNumber(); j++) {
                array[i * userSettings.getColsNumber() + j].setY(i);
                array[i * userSettings.getColsNumber() + j].setX(j);
            }
        }
    }
}
