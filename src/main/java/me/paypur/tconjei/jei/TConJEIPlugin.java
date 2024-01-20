package me.paypur.tconjei.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.paypur.tconjei.TConJEI.MOD_ID;

@JeiPlugin
public class TConJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ToolStatsCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(recipes(registration.getJeiHelpers().getGuiHelper()), ToolStatsCategory.UID);
    }

    private ArrayList<ToolStatsRecipe> recipes(IGuiHelper guiHelper) {
        ArrayList<ToolStatsRecipe> list = new ArrayList<ToolStatsRecipe>();
        for (IMaterial material : MaterialRegistry.getMaterials())
            // && material.hasItems() && !material.getAllStats().isEmpty()
            if (!material.isHidden()) {
                list.add(new ToolStatsRecipe(material, guiHelper));
            }
        return list;
    }

}
