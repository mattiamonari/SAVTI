package JavaFXVersion;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.nio.file.Paths;

public class UserSettings {

    private int precision;
    private File outputDirectory;
    private File ffmpegPath;
    private MediaView m;
    private String outName;
    private static String currDirectory;

    //By default the program will produce output in the subdirectory of the current directory 'out' (creating it if
    // not existing)
    public UserSettings() {
        precision = 8;
        currDirectory = Paths.get("").toAbsolutePath().toString();
        System.out.println();
        outputDirectory = new File( currDirectory + "\\out\\");
        ffmpegPath = new File(currDirectory + "\\ext\\ffmpeg.exe");
        outName = "sorted.mp4";
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public File getFfmpegPath() {
        return ffmpegPath;
    }

    public void setFfmpegPath(File ffmpegDirectory) {
        this.ffmpegPath = ffmpegDirectory;
    }

    public String getOutName() {
        return outName;
    }
}
