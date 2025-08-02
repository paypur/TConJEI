package me.paypur.tconjei.jei;

import me.paypur.tconjei.Utils;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
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
        this.recipeType = TConJEIPlugin.SKULL_STATS;
    }

    @Override
    public void draw(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        super.draw(wrapper, recipeSlotsView, gui, mouseX, mouseY);

        final int color = MaterialTooltipCache.getColor(wrapper.getMaterialId()).getValue();
        float lineNumber = 2f;

        Optional<IMaterialStats> skullOptional = wrapper.getStats(SkullStats.ID);

        if (skullOptional.isPresent()) {
            IMaterialStats skull = skullOptional.get();
            drawTraits(gui, wrapper.getTraits(skull.getIdentifier()), lineNumber);
            drawComponent(gui, skull.getLocalizedName().withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);
            for (Component stat : skull.getLocalizedInfo()) {
                drawStatComponent(gui, stat, lineNumber++);
            }
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // MATERIAL
        List<Component> materialTooltips = getMaterialTooltip(wrapper, mouseX, mouseY);
        if (!materialTooltips.isEmpty()) {
            tooltip.addAll(materialTooltips);
            return;
        }

        Optional<IMaterialStats> skullOptional = wrapper.getStats(SkullStats.ID);

        float lineNumber = 2f;

        if (skullOptional.isPresent()) {
            IMaterialStats skull = skullOptional.get();

            List<Component> traitTooltips = getTraitTooltips(wrapper.getTraits(skull.getIdentifier()), mouseX, mouseY, lineNumber++);
            if (!traitTooltips.isEmpty()) {
                tooltip.addAll(traitTooltips);
                return;
            }

            assert skull.getLocalizedInfo().size() == skull.getLocalizedDescriptions().size();
            for (int i = 0; i < skull.getLocalizedDescriptions().size(); i++) {
                final int width = FONT.width(skull.getLocalizedInfo().get(i).plainCopy());
                if (Utils.inBox(mouseX, mouseY, 0, lineNumber++ * LINE_HEIGHT - 1, width, LINE_HEIGHT)) {
                    tooltip.add(skull.getLocalizedDescriptions().get(i));
                    return;
                }
            }
        }
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
