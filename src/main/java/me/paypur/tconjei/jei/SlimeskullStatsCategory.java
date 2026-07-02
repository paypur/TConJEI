package me.paypur.tconjei.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;
import java.util.Optional;


public class SlimeskullStatsCategory extends MaterialStatsCategory {

    public SlimeskullStatsCategory(IGuiHelper guiHelper, RecipeType<MaterialStatsWrapper> recipeType, List<MaterialStatsId> statsIds, Component title, IDrawable icon) {
        super(guiHelper, recipeType, statsIds, title, icon, null);
    }

    // taken from ContentMaterialSkull
    @Override
    protected List<ItemStack> getInputsParts(MaterialId materialId) {
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            Optional<IDisplayableCastingRecipe> optional = world.getRecipeManager().getAllRecipesFor(TinkerRecipeTypes.CASTING_BASIN.get()).stream()
                    .filter(recipe -> recipe instanceof IDisplayableCastingRecipe)
                    .map(recipe -> (IDisplayableCastingRecipe) recipe)
                    .filter(recipe -> {
                        ItemStack output = recipe.getOutput();
                        return output.getItem() == TinkerTools.slimesuit.get(ArmorItem.Type.HELMET) && MaterialIdNBT.from(output).getMaterial(0).getId().equals(materialId);
                    })
                    .findFirst();
            if (optional.isPresent()) return optional.get().getCastItems();
        }
        return List.of();
    }

}
