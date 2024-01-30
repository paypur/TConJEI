package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Optional;

import static me.paypur.tconjei.TConJEI.MOD_ID;
import static net.minecraftforge.common.ForgeI18n.getPattern;

public class ToolStatsCategory implements IRecipeCategory<ToolStatsRecipe> {

    public static final int WIDTH = 176, HEIGHT = 192;
    public static ResourceLocation UID = new ResourceLocation(MOD_ID, "tool_stats");
    private final IDrawable BACKGROUND, ICON;

    public ToolStatsCategory(IGuiHelper guiHelper) {
        this.BACKGROUND = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.ICON = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/icon.png"), 0, 0, 16, 16);
    }

    @Override
    public void draw(ToolStatsRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        String materialName = recipe.material.getIdentifier().getId().getPath();
        Font font = Minecraft.getInstance().font;

        int textColor = MaterialTooltipCache.getColor(recipe.material.getIdentifier()).getValue();
        float textWidth = font.getSplitter().stringWidth(materialName);

        font.drawShadow(poseStack, getPattern("material.tconstruct." + recipe.material.getIdentifier().getId().getPath()), ((WIDTH - textWidth) / 2), 3, textColor);

        Optional<HeadMaterialStats> headStats = MaterialRegistry.getInstance().getMaterialStats(recipe.material.getIdentifier(), HeadMaterialStats.ID);
        if (headStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.head")), 0, 20, 8);
            font.draw(poseStack, String.format("%s%d", getPattern("tool_stat.tconstruct.durability"), headStats.get().getDurability()), 0, 30, 4144959);
            font.draw(poseStack, String.format("%s%s", getPattern("tool_stat.tconstruct.harvest_tier"), headStats.get().getTierId().getPath()), 0, 40, 4144959);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.mining_speed"), headStats.get().getMiningSpeed()), 0, 50, 4144959);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.attack_damage"), headStats.get().getAttack()), 0, 60, 4144959);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolStatsRecipe recipe, IFocusGroup focuses) {
        if (!recipe.material.isCraftable()) {
            // float is in milli buckets
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 18, 0).addFluidStack(recipe.getFluidStack().getFluid(), 1000);
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addFluidStack(recipe.getFluidStack().getFluid(), 1000);
            builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addFluidStack(recipe.getFluidStack().getFluid(), 1000);
        }
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0).addItemStacks(recipe.getRepresentativeItems());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.getRepresentativeItems());
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStacks(recipe.getRepresentativeItems());

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, (WIDTH - 18), 0).addItemStacks(recipe.getParts());
    }

    @Override
    public Component getTitle() {
        return new TextComponent("Tool Stats");
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public IDrawable getIcon() {
        return ICON;
    }

    @Override
    public IDrawable getBackground() {
        return BACKGROUND;
    }

    @SuppressWarnings("removal")
    @Override
    public Class<? extends ToolStatsRecipe> getRecipeClass() {
        return ToolStatsRecipe.class;
    }

    @Override
    public RecipeType<ToolStatsRecipe> getRecipeType() {
        return RecipeType.create(MOD_ID, "tool_stats", getRecipeClass());
    }

}
