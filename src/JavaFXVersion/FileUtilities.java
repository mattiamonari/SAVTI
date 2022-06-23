package JavaFXVersion;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileUtilities {

    public static void deleteAllPreviousFiles(UserSettings userSettings)   {
        File directory = userSettings.getOutputDirectory();
        for (File f : directory.listFiles()) {
            if (f.getName().startsWith("final")) {
                f.delete();
            }
        }
    }

    public static void writeImage(UserSettings userSettings, Tail[] array , int chunkWidth , int chunkHeight , int index, int comparisons, int swaps) {
        int cols = userSettings.getPrecision();
        int rows = userSettings.getPrecision();
        BufferedImage finalImage = new BufferedImage(chunkWidth * cols , chunkHeight * rows , BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = finalImage.createGraphics();
        int i = 0;
        for (Tail t : array) {
            int row = i / rows;
            int col = i % rows;
            BufferedImage tail = SwingFXUtils.fromFXImage(t.getImage() , null);
            graphics2D.drawImage(tail , row * chunkWidth , col * chunkHeight , null);
            i++;
        }
        graphics2D.drawString ("Comparisons =" + comparisons, 20, 20 );
        graphics2D.drawString("Swaps =" + swaps, 20, 35);
        graphics2D.dispose();
        File f = new File(userSettings.getOutputDirectory().getAbsolutePath() + "\\final" + index / 5 + ".png");
        try {
            ImageIO.write(finalImage , "png" , f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
