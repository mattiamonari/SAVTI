package JavaFXVersion.utilities;

import JavaFXVersion.Tile;
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
    /**
     * Divide un'immagine in tante WritableImage, le quali verranno poi inserite nel vettore array
     *
     * @param oldImage Immagine da dividere
     * @param rows Numero di righe in cui l'immagine verrà divisa
     * @param cols Numero di colonne in cui l'immagine verrà divisa
     */
    //? Could it be better to return a Tile[] vector?
    public static void splitImage(Image oldImage , int rows , int cols , Tile[] array) {
        int chunkWidth = (int) oldImage.getWidth() / cols;
        int chunkHeight = (int) oldImage.getHeight() / rows;
        PixelReader reader = oldImage.getPixelReader();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                array[y * cols + x] = new Tile(new WritableImage(reader , x * chunkWidth , y * chunkHeight , chunkWidth , chunkHeight), y * cols + x , x , y);
            }
        }
    }

    public static void fillImage(UserSettings userSettings, Tile[] array, ImageView imageView, int width, int height) {
        File f = writeImage(userSettings, array, (int) array[0].getWidth(), (int) array[0].getHeight(), "TMP");
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

    public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    public static void resetCoordinates(UserSettings userSettings , Tile[] array)
    {
        for (int i = 0; i < userSettings.getColsNumber(); i++) {
            for (int j = 0; j < userSettings.getRowsNumber(); j++) {
                array[i * userSettings.getRowsNumber() + j].setY(i);
                array[i * userSettings.getRowsNumber() + j].setX(j);
            }
        }
    }
}
