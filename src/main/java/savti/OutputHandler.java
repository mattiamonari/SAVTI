package savti;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.SeekableByteChannel;
import savti.utilities.ErrorUtilities;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class OutputHandler {
    private SeekableByteChannel out = null;
    private AWTSequenceEncoder encoder = null;

    public OutputHandler(String outPath) {
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


    public SeekableByteChannel getOut() {
        return out;
    }

    public AWTSequenceEncoder getEncoder() {
        return encoder;
    }
}