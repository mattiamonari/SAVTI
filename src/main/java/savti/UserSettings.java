package savti;

import java.io.File;

public class UserSettings {

    private boolean saveImage = false;
    private int rowsNumber, colsNumber;
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

    private int startingImageIndex;

    //By default the program will produce output in the subdirectory of the current directory 'out' (creating it if
    // not existing)
    //?Maybe add a time of the video feature?
    public UserSettings() {
        colsNumber = rowsNumber = 1;
        outputDirectory = new File("D:\\IdeaProjects\\sortingVisualization\\ext");
        ffmpegPath = new File("D:\\IdeaProjects\\sortingVisualization\\ext\\ffmpeg.exe");
        ffprobePath = new File("D:\\IdeaProjects\\sortingVisualization\\ext\\ffprobe.exe");
        outName = "sorted.mp4";
        frameRate = 30;
        music = null;
        openFile = false;
        videoDuration = 15;
        startingImageIndex = 0;
    }
    public boolean isADirectory() {
        return getOutputDirectory() != null && getOutputDirectory().isDirectory();
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

    public boolean getSaveImage() {
        return saveImage;
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

    public boolean verifyFfmpegPath() {
        return ffmpegPath != null && ffmpegPath.toString().endsWith("ffmpeg.exe");
    }

    public boolean verifyFfprobePath() {
        return ffprobePath != null && ffprobePath.toString().endsWith("ffprobe.exe");
    }

    public boolean verifyOutputPath() {
        return getOutputDirectory() != null;
    }

    public int getStartingImageIndex() {
        return startingImageIndex;
    }

    public void setStartingImageIndex(int startingImageIndex) {
        this.startingImageIndex = startingImageIndex;
    }
}
