package savti;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import savti.utilities.ErrorUtilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class OutputHandler {
    private SeekableByteChannel out;
    private AWTSequenceEncoder encoder;

    public OutputHandler() {
        encoder = null;
        out = null;
    }

    public void encodeImage(BufferedImage bufferedImage) {
        try {
            encoder.encodeImage(bufferedImage);
        } catch (IOException e) {
            ErrorUtilities.writeError();
        }
    }

    public void initializeHandler(String outPath, String outName, int framerate) {
        try {
            new File(outPath).createNewFile();
            out = NIOUtils.writableFileChannel(outPath + File.separator + outName);
            encoder = new AWTSequenceEncoder(out, Rational.R(framerate, 1));
        } catch (IOException ex) {
            //TODO
            ErrorUtilities.somethingWentWrong();
        }
    }

    public void closeOutputChannel() {
        try {
            encoder.finish();
        } catch (IOException e) {
            ErrorUtilities.finishEncodingError();
        }
        NIOUtils.closeQuietly(out);
    }


    public SeekableByteChannel getOut() {
        return out;
    }

    public AWTSequenceEncoder getEncoder() {
        return encoder;
    }
}