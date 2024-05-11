package me.paypur.tconjei.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.paypur.tconjei.TConJEI.MOD_ID;

@SuppressWarnings("unused")
@JeiPlugin
public class TConJEIPlugin implements IModPlugin {

    private static final RecipeType<MaterialStatsWrapper> RECIPE_TYPE = RecipeType.create(MOD_ID, "material_stats", MaterialStatsWrapper.class);
    private static final RecipeType<ToolPartWrapper> TOOL_PARTS = RecipeType.create(MOD_ID, "tool_parts", ToolPartWrapper.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MOD_ID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RECIPE_TYPE, recipes());
        registration.addRecipes(TOOL_PARTS, getToolParts());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MaterialStatsCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ToolPartCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TinkerTables.tinkerStation.asItem()), RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(TinkerTables.tinkersAnvil.asItem()), RECIPE_TYPE);
    }

    private List<MaterialStatsWrapper> recipes() {
        return MaterialRegistry.getMaterials()
                .stream()
                .filter(iMaterial -> !iMaterial.isHidden())
                .map(stats -> new MaterialStatsWrapper((Material) stats))
                .filter(MaterialStatsWrapper::hasTraits)
                .collect(Collectors.toList());
    }

    private List<ToolPartWrapper> getToolParts() {
        return RegistryHelper.getTagValueStream(Registry.ITEM, TinkerTags.Items.TOOL_PARTS)
                .filter(part -> part instanceof IToolPart)
                .map(part -> new ToolPartWrapper((IToolPart) part))
                .collect(Collectors.toList());
    }


}
