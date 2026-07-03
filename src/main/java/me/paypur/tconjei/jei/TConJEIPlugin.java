package me.paypur.tconjei.jei;

import me.paypur.tconjei.TConJEI;
import me.paypur.tconjei.Utils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.*;

import java.util.List;

import static me.paypur.tconjei.TConJEI.MOD_ID;

@SuppressWarnings("unused")
@JeiPlugin
public class TConJEIPlugin implements IModPlugin {

    ResourceLocation UID = new ResourceLocation(MOD_ID, "jei_plugin");
    public static final RecipeType<MaterialStatsWrapper> HARVEST_STATS = RecipeType.create(MOD_ID, "harvest_stats", MaterialStatsWrapper.class);
    public static final RecipeType<MaterialStatsWrapper> RANGED_STATS = RecipeType.create(MOD_ID, "ranged_stats", MaterialStatsWrapper.class);
    public static final RecipeType<MaterialStatsWrapper> ARMOR_STATS = RecipeType.create(MOD_ID, "armor_stats", MaterialStatsWrapper.class);
    public static final RecipeType<MaterialStatsWrapper> AMMO_STATS = RecipeType.create(MOD_ID, "ammo_stats", MaterialStatsWrapper.class);
    public static final RecipeType<MaterialStatsWrapper> SKULL_STATS = RecipeType.create(MOD_ID, "skull_stats", MaterialStatsWrapper.class);

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<MaterialStatsWrapper> statsWrappers = Utils.getMaterialWrappers();
        registration.addRecipes(HARVEST_STATS, statsWrappers.stream()
                .filter(w -> w.hasStats(TConJEI.HARVEST_STAT_IDS))
                .toList());
        registration.addRecipes(RANGED_STATS, statsWrappers.stream()
                .filter(w -> w.hasStats(TConJEI.RANGED_STAT_IDS))
                .toList());
        registration.addRecipes(ARMOR_STATS, statsWrappers.stream()
                .filter(w -> w.hasStats(TConJEI.ARMOR_STAT_IDS))
                .toList());
        registration.addRecipes(SKULL_STATS, statsWrappers.stream()
                .filter(w -> w.hasStats(List.of(SkullStats.ID)))
                .toList());
        registration.addRecipes(AMMO_STATS, statsWrappers.stream()
                .filter(w -> w.hasStats(TConJEI.AMMO_STAT_IDS))
                .toList());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new MaterialStatsCategory(
            guiHelper,
            TConJEIPlugin.HARVEST_STATS,
            TConJEI.HARVEST_STAT_IDS,
            Component.translatable("tconjei.tool_stats.harvest"),
            guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 0, 0, 16, 16),
            TinkerTags.Items.HARVEST
        ));
        registration.addRecipeCategories(new MaterialStatsCategory(
            guiHelper,
            TConJEIPlugin.RANGED_STATS,
            TConJEI.RANGED_STAT_IDS,
            Component.translatable("tconjei.tool_stats.ranged"),
            guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 16, 0, 16, 16),
            TinkerTags.Items.RANGED
        ));
        registration.addRecipeCategories(new ArmorStatsCategory(
            guiHelper,
            TConJEIPlugin.ARMOR_STATS,
            TConJEI.ARMOR_STAT_IDS,
            Component.translatable("tconjei.tool_stats.armor"),
            guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 32, 0, 16, 16),
            TinkerTags.Items.ARMOR
        ));
        registration.addRecipeCategories(new MaterialStatsCategory(
            guiHelper,
            TConJEIPlugin.AMMO_STATS,
            TConJEI.AMMO_STAT_IDS,
            Component.translatable("tconjei.tool_stats.ammo"),
            guiHelper.createDrawableItemStack(TinkerTools.arrow.get().getRenderTool()),
            TinkerTags.Items.AMMO
        ));
        registration.addRecipeCategories(new SlimeskullStatsCategory(
            guiHelper,
            TConJEIPlugin.SKULL_STATS,
            List.of(SkullStats.ID),
            Component.translatable("tconjei.tool_stats.skull"),
            guiHelper.createDrawableItemLike(Items.SKELETON_SKULL)
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TinkerTables.tinkerStation.asItem()), HARVEST_STATS, RANGED_STATS, ARMOR_STATS);
        registration.addRecipeCatalyst(new ItemStack(TinkerTables.tinkersAnvil.asItem()), HARVEST_STATS, RANGED_STATS, ARMOR_STATS);
        registration.addRecipeCatalyst(new ItemStack(TinkerTables.scorchedAnvil.asItem()), HARVEST_STATS, RANGED_STATS, ARMOR_STATS);
        registration.addRecipeCatalyst(new ItemStack(TinkerSmeltery.searedBasin.asItem()), SKULL_STATS);
        registration.addRecipeCatalyst(new ItemStack(TinkerSmeltery.scorchedBasin.asItem()), SKULL_STATS);
    }

}
