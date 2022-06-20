package JavaFXVersion;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

//./ffmpeg -framerate 1 -i final%d.png -vf -vcodec libx264 -acodec aac -strict -2 -preset slow -pix_fmt yuv420p -r 30
// prova1.mp4
//-vf -vf "drawtext=fontfile=/path/to/font.ttf:text='Stack Overflow':fontcolor=white:fontsize=24:box=1:boxcolor=black@0.5:boxborderw=5:x=(w-text_w)/2:y=(h-text_h)/2"


//? MAYBE THIS CLASS CAN EVEN EXTEND PROCESS TODO
public class FFMPEG {

    private Process process = null;

    public FFMPEG(File pathToFFMPEG, String outName, File outputDirectory, int framerate) {
        File pathToExecutable = pathToFFMPEG;
        ProcessBuilder builder = new ProcessBuilder( pathToExecutable.getAbsolutePath(), "-framerate",
                String.valueOf(framerate),
                "-y", "-i",
                "final%d.png","-vcodec","libx264","-acodec", "aac", "-strict", "2", "-preset", "slow",
                "-vf","\"drawtext=fontfile=C:/Users/mmatt/Desktop/Roboto/Roboto-LightItalic" +
                ".ttf:text=tmp.txt:fontcolor=white:fontsize=24:box=1:boxcolor=black@0.5:boxborderw=5:x=w-tw:y=0\"",
                "-pix_fmt", "yuv420p", outName);
        builder.directory(outputDirectory);
        builder.redirectErrorStream(true);
        try {
            process = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner s = new Scanner(process.getInputStream());
        StringBuilder text = new StringBuilder();
        while (s.hasNextLine()) {
            text.append(s.nextLine());
            text.append("\n");
        }

        s.close();
        int result = 0;
        try {
            result = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(text);
        System.out.printf( "Process exited with result %d", result );
    }
}
