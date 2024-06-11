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
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static me.paypur.tconjei.TConJEI.MOD_ID;
import static slimeknights.tconstruct.tables.TinkerTables.*;

@SuppressWarnings("unused")
@JeiPlugin
public class TConJEIPlugin implements IModPlugin {

    ResourceLocation UID = new ResourceLocation(MOD_ID, "jei_plugin");
    private static final RecipeType<MaterialStatsWrapper> HARVEST_STATS = RecipeType.create(MOD_ID, "material_stats", MaterialStatsWrapper.class);
    private static final RecipeType<MaterialStatsWrapper> RANGED_STATS = RecipeType.create(MOD_ID, "material_stats", MaterialStatsWrapper.class);
    private static final RecipeType<ToolPartsWrapper> TOOL_PARTS = RecipeType.create(MOD_ID, "tool_parts", ToolPartsWrapper.class);

    @NotNull
    @Override
    public  ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(HARVEST_STATS, materials());
        registration.addRecipes(RANGED_STATS, materials());
        registration.addRecipes(TOOL_PARTS, toolDefinitions());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new HarvestStatsCategory(guiHelper));
        registration.addRecipeCategories(new RangedStatsCategory(guiHelper));
        registration.addRecipeCategories(new ToolPartsCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(tinkerStation.asItem()), HARVEST_STATS);
        registration.addRecipeCatalyst(new ItemStack(tinkersAnvil.asItem()), HARVEST_STATS);
        registration.addRecipeCatalyst(new ItemStack(scorchedAnvil.asItem()), HARVEST_STATS);
        registration.addRecipeCatalyst(new ItemStack(tinkerStation.asItem()), RANGED_STATS);
        registration.addRecipeCatalyst(new ItemStack(tinkersAnvil.asItem()), RANGED_STATS);
        registration.addRecipeCatalyst(new ItemStack(scorchedAnvil.asItem()), RANGED_STATS);
        registration.addRecipeCatalyst(new ItemStack(tinkerStation.asItem()), TOOL_PARTS);
        registration.addRecipeCatalyst(new ItemStack(tinkersAnvil.asItem()), TOOL_PARTS);
        registration.addRecipeCatalyst(new ItemStack(scorchedAnvil.asItem()), TOOL_PARTS);
    }

    private List<MaterialStatsWrapper> materials() {
        return MaterialRegistry.getMaterials()
                .stream()
                .filter(iMaterial -> !iMaterial.isHidden())
                .map(stats -> new MaterialStatsWrapper((Material) stats))
                .filter(MaterialStatsWrapper::hasTraits)
                .collect(Collectors.toList());
    }

    private List<ToolPartsWrapper> toolDefinitions() {
        return ToolDefinitionLoader.getInstance().getRegisteredToolDefinitions()
                .stream()
                .filter(definition -> definition.isMultipart() &&
                        !definition.getId().equals(new ResourceLocation("tconstruct", "slime_helmet")))
                .sorted(Comparator.comparingInt(a -> StationSlotLayoutLoader.getInstance().get(a.getId()).getSortIndex()))
                .map(ToolPartsWrapper::new)
                .toList();
    }

}
