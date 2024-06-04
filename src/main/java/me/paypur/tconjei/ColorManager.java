package me.paypur.tconjei;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static me.paypur.tconjei.TConJEI.MOD_ID;

public class ColorManager {

    public static final int WHITE = 0xffffff;
    public static final int BLACK = 0x000000;
    public static int TEXT_COLOR = 0x3F3F3F;
    public static int DURABILITY_COLOR = 0x46ca46; //0x298E29
    public static int MINING_COLOR = 0x779ecb; //0x4A7EBA
    public static int ATTACK_COLOR = 0xd46363; //0xD05353
    static ResourceLocation palette = new ResourceLocation(MOD_ID, "textures/gui/palette.png");

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public final class ClientForgeHandler {
        @SubscribeEvent
        public static void onClientReload(TextureStitchEvent.Post event) {
            try {
                InputStream stream = Minecraft.getInstance().getResourceManager().getResource(palette).getInputStream();
                BufferedImage image = ImageIO.read(stream);
                ColorManager.TEXT_COLOR = image.getRGB(0, 0);
                ColorManager.DURABILITY_COLOR = image.getRGB(1, 0);
                ColorManager.MINING_COLOR = image.getRGB(0, 1);
                ColorManager.ATTACK_COLOR = image.getRGB(1, 1);
                stream.close();
            } catch (IOException e) {
                LogUtils.getLogger().error("Error loading palette", e);
            }
        }
    }

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

    public static int getMiningLevelColor(ResourceLocation miningLevel) {
        // TODO: found colors in assets/tconstruct/mantle/colors.json
        // for some reason the library specified colors are different
        // are also even harder to read
//        return ResourceColorManager.getColor(Util.makeTranslationKey("harvest_tier", miningLevel));
        return switch (miningLevel.getPath()) {
            case "wood" -> 0x8C651B;
            case "gold" -> 0xFCA800 ;
            case "stone" -> 0x979797;
            case "iron" -> 0xDFDFDF; // default color 0xC8C8C8 is not visible in light mode
            case "diamond" -> 0x54FCFC;
            case "netherite" -> 0x4C4143;
            default -> TEXT_COLOR;
        };
    }

    // @formatter:off
    public static int getMultiplierColor(Float f) {
        if (f < 0.55f) { return 0xbd0000; }
        if (f < 0.60f) { return 0xbd2600; }
        if (f < 0.65f) { return 0xbd4b00; }
        if (f < 0.70f) { return 0xbd7100; }
        if (f < 0.75f) { return 0xbd9700; }
        if (f < 0.80f) { return 0xbdbd00; }
        if (f < 0.85f) { return 0x97bd00; }
        if (f < 0.90f) { return 0x71bd00; }
        if (f < 0.95f) { return 0x4bbd00; }
        if (f < 1.00f) { return 0x26bd00; }
        if (f < 1.05f) { return 0x00bd00; }
        if (f < 1.10f) { return 0x00bd26; }
        if (f < 1.15f) { return 0x00bd4b; }
        if (f < 1.20f) { return 0x00bd71; }
        if (f < 1.25f) { return 0x00bd97; }
        if (f < 1.30f) { return 0x00bdbd; }
        if (f < 1.35f) { return 0x0097bd; }
        if (f < 1.4f) { return 0x0071bd; }
        return 0x004bbd;
    }
    // @formatter:on

    public static int getDifferenceColor(float f) {
        return getMultiplierColor(f + 1f);
    }

}
