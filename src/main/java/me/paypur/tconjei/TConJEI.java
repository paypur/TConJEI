package me.paypur.tconjei;

import com.mojang.logging.LogUtils;
import me.paypur.tconjei.jei.MaterialStatsWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.tools.stats.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static me.paypur.tconjei.TConJEI.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class TConJEI {

    public static final String MOD_ID = "tconjei";
    public static final List<MaterialStatsId> HARVEST_STAT_IDS = List.of(HeadMaterialStats.ID, ExtraMaterialStats.ID, HandleMaterialStats.ID);
    public static final List<MaterialStatsId> RANGED_STAT_IDS = List.of(LimbMaterialStats.ID, GripMaterialStats.ID, BowstringMaterialStats.ID);

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ClientModHandler {
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

        @SubscribeEvent
        public static void onClientReload(TextureStitchEvent.Post event) {
            try {
                InputStream stream = Minecraft.getInstance().getResourceManager().getResource(ColorManager.palette).getInputStream();
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

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ClientForgeHandler {
        // TODO: might have problems if server changes and valid materials change too
        // runs on reload too
        @SubscribeEvent
        public static void onLogin(RecipesUpdatedEvent event) {
            if (!Utils.allMaterialsTooltip.isEmpty()) {
                return;
            }

            for (MaterialStatsWrapper wrapper : Utils.getMaterialWrappers()) {
                for (ItemStack stack : wrapper.getInputs()) {
                    int h = wrapper.hasStats(HARVEST_STAT_IDS) ? 1 : 0;
                    int r = wrapper.hasStats(RANGED_STAT_IDS) ? 1 : 0;

                    int flag = h << 1 | r;

                    if (flag == 0) {
                        break;
                    }

                    MutableComponent component = new TranslatableComponent("tconjei.tooltip.tier", wrapper.material().getTier()).withStyle(ChatFormatting.GRAY);

                    switch (flag) {
                        case 0b01 -> component.append(new TranslatableComponent("tconjei.tooltip.ranged"));
                        case 0b10 -> component.append(new TranslatableComponent("tconjei.tooltip.harvest"));
                        case 0b11 -> component.append(new TranslatableComponent("tconjei.tooltip.harvest_ranged"));
                    }

                    Utils.allMaterialsTooltip.put(stack.getItem().getDescriptionId(), component);
                }
            }
        }
    }

}
