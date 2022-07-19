package JavaFXVersion;

import java.io.File;

public class UserSettings {
    public boolean saveImage = false;
    private int precision;
    private File outputDirectory;
    private File ffmpegPath;
    private File ffprobePath;
    private String outName;
    private int frameRate;
    private File music;
    private boolean openFile;
    private int videoDuration;
    private int chunkWidth;
    private int chunkHeight;

    //By default the program will produce output in the subdirectory of the current directory 'out' (creating it if
    // not existing)
    //?Maybe add a time of the video feature?
    public UserSettings() {
        precision = 8;
        outputDirectory = null;
        ffmpegPath = null;
        ffprobePath = null;
        outName = "sorted.mp4";
        frameRate = 3;
        music = null;
        openFile = false;
        videoDuration = 15;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        if (precision == 0)
            this.precision = 1;
        else
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

    public File getMusic() {
        return music;
    }

    public void setMusic(File music) {
        this.music = music;
    }

    public boolean isOpenFile() {
        return openFile;
    }

    public void setOpenFile(boolean openFile) {
        this.openFile = openFile;
    }

    public void setSaveImage(boolean saveImage) {
        this.saveImage = saveImage;
    }

    public File getFfprobePath() {
        return ffprobePath;
    }

    public void setFfprobePath(File ffprobePath) {
        this.ffprobePath = ffprobePath;
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

    public void setChunkHeight(int chunkHeight) {
        this.chunkHeight = chunkHeight;
    }

    public void setChunkWidth(int chunkWidth) {
        this.chunkWidth = chunkWidth;
    }

    public int getChunkWidth() {
        return chunkWidth;
    }

    public int getChunkHeight() {
        return chunkHeight;
    }
}
