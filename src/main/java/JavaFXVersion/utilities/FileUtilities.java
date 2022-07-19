package JavaFXVersion.utilities;

import JavaFXVersion.Tile;
import JavaFXVersion.UserSettings;
import javafx.embed.swing.SwingFXUtils;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;

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
                    f.delete();
                }
            }
        }
    }

    public static void writeImage(UserSettings userSettings , Tile[] array , int chunkWidth , int chunkHeight , int index , int comparisons , int swaps) {
        int cols = userSettings.getPrecision();
        int rows = userSettings.getPrecision();
        BufferedImage finalImage = new BufferedImage(chunkWidth * cols , chunkHeight * rows , BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = finalImage.createGraphics();
        int i = 0;
        for (Tile t : array) {
            int row = i / rows;
            int col = i % rows;
            BufferedImage Tile = SwingFXUtils.fromFXImage(t.getImage() , null);
            graphics2D.drawImage(Tile , row * chunkWidth , col * chunkHeight , null);
            i++;
        }
        graphics2D.setFont(new Font("Segoe UI Bold" , Font.BOLD , 25));
        graphics2D.drawString("Comparisons =" + comparisons , 20 , 30);
        graphics2D.drawString("Swaps =" + swaps , 20 , 60);
        graphics2D.dispose();
        if (!userSettings.getOutputDirectory().isDirectory())
            userSettings.getOutputDirectory().mkdir();
        File f = new File(userSettings.getOutputDirectory().getAbsolutePath() + "\\final" + index + ".png");
        try {
            Imaging.writeImage(finalImage , f , ImageFormats.PNG);
        } catch (ImageWriteException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeImage(UserSettings userSettings , Tile[] array , int chunkWidth , int chunkHeight , int index) {
        int cols = userSettings.getPrecision();
        int rows = userSettings.getPrecision();
        BufferedImage finalImage = new BufferedImage(chunkWidth * cols , chunkHeight * rows , BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = finalImage.createGraphics();
        int i = 0;
        for (Tile t : array) {
            int row = i / rows;
            int col = i % rows;
            BufferedImage tile = SwingFXUtils.fromFXImage(t.getImage() , null);
            graphics2D.drawImage(tile , row * chunkWidth , col * chunkHeight , null);
            i++;
        }
        if (!userSettings.getOutputDirectory().isDirectory())
            userSettings.getOutputDirectory().mkdir();
        File f = new File(userSettings.getOutputDirectory().getAbsolutePath() + "\\final" + index + ".png");
        try {
            Imaging.writeImage(finalImage , f , ImageFormats.PNG);
        } catch (ImageWriteException | IOException e) {
            e.printStackTrace();
        }
    }
}
