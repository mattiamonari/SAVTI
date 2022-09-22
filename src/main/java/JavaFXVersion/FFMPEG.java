package JavaFXVersion;

import JavaFXVersion.utilities.ErrorUtilities;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FFMPEG {
    FFmpeg ffmpeg;
    FFprobe ffprobe;
    FFmpegBuilder builder;
    FFmpegJob job;
    double percentage;

    public FFMPEG(UserSettings userSettings , ProgressBar progressBar) {
        try {
            ffprobe = new FFprobe(userSettings.getFfprobePath().getAbsolutePath());
            if (!ffprobe.isFFprobe() ) {
                Platform.exit();
            }
            ffmpeg = new FFmpeg(userSettings.getFfmpegPath().getAbsolutePath());
            if (!ffmpeg.isFFmpeg()) {
                Platform.exit();
            }
            FFmpegProbeResult in = ffprobe.probe(userSettings.getOutputDirectory().getAbsolutePath() + "\\final%d" + ".jpg");
            builder = new FFmpegBuilder().addExtraArgs("-framerate" , String.valueOf(userSettings.getFrameRate())).setInput(in).overrideOutputFiles(true).addOutput(userSettings.getOutputDirectory().getAbsolutePath() + "\\" + userSettings.getOutName()).setVideoFrameRate(userSettings.getFrameRate()).setAudioCodec("aac").setVideoCodec("libx264").setPreset("slow").setVideoPixelFormat("yuv420p").addExtraArgs("-shortest" , "-crf" , "18",  "-vf", "\"crop=trunc(iw/2)*2:trunc(ih/2)*2,loop=" + userSettings.getFrameRate() * 2 + ":1:" + userSettings.getStartingImageIndex() + ",setpts=N/FRAME_RATE/TB\"").done();
            if (userSettings.getMusic() != null) {
                builder = builder.addInput(userSettings.getMusic().getAbsolutePath());
            }
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg , ffprobe);
            job = executor.createJob(builder , new ProgressListener() {
                // Using the FFmpegProbeResult determine the duration of the input
                final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

                @Override
                public void progress(Progress progress) {
                    percentage = progress.out_time_ns / duration_ns;
                    progressBar.setProgress(percentage);
                }
            });
            job.run();
        } catch (IOException e) {
            Platform.runLater(ErrorUtilities::FFError);
        }
    }
}
