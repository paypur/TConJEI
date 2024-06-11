package me.paypur.tconjei;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static me.paypur.tconjei.TConJEI.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class TConJEI {
    public static final String MOD_ID = "tconjei";

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public final class ClientForgeHandler {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            File folder = new File("resourcepacks");
            File copy = new File(folder, "tconjeidark.zip");

            folder.mkdirs();

            if (!copy.exists()) {
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
        }
    }

    public static boolean inBox(double mX, double mY, float x, float y, float w, float h) {
        return (x <= mX && mX <= x + w && y <= mY && mY <= y + h);
    }

}
