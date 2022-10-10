package JavaFXVersion.utilities;

import JavaFXVersion.Tile;
import JavaFXVersion.TiledImage;
import JavaFXVersion.UserSettings;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ProgressBar;
import net.bramp.ffmpeg.progress.Progress;
import org.jcodec.api.awt.AWTSequenceEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileUtilities {

    public static void deleteAllPreviousFiles(UserSettings userSettings) {
        File directory = userSettings.getOutputDirectory();
        if (directory.listFiles() != null) {
            for (File f : directory.listFiles()) {
                if (f.getName().startsWith("final")) {
                    if (!f.delete()) ErrorUtilities.SWW();
                }
            }
        }
    }

//    public static File writeImage(UserSettings userSettings, Tile[] array, int chunkWidth, int chunkHeight, long index, long comparisons, long swaps, double fontSize) {
//        int rows = userSettings.getRowsNumber();
//        int cols = userSettings.getColsNumber();
//        int width = chunkWidth * cols;
//        int heigth = chunkHeight * rows;
//        if (width % 2 != 0) width++;
//        if (heigth % 2 != 0) heigth++;
//        BufferedImage finalImage = new BufferedImage(width, heigth, BufferedImage.TYPE_3BYTE_BGR);
//        Graphics2D graphics2D = finalImage.createGraphics();
//        for (Tile t : array) {
//            BufferedImage tile = SwingFXUtils.fromFXImage(t.getTile(), null);
//            graphics2D.drawImage(tile, t.getX() * chunkWidth, t.getY() * chunkHeight, null);
//        }
//        graphics2D.setFont(new Font("Segoe UI Bold", Font.BOLD, (int) fontSize));
//        graphics2D.drawString("Comparisons = " + comparisons, 20, (int) (fontSize + fontSize / 5));
//        graphics2D.drawString("Swaps = " + swaps, 20, (int) (2 * (fontSize + fontSize / 5)));
//        graphics2D.dispose();
//        if (!userSettings.getOutputDirectory().isDirectory())
//            if (!userSettings.getOutputDirectory().mkdir()) ErrorUtilities.SWW();
//        File f = new File(userSettings.getOutputDirectory().getAbsolutePath() + "\\final" + index + ".jpg");
//
//        try {
//            ImageIO.write(finalImage, "JPG", f);
//        } catch (IOException imageWriteException) {
//            ErrorUtilities.writeError();
//        }
//        return f;
//    }
//
    public static File writeImage(UserSettings userSettings, Tile[] array, int chunkWidth, int chunkHeight, String fileName) {
        int rows = userSettings.getRowsNumber();
        int cols = userSettings.getColsNumber();
        int width = chunkWidth * cols;
        int heigth = chunkHeight * rows;
        if (width % 2 != 0) width++;
        if (heigth % 2 != 0) heigth++;
        BufferedImage finalImage = new BufferedImage(width, heigth, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = finalImage.createGraphics();
        for (Tile t : array) {
            BufferedImage tile = SwingFXUtils.fromFXImage(t.getTile(), null);
            graphics2D.drawImage(tile, t.getX() * chunkWidth, t.getY() * chunkHeight, null);
        }
        graphics2D.dispose();
        if (!userSettings.getOutputDirectory().isDirectory())
            if (!userSettings.getOutputDirectory().mkdir()) ErrorUtilities.SWW();
        File f = new File(userSettings.getOutputDirectory().getAbsolutePath() + "\\" + fileName + ".jpg");

        try {
            ImageIO.write(finalImage, "JPG", f);
        } catch (IOException imageWriteException) {
            ErrorUtilities.writeError();
        }
        return f;
    }

    public static void writeFreezedFrames(int numOfFrames, AWTSequenceEncoder encoder, TiledImage image, UserSettings userSettings, double increment, ProgressBar progressBar){
        for (int i = 0; i < numOfFrames; i++) {
            writeFrame(encoder, image, userSettings, increment, progressBar);
        }
    }

    public static void writeFreezedFrames(int numOfFrames, AWTSequenceEncoder encoder, TiledImage image, UserSettings userSettings){
        for (int i = 0; i < numOfFrames; i++) {
            writeFrame(encoder, image, userSettings);
        }
    }

    public static void writeFrame(AWTSequenceEncoder encoder, TiledImage image, UserSettings userSettings) {
        int width = (int) ( (image.getArray()[0].getWidth() * userSettings.getColsNumber()) % 2 == 0 ? image.getArray()[0].getWidth() * userSettings.getColsNumber() : image.getArray()[0].getWidth() * userSettings.getColsNumber() + 1);
        int heigth = (int) ( (image.getArray()[0].getHeight() * userSettings.getRowsNumber()) % 2 == 0 ? image.getArray()[0].getHeight() * userSettings.getRowsNumber() : image.getArray()[0].getHeight() * userSettings.getRowsNumber() + 1);
        BufferedImage finalImage = new BufferedImage(width, heigth, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = finalImage.createGraphics();
        for (Tile t : image.getArray()) {
            BufferedImage tile = SwingFXUtils.fromFXImage(t.getTile(), null);
            graphics2D.drawImage(tile, (int) (t.getX() * image.getArray()[0].getWidth()), (int) (t.getY() * image.getArray()[0].getHeight()), null);
        }
        try {
            encoder.encodeImage(finalImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    public static void writeFrame(AWTSequenceEncoder encoder, TiledImage image, UserSettings userSettings, double increment, ProgressBar progressBar){
        int width = (int) ( (image.getArray()[0].getWidth() * userSettings.getColsNumber()) % 2 == 0 ? image.getArray()[0].getWidth() * userSettings.getColsNumber() : image.getArray()[0].getWidth() * userSettings.getColsNumber() + 1);
        int heigth = (int) ( (image.getArray()[0].getHeight() * userSettings.getRowsNumber()) % 2 == 0 ? image.getArray()[0].getHeight() * userSettings.getRowsNumber() : image.getArray()[0].getHeight() * userSettings.getRowsNumber() + 1);
        BufferedImage finalImage = new BufferedImage(width, heigth, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = finalImage.createGraphics();
        for (Tile t : image.getArray()) {
            BufferedImage tile = SwingFXUtils.fromFXImage(t.getTile(), null);
            graphics2D.drawImage(tile, (int) (t.getX() * image.getArray()[0].getWidth()), (int) (t.getY() * image.getArray()[0].getHeight()), null);
        }
        try {
            encoder.encodeImage(finalImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        progressBar.setProgress(progressBar.getProgress() + increment);
        graphics2D.dispose();
    }
}
