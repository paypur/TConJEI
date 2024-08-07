package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.paypur.tconjei.Utils;
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
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

import static me.paypur.tconjei.ColorManager.TEXT_COLOR;
import static me.paypur.tconjei.ColorManager.getShade;
import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.RENDER_ONLY;

public abstract class AbstractToolStatsCategory implements IRecipeCategory<ToolStatsWrapper> {

    static protected final Font FONT = Minecraft.getInstance().font;
    static protected final int LINE_HEIGHT = 10;
    static protected final float LINE_SPACING = 0.5f;
    static protected final int WIDTH = 172;
    static protected final int HEIGHT = 200;
    protected Component title;
    protected RecipeType<ToolStatsWrapper> recipeType;
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

    @Override
    public List<Component> getTooltipStrings(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String MATERIAL_NAME = ForgeI18n.getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        final int materialWidth = FONT.width(MATERIAL_NAME);
        if (Utils.inBox(mouseX, mouseY, (WIDTH - materialWidth) / 2f, 0.4f * LINE_HEIGHT - 1, materialWidth, LINE_HEIGHT)) {
            return List.of(MutableComponent.create(new TranslatableContents(Util.makeTranslationKey("material", recipe.getMaterialId()) + ".flavor")).withStyle(ChatFormatting.ITALIC));
        }
        return List.of();
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

    protected void drawTraits(PoseStack stack, List<ModifierEntry> traits, float lineNumber) {
        for (ModifierEntry trait : traits) {
            String pattern = ForgeI18n.getPattern(Util.makeTranslationKey("modifier", trait.getId()));
            int traitColor = ResourceColorManager.getColor(Util.makeTranslationKey("modifier", trait.getId()));
            drawShadow(stack, pattern, WIDTH - FONT.width(pattern), lineNumber++, traitColor);
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
            int textWidth = FONT.width(ForgeI18n.getPattern(Util.makeTranslationKey("modifier", trait.getId())));
            if (Utils.inBox(mouseX, mouseY, WIDTH - textWidth, lineNumber++ * LINE_HEIGHT - 1, textWidth, LINE_HEIGHT)) {
                return List.of(MutableComponent.create(new TranslatableContents(Util.makeTranslationKey("modifier", trait.getId()) + ".flavor")).withStyle(ChatFormatting.ITALIC),
                        MutableComponent.create(new TranslatableContents(Util.makeTranslationKey("modifier", trait.getId()) + ".description")));
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
