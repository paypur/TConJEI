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
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.ArrayList;

import static me.paypur.tconjei.TConJEI.MOD_ID;

@SuppressWarnings("unused")
@JeiPlugin
public class TConJEIPlugin implements IModPlugin {

    public static final RecipeType<ToolStatsWrapper> RECIPE_TYPE = RecipeType.create(MOD_ID, "tool_stats", ToolStatsWrapper.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MOD_ID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RECIPE_TYPE, recipes(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ToolStatsCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TinkerTables.tinkerStation.asItem()), RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(TinkerTables.tinkersAnvil.asItem()), RECIPE_TYPE);
    }

    private ArrayList<ToolStatsWrapper> recipes(IGuiHelper guiHelper) {
        ArrayList<ToolStatsWrapper> list = new ArrayList<>();
        for (IMaterial material : MaterialRegistry.getMaterials())
            // && material.hasItems() && !material.getAllStats().isEmpty()
            if (!material.isHidden()) {
                list.add(new ToolStatsWrapper((Material) material));
            }
        return list;
    }

}
