package me.paypur.tconjei;

import java.awt.*;

public class ColorManager {

    // https://www.w3.org/TR/WCAG20-TECHS/G17.html#G17-procedure
    public static float luminance(int color) {
        float r = (color >> 16 & 0xff) / 255f;
        float g = (color >> 8 & 0xff) / 255f;
        float b = (color & 0xff) / 255f;

        // use approximation instead
        return (float) (0.2126f * Math.pow(r, 2.2) + 0.7152f * Math.pow(g, 2.2) + 0.0722f * Math.pow(b, 2.2));
    }

    public static float contrast(float luminance1, float luminance2) {
        return (Math.max(luminance1, luminance2) + 0.05f) / (Math.min(luminance1, luminance2) + 0.05f);
    }

    //https://gamedev.stackexchange.com/questions/38536/given-a-rgb-color-x-how-to-find-the-most-contrasting-color-y
    public static int getShade(int color, float contrastRatio) {
        float colorLuminance = luminance(color);
        float factor = 0;

        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;
        float[] hsb = new float[3];
        Color.RGBtoHSB(r,g,b,hsb);

        // use shade when it is possible to meet the minimum contrast ratio
        if (contrast(colorLuminance, 0) > contrastRatio) {
            factor = (float) Math.pow(
                    (colorLuminance + 0.05f - contrastRatio * 0.05f) / (contrastRatio * colorLuminance),
                    1/2.2f
            );
        }

//        else if (contrast(colorLuminance, Color.HSBtoRGB(hsb[0], hsb[1],1)) > contrastRatio) {
//            factor = (float) Math.pow(
//                    (contrastRatio * (colorLuminance + 0.05f) - 0.05f) / colorLuminance,
//                    1/2.2f
//            );
//        }

        // relationship between rgb is constant while changing brightness
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * factor);
    }

}