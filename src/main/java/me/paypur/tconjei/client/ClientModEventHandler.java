package me.paypur.tconjei.client;

import com.mojang.logging.LogUtils;
import me.paypur.tconjei.ColorManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static me.paypur.tconjei.TConJEI.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventHandler {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        File folder = new File("resourcepacks");
        File copy = new File(folder, "tconjeidark.zip");

        folder.mkdirs();

        if (copy.exists()) {
            // delete potentially old versions
            copy.delete();
        }

        try {
            folder.mkdirs();

            ResourceLocation texture = new ResourceLocation(MOD_ID, "tconjeidark.zip");
            InputStream in = Minecraft.getInstance().getResourceManager().getResource(texture).getInputStream();
            FileOutputStream out = new FileOutputStream(copy);

            byte[] buffer = new byte[4096];
            int read;

            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
                out.flush();
            }

            in.close();
            out.close();
        } catch (IOException e) {
            LogUtils.getLogger().error("Failed to copy built-in resource pack", e);
        }
    }

    @SubscribeEvent
    public static void onClientReload(TextureStitchEvent.Post event) {
        try {
            InputStream stream = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(MOD_ID, "textures/gui/palette.png")).getInputStream();
            BufferedImage image = ImageIO.read(stream);
            ColorManager.TEXT_COLOR = image.getRGB(0, 0);
            stream.close();
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            LogUtils.getLogger().error("Error loading palette", e);
        }
    }

}
