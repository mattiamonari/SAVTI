package savti.utilities;

import java.awt.*;

public class ColorUtilities {

    private ColorUtilities(){

    }

    public static String getHexFromValue(float value) {
        double red, green;
        if (value > 0.5f) {
            green = (1f - value) / 0.5f;
            red = 1f;
        } else {
            green = 1f;
            red = (value * 2);
        }
        Color c = new Color((float) red, (float) green, 0.15f);
        return Integer.toHexString(c.getRGB()).substring(2);
    }
}
