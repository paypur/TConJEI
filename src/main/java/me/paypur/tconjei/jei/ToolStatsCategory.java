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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static me.paypur.tconjei.TConJEI.MOD_ID;

public class ToolStatsCategory implements IRecipeCategory<ToolStatsRecipe> {

    public static ResourceLocation UID = new ResourceLocation(MOD_ID, "tool_stats");
    public static final int WIDTH  = 182, HEIGHT = 128;
    private final IDrawable BACKGROUND, ICON;

    public ToolStatsCategory(IGuiHelper guiHelper) {
        this.BACKGROUND = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.ICON = guiHelper.createDrawable(new ResourceLocation(MOD_ID,"textures/gui/icon.png"), 0 ,0,16,16);
    }
    @Override
    public void draw(ToolStatsRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolStatsRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> representative = recipe.getRepresentativeItems();
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addItemStacks(representative);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 2, 2).addItemStacks(representative);
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
