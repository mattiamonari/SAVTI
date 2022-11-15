package savti.utilities;

import javafx.embed.swing.SwingFXUtils;
import savti.Tile;
import savti.TiledImage;
import savti.UserSettings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static savti.utilities.FileUtilities.writeImage;

public class ImageUtilities {

    //? Could it be better to return a Tile[] vector?

    /**
     * Divide un'immagine in tante WritableImage, le quali verranno poi inserite nel vettore array
     *
     * @param oldImage Immagine da dividere
     * @param rows     Numero di righe in cui l'immagine verrà divisa
     * @param cols     Numero di colonne in cui l'immagine verrà divisa
     */
    public static void splitImage(TiledImage oldImage, int cols, int rows, TiledImage newImage) {
        int chunkWidth = (int) oldImage.getImage().getWidth() / cols;
        int chunkHeight = (int) oldImage.getImage().getHeight() / rows;
        PixelReader reader = oldImage.getImage().getPixelReader();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                newImage.setTileAtPosition(new Tile(new WritableImage(reader, x * chunkWidth, y * chunkHeight, chunkWidth, chunkHeight), y * cols + x, x, y), y * cols + x);
            }
        }
    }


    public static void fillImage(TiledImage image, ImageView imageView, int width, int height) {
        imageView.setPreserveRatio(true);
        if (image.getImage().getHeight() / height > image.getImage().getWidth() / width)
            imageView.setFitHeight(height);
        else
            imageView.setFitWidth(width);

        imageView.setImage(image.getImage());
    }

    public static void fillImageFromArray(TiledImage image, ImageView imageView, int width, int height) {

        BufferedImage finalImage = new BufferedImage((int) image.getImage().getWidth(), (int) image.getImage().getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = finalImage.createGraphics();
        for (Tile t : image.getArray()) {
            BufferedImage tile = SwingFXUtils.fromFXImage(t.getTile(), null);
            graphics2D.drawImage(tile, (int) (t.getX() * t.getTile().getWidth()), (int) (t.getY() * t.getTile().getHeight()), null);
        }
        graphics2D.dispose();

        imageView.setPreserveRatio(true);
        if (image.getImage().getHeight() / height > image.getImage().getWidth() / width)
            imageView.setFitHeight(height);
        else
            imageView.setFitWidth(width);

        imageView.setImage(SwingFXUtils.toFXImage(finalImage, null));
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
