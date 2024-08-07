package me.paypur.tconjei;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static me.paypur.tconjei.TConJEI.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class TConJEI {
    public static final String MOD_ID = "tconjei";


    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public final class ClientModHandler {
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

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public final class ForgeHandler {
        @SubscribeEvent
        public static void onLogin(RecipesUpdatedEvent event) {
            if (Utils.AllInputs.isEmpty()) {
                Level world = Minecraft.getInstance().level;
                if (world == null) {
                    return;
                }
                List<Item> repairItems = RecipeHelper.getRecipes(world.getRecipeManager(), TinkerRecipeTypes.MATERIAL.get(), MaterialRecipe.class)
                        .stream()
                        .flatMap(recipe -> Arrays.stream(recipe.getIngredient().getItems()))
                        .map(ItemStack::getItem)
                        .toList();
                Utils.AllInputs = new HashSet<>(repairItems);
            }
        }
    }

}
