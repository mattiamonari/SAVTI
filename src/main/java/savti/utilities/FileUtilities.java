package savti.utilities;

import savti.Tile;
import savti.TiledImage;
import savti.UserSettings;
import javafx.embed.swing.SwingFXUtils;
import org.jcodec.api.awt.AWTSequenceEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileUtilities {


    /**
     * Encode a specific number of frames of a TiledImage inside the given encoder, adding the number of swaps and comparison to it.
     * @param numOfFrames Number of frames that will be encoded
     * @param encoder The encoder which holds the frames
     * @param image TiledImage to encode
     * @param userSettings Used to get the framerate
     * @param countSwaps Number of swaps to be written on the top-right corner of the image
     * @param countComparisons Number of comparisons to be written on the top-right corner of the image
     * @param fontSize Size of the font for the number of swaps and comparisons.
     */
    public static void writeFreezedFrames(int numOfFrames, AWTSequenceEncoder encoder, TiledImage image, UserSettings userSettings, double countSwaps, double countComparisons, int fontSize) {
        for (int i = 0; i < numOfFrames; i++) {
            writeFrame(encoder, image, userSettings, countSwaps, countComparisons, fontSize);
        }
    }

    /**
     * Encode a specific number of frames of a TiledImage inside the given encoder, adding the number of swaps and comparison to it.
     * @param numOfFrames Number of frames that will be encoded
     * @param encoder The encoder which holds the frames
     * @param image TiledImage to encode
     */
    public static void writeFreezedFrames(int numOfFrames, AWTSequenceEncoder encoder, TiledImage image) {
        for (int i = 0; i < numOfFrames; i++) {
            writeFrame(encoder, image);
        }
    }

    /**
     *
     * @param encoder
     * @param image
     */
    public static void writeFrame(AWTSequenceEncoder encoder, TiledImage image) {
        int width = (int)(image.getImage().getWidth() % 2 == 0 ? image.getImage().getWidth() : image.getImage().getWidth()+1);
        int height = (int)(image.getImage().getHeight() % 2 == 0 ? image.getImage().getHeight() : image.getImage().getHeight()+1);
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
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


    /**
     * @param encoder
     * @param image
     * @param userSettings
     * @param countSwaps
     * @param countComparisons
     * @param fontSize
     */
    public static void writeFrame(AWTSequenceEncoder encoder, TiledImage image, UserSettings userSettings, double countSwaps, double countComparisons, int fontSize) {
        int width = (int)(image.getImage().getWidth() % 2 == 0 ? image.getImage().getWidth() : image.getImage().getWidth()+1);
        int height = (int)(image.getImage().getHeight() % 2 == 0 ? image.getImage().getHeight() : image.getImage().getHeight()+1);
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = finalImage.createGraphics();
        for (Tile t : image.getArray()) {
            BufferedImage tile = SwingFXUtils.fromFXImage(t.getTile(), null);
            graphics2D.drawImage(tile, (int) (t.getX() * image.getArray()[0].getWidth()), (int) (t.getY() * image.getArray()[0].getHeight()), null);
        }
        graphics2D.drawString(String.valueOf(countSwaps), fontSize, fontSize);
        graphics2D.drawString(String.valueOf(countComparisons), fontSize, 2 * fontSize);
        graphics2D.drawString(userSettings.getChunkHeight() + "x" + userSettings.getChunkWidth(), fontSize, 3 * fontSize);
        graphics2D.drawString(String.valueOf(userSettings.getFrameRate()), fontSize, 4 * fontSize);
        try {
            encoder.encodeImage(finalImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }
}
