package me.paypur.tconjei.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.tools.stats.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static me.paypur.tconjei.TConJEI.MOD_ID;
import static slimeknights.tconstruct.tables.TinkerTables.*;

@SuppressWarnings("unused")
@JeiPlugin
public class TConJEIPlugin implements IModPlugin {

    ResourceLocation UID = new ResourceLocation(MOD_ID, "jei_plugin");
    private static final RecipeType<ToolStatsWrapper> HARVEST_STATS = RecipeType.create(MOD_ID, "harvest_stats", ToolStatsWrapper.class);
    private static final RecipeType<ToolStatsWrapper> RANGED_STATS = RecipeType.create(MOD_ID, "ranged_stats", ToolStatsWrapper.class);
    private static final RecipeType<ToolStatsWrapper> ARMOR_STATS = RecipeType.create(MOD_ID, "armor_stats", ToolStatsWrapper.class);
    private static final RecipeType<ToolPartsWrapper> TOOL_PARTS = RecipeType.create(MOD_ID, "tool_parts", ToolPartsWrapper.class);

    @NotNull
    @Override
    public  ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<ToolStatsWrapper> statsWrappers = materials();
        registration.addRecipes(HARVEST_STATS, statsWrappers.stream()
                .filter(w -> w.hasStats(List.of(HeadMaterialStats.ID, StatlessMaterialStats.BINDING.getIdentifier(), HandleMaterialStats.ID)))
                .toList());
        registration.addRecipes(RANGED_STATS, statsWrappers.stream()
                .filter(w -> w.hasStats(List.of(LimbMaterialStats.ID, GripMaterialStats.ID, StatlessMaterialStats.BOWSTRING.getIdentifier())))
                .toList());
//        registration.addRecipes(ARMOR_STATS, materials());
        registration.addRecipes(TOOL_PARTS, toolDefinitions());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new HarvestStatsCategory(guiHelper));
        registration.addRecipeCategories(new RangedStatsCategory(guiHelper));
//        registration.addRecipeCategories(new ArmorStatsCategory(guiHelper));
        registration.addRecipeCategories(new ToolPartsCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(tinkerStation.asItem()), HARVEST_STATS, RANGED_STATS, TOOL_PARTS, ARMOR_STATS);
        registration.addRecipeCatalyst(new ItemStack(tinkersAnvil.asItem()), HARVEST_STATS, RANGED_STATS, TOOL_PARTS, ARMOR_STATS);
        registration.addRecipeCatalyst(new ItemStack(scorchedAnvil.asItem()), HARVEST_STATS, RANGED_STATS, TOOL_PARTS, ARMOR_STATS);
    }

    private List<ToolStatsWrapper> materials() {
        return MaterialRegistry.getInstance().getVisibleMaterials()
                .stream()
                .map(ToolStatsWrapper::new)
                .collect(Collectors.toList());
    }

    private List<ToolPartsWrapper> toolDefinitions() {
        return ToolDefinitionLoader.getInstance().getRegisteredToolDefinitions()
                .stream()
                .filter(definition -> definition.hasMaterials() && !definition.getId().equals(new ResourceLocation("tconstruct", "slime_helmet")))
                .sorted(Comparator.comparingInt(a -> StationSlotLayoutLoader.getInstance().get(a.getId()).getSortIndex()))
                .map(ToolPartsWrapper::new)
                .toList();
    }


}
