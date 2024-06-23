package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.utils.Util;

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

    static protected final Font FONT = Minecraft.getInstance().font;
    static protected final int LINE_HEIGHT = 10;
    static protected final float LINE_SPACING = 0.5f;
    protected Component title;
    protected RecipeType<ToolStatsWrapper> recipeType;
    protected IDrawable background, icon;
    protected TagKey<Item> tag;
    protected int WIDTH, HEIGHT;

    protected void createBackground(IGuiHelper guiHelper) {
        background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
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

    @Override
    public List<Component> getTooltipStrings(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String MATERIAL_NAME = getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        final int materialWidth = FONT.width(MATERIAL_NAME);
        if (inBox(mouseX, mouseY, (WIDTH - materialWidth) / 2f, 0.4f * LINE_HEIGHT - 1, materialWidth, LINE_HEIGHT)) {
            return List.of(MutableComponent.create(new TranslatableContents( makeTranslationKey("material", recipe.getMaterialId()) + ".flavor")).withStyle(ChatFormatting.ITALIC));
        }
        return List.of();
    }

    protected void drawStatsShadow(PoseStack poseStack, Component component, float lineNumber, int ACCENT_COLOR) {
        String[] strings = component.getString().split(":");
        strings[0] += ":";
        float width = FONT.width(strings[0]);
        FONT.draw(poseStack, strings[0], 0, lineNumber * LINE_HEIGHT, TEXT_COLOR);
        drawShadow(poseStack, strings[1], width, lineNumber, ACCENT_COLOR);
    }

    protected void drawStatsShadow(PoseStack poseStack, String string, float lineNumber, int ACCENT_COLOR) {
        String[] strings = string.split(":");
        strings[0] += ":";
        float width = FONT.width(strings[0]);
        FONT.draw(poseStack, strings[0], 0, lineNumber * LINE_HEIGHT, TEXT_COLOR);
        drawShadow(poseStack, strings[1], width, lineNumber, ACCENT_COLOR);
    }

    protected void drawTraits(PoseStack poseStack, List<ModifierEntry> traits, float lineNumber) {
        for (ModifierEntry trait : traits) {
            String pattern = getPattern(makeTranslationKey("modifier", trait.getId()));
            int traitColor = ResourceColorManager.getColor(makeTranslationKey("modifier", trait.getId()));
            drawShadow(poseStack, pattern, WIDTH - FONT.getSplitter().stringWidth(pattern), lineNumber++, traitColor);
        }
    }

    protected void drawShadow(PoseStack poseStack, String string, float x, float lineNumber, int color) {
        FONT.draw(poseStack, string, x + 1f, lineNumber * LINE_HEIGHT + 1f, getShade(color, 6));
        FONT.draw(poseStack, string, x, lineNumber * LINE_HEIGHT, color);
    }

    protected List<Component> getStatTooltip(IMaterialStats stats, int i, double mouseX, double mouseY, float lineNumber) {
        int textWidth = FONT.width(stats.getLocalizedInfo().get(i).getString().split(":")[0]);
        if (inBox(mouseX, mouseY, 0, lineNumber * LINE_HEIGHT - 1, textWidth, LINE_HEIGHT)) {
            return List.of(stats.getLocalizedDescriptions().get(i));
        }
        return List.of();
    }

    protected List<Component> getTraitTooltips(List<ModifierEntry> traits, double mouseX, double mouseY, float lineNumber) {
        for (ModifierEntry trait : traits) {
            int textWidth = FONT.width(getPattern(makeTranslationKey("modifier", trait.getId())));
            if (inBox(mouseX, mouseY, WIDTH - textWidth, lineNumber++ * LINE_HEIGHT - 1, textWidth, LINE_HEIGHT)) {
                return List.of(MutableComponent.create(new TranslatableContents(makeTranslationKey("modifier", trait.getId()) + ".flavor")).withStyle(ChatFormatting.ITALIC),
                        MutableComponent.create(new TranslatableContents(makeTranslationKey("modifier", trait.getId()) + ".description")));
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

}
