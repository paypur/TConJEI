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

public class ToolPartCategory implements IRecipeCategory<ToolPartWrapper> {

    final ResourceLocation UID = new ResourceLocation(MOD_ID, "tool_parts");
    final IDrawable BACKGROUND, ICON;

    public ToolPartCategory(IGuiHelper guiHelper) {
        this.BACKGROUND = guiHelper.createBlankDrawable(40,20);
        this.ICON = guiHelper.createBlankDrawable(16,16);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolPartWrapper recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addItemStacks(recipe.getToolParts());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 20, 0).addItemStacks(recipe.getCraftableTools());
    }

    @Override
    public void draw(ToolPartWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);
    }

    @Override
    public Component getTitle() {
        return new TextComponent("Tool Recipe");
    }

    @Override
    public IDrawable getBackground() {
        return this.BACKGROUND;
    }

    @Override
    public IDrawable getIcon() {
        return this.ICON;
    }

    @Override
    public ResourceLocation getUid() {
        return this.UID;
    }

    @Override
    public Class<? extends ToolPartWrapper> getRecipeClass() {
        return ToolPartWrapper.class;
    }

    @Override
    public RecipeType<ToolPartWrapper> getRecipeType() {
        return RecipeType.create(MOD_ID, "tool_parts", getRecipeClass());
    }
}
