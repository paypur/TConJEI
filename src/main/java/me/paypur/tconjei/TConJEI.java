package me.paypur.tconjei;

import com.mojang.logging.LogUtils;
import me.paypur.tconjei.jei.MaterialStatsWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

    public static final boolean ENCYCLOPEDIA_MODE = true;

    public static final List<MaterialStatsId> HARVEST_STAT_IDS = List.of(HeadMaterialStats.ID, StatlessMaterialStats.BINDING.getIdentifier(), HandleMaterialStats.ID);
    public static final List<MaterialStatsId> RANGED_STAT_IDS = List.of(LimbMaterialStats.ID, GripMaterialStats.ID, StatlessMaterialStats.BOWSTRING.getIdentifier());
    public static final List<MaterialStatsId> ARMOR_STAT_IDS = List.of(
            PlatingMaterialStats.HELMET.getId(),
            PlatingMaterialStats.CHESTPLATE.getId(),
            PlatingMaterialStats.LEGGINGS.getId(),
            PlatingMaterialStats.BOOTS.getId(),
            PlatingMaterialStats.SHIELD.getId(),
            StatlessMaterialStats.MAILLE.getIdentifier(),
            StatlessMaterialStats.SHIELD_CORE.getIdentifier()
    );

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ClientModHandler {
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
                InputStream in = Minecraft.getInstance().getResourceManager().getResource(texture).get().open();;
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
                InputStream stream = Minecraft.getInstance().getResourceManager().getResource(ColorManager.palette).get().open();
                BufferedImage image = ImageIO.read(stream);
                ColorManager.TEXT_COLOR = image.getRGB(0, 0);
                ColorManager.DURABILITY_COLOR = image.getRGB(1, 0);
                ColorManager.MINING_COLOR = image.getRGB(0, 1);
                ColorManager.ATTACK_COLOR = image.getRGB(1, 1);
                ColorManager.ARMOR_COLOR = image.getRGB(2, 1);
                stream.close();
            } catch (ArrayIndexOutOfBoundsException | IOException e) {
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
                    int a = wrapper.hasStats(ARMOR_STAT_IDS) ? 1 : 0;

                    int flag = h << 2 | r << 1 | a;

                    if (flag == 0) {
                        break;
                    }

                    int tier = wrapper.material().getTier();

                    MutableComponent component = Component.translatable("tconjei.tooltip.tier", tier)
                            .withStyle(style -> style.withColor(ColorManager.getTierColor(tier).orElse(0xAAAAAA)));

                    MutableComponent extra = switch (flag) {
                        case 0b001 -> Component.translatable("tconjei.tooltip.armor");
                        case 0b010 -> Component.translatable("tconjei.tooltip.ranged");
                        case 0b011 -> Component.translatable("tconjei.tooltip.ranged_armor");
                        case 0b100 -> Component.translatable("tconjei.tooltip.harvest");
                        case 0b101 -> Component.translatable("tconjei.tooltip.harvest_armor");
                        case 0b110 -> Component.translatable("tconjei.tooltip.harvest_ranged");
                        case 0b111 -> Component.translatable("tconjei.tooltip.harvest_ranged_armor");
                        default -> Component.empty();
                    };

                    Utils.allMaterialsTooltip.put(stack.getItem(), component.append(extra.withStyle(ChatFormatting.GRAY)));
                }
            }
        }
    }

}
