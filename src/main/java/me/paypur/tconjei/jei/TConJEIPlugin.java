package me.paypur.tconjei.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.tables.TinkerTables;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static me.paypur.tconjei.TConJEI.MOD_ID;

@SuppressWarnings("unused")
@JeiPlugin
public class TConJEIPlugin implements IModPlugin {

    private static final RecipeType<MaterialStatsWrapper> RECIPE_TYPE = RecipeType.create(MOD_ID, "material_stats", MaterialStatsWrapper.class);

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
        registration.addRecipeCategories(new MaterialStatsCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TinkerTables.tinkerStation.asItem()), RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(TinkerTables.tinkersAnvil.asItem()), RECIPE_TYPE);
    }

    private ArrayList<MaterialStatsWrapper> recipes(IGuiHelper guiHelper) {
        ArrayList<MaterialStatsWrapper> list = new ArrayList<>();
        for (IMaterial material : MaterialRegistry.getMaterials())
            // && material.hasItems() && !material.getAllStats().isEmpty()
            if (!material.isHidden()) {
                list.add(new MaterialStatsWrapper((Material) material));
            }
        return list;
    }

}
