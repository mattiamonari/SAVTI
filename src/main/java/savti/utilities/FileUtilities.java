package savti.utilities;

import javafx.embed.swing.SwingFXUtils;
import savti.OutputHandler;
import savti.Tile;
import savti.TiledImage;
import savti.UserSettings;

import java.awt.*;
import java.awt.image.BufferedImage;


public class FileUtilities {

    private FileUtilities() {

    }


    /**
     * Encode a specific number of frames of a TiledImage inside the given encoder, adding the number of swaps and comparison to it.
     *
     * @param numOfFrames      Number of frames that will be encoded
     * @param outputHandler    The handler of the frames written to file.
     * @param image            TiledImage to encode
     * @param userSettings     Used to get the frame rate
     * @param countSwaps       Number of swaps to be written in the top-right corner of the image
     * @param countComparisons Number of comparisons to be written in the top-right corner of the image
     * @param fontSize         Size of the font for the number of swaps and comparisons.
     */
    public static void writeFreezedFrames(int numOfFrames, OutputHandler outputHandler, TiledImage image, UserSettings userSettings, double countSwaps, double countComparisons, int fontSize) {
        for (int i = 0; i < numOfFrames; i++) {
            writeFrame(outputHandler, image, userSettings, countSwaps, countComparisons, fontSize);
        }
    }

    /**
     * Set the frame Width of a given image
     *
     * @param image TiledImage to encode
     * @return an int that is the frame width.
     */
    public static int imageToFrameWidth(TiledImage image) {
        if (image.getImage().getWidth() % 2 == 0) {
            return (int) image.getImage().getWidth();
        } else {
            return (int) image.getImage().getWidth() + 1;
        }
    }

    /**
     * Set the frame Height of a given image
     *
     * @param image TiledImage to encode
     * @return an int that is the frame height.
     */
    public static int imageToFrameHeight(TiledImage image) {
        if (image.getImage().getHeight() % 2 == 0) {
            return (int) image.getImage().getHeight();
        } else {
            return (int) image.getImage().getHeight() + 1;
        }
    }

    /**
     * Encode a specific number of frames of a TiledImage inside the given encoder, adding the number of swaps and comparison to it.
     *
     * @param numOfFrames   Number of frames that will be encoded
     * @param outputHandler The handler of the frames written to file.
     * @param image         TiledImage to encode
     */
    public static void writeFreezedFrames(int numOfFrames, OutputHandler outputHandler, TiledImage image) {
        for (int i = 0; i < numOfFrames; i++) {
            writeFrame(outputHandler, image);
        }
    }

    /**
     * Method that given a TiledImage returned a Buffered image given a width and an height
     * @param image  TiledImage to encode
     * @return finalImage is the BufferedImage built from the TieldImage
     */
    public static BufferedImage buildFinalImage(TiledImage image) {
        int width = imageToFrameWidth(image);
        int height = imageToFrameHeight(image);
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        return finalImage;
    }
    /**
     * @param outputHandler The handler of the frames written to file.
     * @param image         TiledImage to encode
     */
    public static void writeFrame(OutputHandler outputHandler, TiledImage image) {
        BufferedImage finalImage = buildFinalImage(image);
        Graphics2D graphics2D = finalImage.createGraphics();
        for (Tile t : image.getArray()) {
            BufferedImage tile = SwingFXUtils.fromFXImage(t.getTile(), null);
            graphics2D.drawImage(tile, (int) (t.getX() * image.getArray()[0].getWidth()), (int) (t.getY() * image.getArray()[0].getHeight()), null);
        }
        outputHandler.encodeImage(finalImage);
        graphics2D.dispose();
    }

    /**
     * @param outputHandler    The handler of the frames written to file.
     * @param image
     * @param userSettings
     * @param countSwaps
     * @param countComparisons
     * @param fontSize
     */
    public static void writeFrame(OutputHandler outputHandler, TiledImage image, UserSettings userSettings, double countSwaps, double countComparisons, int fontSize) {
        BufferedImage finalImage = buildFinalImage(image);
        Graphics2D graphics2D = finalImage.createGraphics();
        for (Tile t : image.getArray()) {
            BufferedImage tile = SwingFXUtils.fromFXImage(t.getTile(), null);
            graphics2D.drawImage(tile, (int) (t.getX() * image.getArray()[0].getWidth()), (int) (t.getY() * image.getArray()[0].getHeight()), null);
        }
        graphics2D.drawString(String.valueOf(countSwaps), fontSize, fontSize);
        graphics2D.drawString(String.valueOf(countComparisons), fontSize, 2 * fontSize);
        graphics2D.drawString(userSettings.getChunkHeight() + "x" + userSettings.getChunkWidth(), fontSize, 3 * fontSize);
        graphics2D.drawString(String.valueOf(userSettings.getFrameRate()), fontSize, 4 * fontSize);
        outputHandler.encodeImage(finalImage);

        graphics2D.dispose();
    }
}
