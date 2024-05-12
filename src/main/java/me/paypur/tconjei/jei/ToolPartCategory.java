package me.paypur.tconjei.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

import static me.paypur.tconjei.TConJEI.MOD_ID;

public class ToolPartCategory implements IRecipeCategory<ToolPartWrapper> {

    final ResourceLocation UID = new ResourceLocation(MOD_ID, "tool_parts");
    final IDrawable BACKGROUND, ICON;
    final int WIDTH = 120;
    final int HEIGHT = 60;
    final int ITEM_SIZE = 16;

    public ToolPartCategory(IGuiHelper guiHelper) {
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/tool_parts.png"), 0, 0, WIDTH, HEIGHT);
        this.ICON = guiHelper.createDrawableItemStack(TinkerTools.cleaver.get().getRenderTool());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolPartWrapper recipe, IFocusGroup focuses) {
        recipe.getToolParts().forEach(parts -> builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(parts));

        List<LayoutSlot> slots = recipe.getSlots();
        List<ItemStack> items = recipe.getToolRecipe();

        assert items.size() == slots.size();

        int minX, maxX, minY, maxY;
        minX = slots.get(0).getX();
        maxX = slots.get(0).getX();
        minY = slots.get(0).getY();
        maxY = slots.get(0).getY();

        for (int i = 1; i < slots.size(); i++) {
            minX = Math.min(slots.get(i).getX(), minX);
            maxX = Math.max(slots.get(i).getX(), maxX);
            minY = Math.min(slots.get(i).getY(), minY);
            maxY = Math.max(slots.get(i).getY(), maxY);
        }

        // centers slots vertically
        int yOffset = (HEIGHT - (ITEM_SIZE + maxY - minY)) / 2 - minY;
        // centers slots horizontally within square
        int xOffset = (HEIGHT - (ITEM_SIZE + maxX - minX)) / 2 - minX;

        for (int i = 0; i < items.size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, slots.get(i).getX() + xOffset, slots.get(i).getY() + yOffset).addItemStack(items.get(i));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 25, (HEIGHT - ITEM_SIZE) / 2).addItemStack(recipe.getTool());
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
