package JavaFXVersion.utilities;

import java.awt.*;

public class ColorUtilities {
    public static String getHexFromValue(float value) {
        double R, G;
        if (value > 0.5f) {
            G = (1f - value) / 0.5f;
            R = 1f;
        } else {
            G = 1f;
            R = (value * 2);
        }
        Color c = new Color((float) R, (float) G, 0.15f);
        return Integer.toHexString(c.getRGB()).substring(2);
    }
}
