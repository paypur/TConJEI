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
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Optional;

import static me.paypur.tconjei.TConJEI.MOD_ID;

public class ToolStatsCategory implements IRecipeCategory<ToolStatsRecipe> {

    public static ResourceLocation UID = new ResourceLocation(MOD_ID, "tool_stats");
    public static final int WIDTH = 176, HEIGHT = 128;
    private final IDrawable BACKGROUND, ICON;

    public ToolStatsCategory(IGuiHelper guiHelper) {
        this.BACKGROUND = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.ICON = guiHelper.createDrawable(new ResourceLocation(MOD_ID,"textures/gui/icon.png"), 0 ,0,16,16);
    }
    @Override
    public void draw(ToolStatsRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        String materialName = recipe.material.getIdentifier().getId().getPath();
        Font font = Minecraft.getInstance().font;

        int textColor = MaterialTooltipCache.getColor(recipe.material.getIdentifier()).getValue();
        float textWidth = font.getSplitter().stringWidth(materialName);
        font.draw(poseStack, recipe.material.getIdentifier().getId().getPath(), ((WIDTH - textWidth) / 2), 3, 8);

        Optional<HeadMaterialStats> headStats = MaterialRegistry.getInstance().getMaterialStats(recipe.material.getIdentifier(), HeadMaterialStats.ID);
        if (headStats.isPresent()) {
            font.draw(poseStack, String.format("Durability: %d", headStats.get().getDurability()), 0 , 20, 8);
            font.draw(poseStack, String.format("Harvest Level: %s", headStats.get().getTierId().getPath()), 0 , 30, 8);
            font.draw(poseStack, String.format("Mining Speed: %.2f", headStats.get().getMiningSpeed()), 0 , 40, 8);
            font.draw(poseStack, String.format("Attack Damage %.2f", headStats.get().getAttack()), -0 , 50, 8);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolStatsRecipe recipe, IFocusGroup focuses) {
        if (!recipe.material.isCraftable()) {
            // float is in milli buckets
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 18 ,0).addFluidStack(recipe.getFluidStack().getFluid(), 1000);
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addFluidStack(recipe.getFluidStack().getFluid(), 1000);
            builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addFluidStack(recipe.getFluidStack().getFluid(), 1000);
        }
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY,0,0).addItemStacks(recipe.getRepresentativeItems());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.getRepresentativeItems());
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStacks(recipe.getRepresentativeItems());

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, (WIDTH - 18),0).addItemStacks(recipe.getParts());
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
