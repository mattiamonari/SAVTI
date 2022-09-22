package JavaFXVersion.utilities;

import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.embed.swing.SwingFXUtils;

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
                    if(!f.delete())
                        ErrorUtilities.SWW();
                }
            }
        }
    }

    public static void writeImage(UserSettings userSettings , Tile[] array , int chunkWidth , int chunkHeight , long index , long comparisons , long swaps, double fontSize) {
        int cols = userSettings.getRowsNumber();
        int rows = userSettings.getColsNumber();
        BufferedImage finalImage = new BufferedImage(chunkWidth * cols , chunkHeight * rows , BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = finalImage.createGraphics();
        for (Tile t : array) {
            BufferedImage tile = SwingFXUtils.fromFXImage(t.getTile() , null);
            graphics2D.drawImage(tile , t.getX() * chunkWidth , t.getY() * chunkHeight , null);
        }
        graphics2D.setFont(new Font("Segoe UI Bold" , Font.BOLD , (int) fontSize));
        graphics2D.drawString("Comparisons = " + comparisons , 20 , (int) (fontSize + fontSize/5));
        graphics2D.drawString("Swaps = " + swaps , 20 , (int) (2*(fontSize +fontSize / 5)));
        graphics2D.dispose();
        if (!userSettings.getOutputDirectory().isDirectory())
            if(!userSettings.getOutputDirectory().mkdir())
                ErrorUtilities.SWW();
        File f = new File(userSettings.getOutputDirectory().getAbsolutePath() + "\\final" + index + ".jpg");

        try {
            ImageIO.write(finalImage, "JPG", f);
        } catch (IOException imageWriteException) {
            ErrorUtilities.writeError();
        }
    }

    public static File writeImage(UserSettings userSettings , Tile[] array , int chunkWidth , int chunkHeight , String fileName) {
        int cols = userSettings.getRowsNumber();
        int rows = userSettings.getColsNumber();
        BufferedImage finalImage = new BufferedImage(chunkWidth * cols , chunkHeight * rows , BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = finalImage.createGraphics();
        ImageIO.setUseCache(false);
        for (Tile t : array) {
            BufferedImage tile = SwingFXUtils.fromFXImage(t.getTile() , null);
            graphics2D.drawImage(tile , t.getX() * chunkWidth , t.getY() * chunkHeight , null);
        }
        if (!userSettings.getOutputDirectory().isDirectory())
            if(!userSettings.getOutputDirectory().mkdir())
                ErrorUtilities.SWW();
        File f = new File(userSettings.getOutputDirectory().getAbsolutePath() + "\\" + fileName + ".jpg");
        try {
            ImageIO.write(finalImage, "JPG", f);
        } catch (IOException imageWriteException) {
            ErrorUtilities.writeError();
        }
        return f;
    }
}
