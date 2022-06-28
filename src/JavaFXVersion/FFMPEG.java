package JavaFXVersion;

import javafx.scene.control.ProgressBar;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class FFMPEG {

    private Process process = null;

    FFmpeg ffmpeg;
    FFprobe ffprobe;
    FFmpegBuilder builder;
    FFmpegJob job;
    double percentage;

    public FFMPEG(UserSettings userSettings, ProgressBar progressBar) {
        File directory = userSettings.getOutputDirectory();
        if (directory.listFiles() != null) {
            for (File f : directory.listFiles()) {
                if (f.getName().endsWith(".mp4")) {
                    f.delete();
                }
            }
        }
        try {
            ffprobe = new FFprobe(userSettings.getFfprobePath().getAbsolutePath());
            FFmpegProbeResult in = ffprobe.probe(userSettings.getOutputDirectory().getAbsolutePath() + "\\final%d.png");
            ffmpeg = new FFmpeg(userSettings.getFfmpegPath().getAbsolutePath());
            builder = new FFmpegBuilder()
                    .addExtraArgs("-framerate", String.valueOf(userSettings.getFrameRate()))
                    .setInput(in)
                    .overrideOutputFiles(true)
                    .addOutput(userSettings.getOutputDirectory().getAbsolutePath() + "\\" + userSettings.getOutName())
                    .setVideoFrameRate(userSettings.getFrameRate())
                    .setAudioCodec("aac")
                    .setVideoCodec("libx264")
                    .setPreset("slow")
                    .setVideoPixelFormat("yuv420p")
                    .done();

            if (userSettings.getMusic() != null) {
                builder = builder.addInput(userSettings.getMusic().getAbsolutePath()).addExtraArgs("-shortest");

            }

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            job = executor.createJob(builder, new ProgressListener() {

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
            e.printStackTrace();
        }
    }

}
