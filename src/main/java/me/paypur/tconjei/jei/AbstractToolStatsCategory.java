package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.Collections;
import java.util.List;

import static me.paypur.tconjei.ColorManager.TEXT_COLOR;
import static me.paypur.tconjei.ColorManager.getShade;
import static me.paypur.tconjei.TConJEI.inBox;
import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.RENDER_ONLY;
import static net.minecraftforge.common.ForgeI18n.getPattern;
import static slimeknights.mantle.client.ResourceColorManager.getColor;
import static slimeknights.tconstruct.library.utils.Util.makeTranslationKey;

public abstract class AbstractToolStatsCategory implements IRecipeCategory<ToolStatsWrapper> {

    protected Component title;
    protected RecipeType<ToolStatsWrapper> recipeType;
    protected ResourceLocation uid;
    protected IDrawable background, icon;
    static protected final Font FONT = Minecraft.getInstance().font;
    protected final int WIDTH = 172, HEIGHT = 140, LINE_HEIGHT = 10;
    protected float LINE_SPACING = 0.5f;
    protected TagKey<Item> tag;

    public AbstractToolStatsCategory(IGuiHelper guiHelper, TagKey<Item> tag) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.tag = tag;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolStatsWrapper recipe, IFocusGroup focuses) {
        FluidStack fluidStack = recipe.getFluidStack();
        if (!fluidStack.isEmpty()) {
            final int BUCKET = 1000; // milli buckets
            builder.addSlot(RENDER_ONLY, 18, 0).addFluidStack(fluidStack.getFluid(), BUCKET);
            builder.addInvisibleIngredients(INPUT).addFluidStack(fluidStack.getFluid(), BUCKET);
        }
        List<ItemStack> inputs = recipe.getInputs();
        List<ItemStack> inputsParts = recipe.getInputsParts(tag);
        builder.addSlot(RENDER_ONLY, 0, 0).addItemStacks(inputs);
        builder.addSlot(RENDER_ONLY, WIDTH - 16, 0).addItemStacks(inputsParts);
        builder.addInvisibleIngredients(INPUT).addItemStacks(inputs);
        builder.addInvisibleIngredients(INPUT).addItemStacks(inputsParts);
    }

    protected void drawStatsShadow(PoseStack poseStack, String type, String stat, float lineNumber, int ACCENT_COLOR) {
        String pattern = getPattern(type);
        float width = FONT.width(pattern);
        FONT.draw(poseStack, pattern, 0, lineNumber * LINE_HEIGHT, TEXT_COLOR);
        drawShadow(poseStack, stat, width, lineNumber, ACCENT_COLOR);
    }

    protected void drawTraits(PoseStack poseStack, List<ModifierEntry> traits, float lineNumber) {
        for (ModifierEntry trait : traits) {
            String pattern = getPattern(makeTranslationKey("modifier", trait.getId()));
            int traitColor = getColor(makeTranslationKey("modifier", trait.getId()));
            drawShadow(poseStack, pattern, WIDTH - FONT.width(pattern), lineNumber++, traitColor);
        }
    }

    protected void drawShadow(PoseStack poseStack, String string, float x, float lineNumber, int color) {
        FONT.draw(poseStack, string, x + 1f, lineNumber * LINE_HEIGHT + 1f, getShade(color, 6));
        FONT.draw(poseStack, string, x, lineNumber * LINE_HEIGHT, color);
    }

    protected List<Component> getStatTooltip(String pattern, double mouseX, double mouseY, float lineNumber) {
        String string = getPattern(pattern);
        int textWidth = FONT.width(string);
        if (inBox(mouseX, mouseY, 0, lineNumber * LINE_HEIGHT - 1, textWidth, LINE_HEIGHT)) {
            return List.of(new TranslatableComponent(pattern + ".description"));
        }
        return Collections.emptyList();
    }

    protected List<Component> getTraitTooltips(List<ModifierEntry> traits, double mouseX, double mouseY, float lineNumber) {
        for (ModifierEntry trait : traits) {
            String string = getPattern(makeTranslationKey("modifier", trait.getId()));
            int textWidth = FONT.width(string);
            if (inBox(mouseX, mouseY, WIDTH - textWidth, lineNumber++ * LINE_HEIGHT - 1, textWidth, LINE_HEIGHT)) {
                return List.of(new TranslatableComponent(makeTranslationKey("modifier", trait.getId()) + ".flavor").withStyle(ChatFormatting.ITALIC),
                        new TranslatableComponent(makeTranslationKey("modifier", trait.getId()) + ".description"));
            }
        }
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public Component getTitle() {
        return title;
    }
    @NotNull
    @Override
    public RecipeType<ToolStatsWrapper> getRecipeType() {
        return recipeType;
    }

    @NotNull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @NotNull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @NotNull
    @Override
    public ResourceLocation getUid() {
        return this.uid;
    }

    @NotNull
    @Override
    public Class<? extends ToolStatsWrapper> getRecipeClass() {
        return ToolStatsWrapper.class;
    }

}
