package me.paypur.tconjei.jei;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.SkullStats;

import java.util.List;
import java.util.Optional;


public class SlimeskullStatsCategory extends AbstractMaterialStatsCategory {

    public SlimeskullStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawableItemLike(Items.SKELETON_SKULL);
        this.title = Component.translatable("tconjei.tool_stats.skull");
        this.statsIds = List.of(SkullStats.ID);
        this.recipeType = TConJEIPlugin.SKULL_STATS;
    }

    // taken from ContentMaterialSkull
    @Override
    public List<ItemStack> getInputsParts(MaterialId materialId) {
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
