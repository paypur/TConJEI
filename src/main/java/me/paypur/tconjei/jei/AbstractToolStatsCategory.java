package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.paypur.tconjei.Utils;
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
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

import static me.paypur.tconjei.ColorManager.TEXT_COLOR;
import static me.paypur.tconjei.ColorManager.getShade;
import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.RENDER_ONLY;
import static net.minecraftforge.common.ForgeI18n.getPattern;
import static slimeknights.mantle.client.ResourceColorManager.getColor;

public abstract class AbstractToolStatsCategory implements IRecipeCategory<ToolStatsWrapper> {

    static protected final Font FONT = Minecraft.getInstance().font;
    static protected final int LINE_HEIGHT = 10;
    static protected final float LINE_SPACING = 0.5f;
    static protected final int WIDTH = 172;
    static protected final int HEIGHT = 200;
    protected Component title;
    protected RecipeType<ToolStatsWrapper> recipeType;
    protected ResourceLocation uid;
    protected IDrawable background, icon;
    protected TagKey<Item> tag;

    public AbstractToolStatsCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
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

    protected void draw(PoseStack stack, String string, int x, float lineNumber, int color) {
        FONT.draw(stack, string, x, lineNumber * LINE_HEIGHT, color);
    }

    protected void drawShadow(PoseStack stack, String string, int x, float lineNumber, int color) {
        draw(stack, string, x + 1, lineNumber + 0.1f, getShade(color, 6));
        draw(stack, string, x, lineNumber, color);
    }

    protected void drawStatsShadow(PoseStack stack, Component component, float lineNumber, int color) {
        String[] strings = Utils.colonSplit(component.getString());
        int width = FONT.width(strings[0]);
        draw(stack, strings[0], 0, lineNumber, TEXT_COLOR);
        drawShadow(stack, strings[1], width, lineNumber, color);
    }

    protected void drawTraits(PoseStack poseStack, List<ModifierEntry> traits, float lineNumber) {
        for (ModifierEntry trait : traits) {
            String pattern = getPattern(Util.makeTranslationKey("modifier", trait.getId()));
            int traitColor = getColor(Util.makeTranslationKey("modifier", trait.getId()));
            drawShadow(poseStack, pattern, WIDTH - FONT.width(pattern), lineNumber++, traitColor);
        }
    }

    protected List<Component> getStatTooltip(IMaterialStats stats, int i, double mouseX, double mouseY, float lineNumber) {
        int textWidth = FONT.width(Utils.colonSplit(stats.getLocalizedInfo().get(i).getString())[0]);
        if (Utils.inBox(mouseX, mouseY, 0, lineNumber * LINE_HEIGHT - 1, textWidth, LINE_HEIGHT)) {
            return List.of(stats.getLocalizedDescriptions().get(i));
        }
        return List.of();
    }

    protected List<Component> getTraitTooltips(List<ModifierEntry> traits, double mouseX, double mouseY, float lineNumber) {
        for (ModifierEntry trait : traits) {
            int textWidth = FONT.width(getPattern(Util.makeTranslationKey("modifier", trait.getId())));
            if (Utils.inBox(mouseX, mouseY, WIDTH - textWidth, lineNumber++ * LINE_HEIGHT - 1, textWidth, LINE_HEIGHT)) {
                return List.of(new TranslatableComponent(Util.makeTranslationKey("modifier", trait.getId()) + ".flavor").withStyle(ChatFormatting.ITALIC),
                        new TranslatableComponent(Util.makeTranslationKey("modifier", trait.getId()) + ".description"));
            }
        }
        return List.of();
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
