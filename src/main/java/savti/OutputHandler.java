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
    private SeekableByteChannel out = null;
    private AWTSequenceEncoder encoder = null;

    public OutputHandler() {

    }

    public void encodeImage(BufferedImage bufferedImage){
        if(encoder != null && out.isOpen() && out != null) {
            try {
                encoder.encodeImage(bufferedImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
            ErrorUtilities.writeError();
    }

    public void initializeHandler(String outPath, String outName, int framerate)
    {
        try {
            new File(outPath).createNewFile();
            out = NIOUtils.writableFileChannel(outPath + "\\" + outName);
            encoder = new AWTSequenceEncoder(out, Rational.R(framerate, 1));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void closeOutputChannel(){
        try {
            encoder.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
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