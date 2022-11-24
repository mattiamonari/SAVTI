package savti;

import savti.utilities.ErrorUtilities;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UserSettings {
    int colsNumber;
    private int rowsNumber;
    private File outputDirectory;
    private String outName;
    private int frameRate;
    private boolean openFile;
    private int videoDuration;
    private int chunkWidth;
    private int chunkHeight;

    private File music;

    //By default the program will produce output in the subdirectory of the current directory 'out' (creating it if
    // not existing)
    public UserSettings() {
        colsNumber = rowsNumber = 1;
        outName = "sorted.mp4";
        frameRate = 30;
        openFile = false;
        videoDuration = 15;
        outputDirectory = new File(Path.of("").toAbsolutePath() + "\\out\\");

        if (isOutputDirectory()) {
            try {
                Files.createDirectories(outputDirectory.toPath());
            } catch (IOException e) {
                ErrorUtilities.outputPath();
            }
        }

    }

    public boolean isOutputDirectory() {
        return getOutputDirectory() != null && getOutputDirectory().isDirectory();
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getOutName() {
        return outName;
    }

    public void setOutName(String outName) {
        this.outName = outName;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        if (frameRate == 0)
            this.frameRate = 1;
        else
            this.frameRate = frameRate;
    }

    public boolean isOpenFile() {
        return openFile;
    }

    public void setOpenFile(boolean openFile) {
        this.openFile = openFile;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        if (videoDuration == 0)
            this.videoDuration = 1;
        else
            this.videoDuration = videoDuration;
    }

    public int getChunkWidth() {
        return chunkWidth;
    }

    public void setChunkWidth(int chunkWidth) {
        this.chunkWidth = chunkWidth;
    }

    public int getChunkHeight() {
        return chunkHeight;
    }

    public void setChunkHeight(int chunkHeight) {
        this.chunkHeight = chunkHeight;
    }

    public int getRowsNumber() {
        return rowsNumber;
    }

    public void setRowsNumber(int rowsNumber) {
        if (rowsNumber == 0)
            this.rowsNumber = 1;
        else
            this.rowsNumber = rowsNumber;
    }

    public int getColsNumber() {
        return colsNumber;
    }

    public void setColsNumber(int colsNumber) {
        if (colsNumber == 0)
            this.colsNumber = 1;
        else
            this.colsNumber = colsNumber;
    }

    public boolean verifyOutputPath() {
        return getOutputDirectory() != null;
    }

    public void openOutputDirectory() {
        if (isOutputDirectory()) {
            try {
                Desktop.getDesktop().open(getOutputDirectory());
            } catch (IOException e) {
                ErrorUtilities.somethingWentWrong();
            }
        }
    }

    public File getMusic() {
        return music;
    }

    public void setMusic(File music) {
        this.music = music;
    }


}
