package me.paypur.tconjei;

import net.minecraftforge.fml.common.Mod;

import java.awt.*;

import static me.paypur.tconjei.TConJEI.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class TConJEI {
    public static final String MOD_ID = "tconjei";

    public static boolean inBox(double mX, double mY, float x, float y, float w, float h) {
        return (x <= mX && mX <= x + w && y <= mY && mY <= y + h);
    }


    public float luminance(int color) {
        // https://www.w3.org/TR/WCAG20-TECHS/G17.html#G17-procedure
        float r = (color >> 16 & 0xff) / 255f;
        float g = (color >> 8 & 0xff) / 255f;
        float b = (color & 0xff) / 255f;

        float R = r <= 0.03928f ? r / 12.92f : (float) Math.pow((r + 0.055f) / 1.055, 2.4);
        float G = g <= 0.03928f ? g / 12.92f : (float) Math.pow((g + 0.055f) / 1.055, 2.4);
        float B = b <= 0.03928f ? b / 12.92f : (float) Math.pow((b + 0.055f) / 1.055, 2.4);

        return 0.2126f * R + 0.7152f * G + 0.0722f * B;
    }

    public float contrast(int color1, int color2) {
        return (Math.max(color1, color2) + 0.05f) / (Math.min(color1, color2) + 0.05f);
    }

    public int getTint(int color, float contrast) {
        // TODO: how
        return 0;
    }



}
