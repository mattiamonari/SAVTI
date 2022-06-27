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
//./ffmpeg -framerate 1 -i final%d.png -vf -vcodec libx264 -acodec aac -strict -2 -preset slow -pix_fmt yuv420p -r 30
// prova1.mp4
//-vf -vf "drawtext=fontfile=/path/to/font.ttf:text='Stack Overflow':fontcolor=white:fontsize=24:box=1:boxcolor=black@0.5:boxborderw=5:x=(w-text_w)/2:y=(h-text_h)/2"


public class FFMPEG {

    private Process process = null;

    FFmpeg ffmpeg;
    FFprobe ffprobe;
    FFmpegBuilder builder;
    FFmpegJob job;
    double percentage;
/*
 "-i", music.getAbsolutePath(), "-c", "copy", "-map", "0:v:0", "-map",
                    "1:a:0","-shortest"

 */
    public FFMPEG(UserSettings userSettings, ProgressBar progressBar) {
        try {
            ffprobe = new FFprobe(userSettings.getFfprobePath().getAbsolutePath());
            FFmpegProbeResult in = ffprobe.probe(userSettings.getOutputDirectory().getAbsolutePath() + "\\final%d.png");
            ffmpeg = new FFmpeg(userSettings.getFfmpegPath().getAbsolutePath());
            builder = new FFmpegBuilder()
                    .setInput(in)
                    .overrideOutputFiles(true)
                    .addOutput(userSettings.getOutputDirectory().getAbsolutePath() + "\\" + userSettings.getOutName())
                    .addExtraArgs("-shortest")
                    .setAudioCodec("aac")
                    .setVideoCodec("libx264")
                    .setVideoFrameRate(userSettings.getFrameRate())
                    .setPreset("slow")
                    .setVideoPixelFormat("yuv420p")
                    .done();

            if(userSettings.getMusic() != null)
            {
                builder = builder.addInput(userSettings.getMusic().getAbsolutePath());

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
