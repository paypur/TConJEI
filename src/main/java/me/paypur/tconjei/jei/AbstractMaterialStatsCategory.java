package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.paypur.tconjei.ColorManager;
import me.paypur.tconjei.TConJEI;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

import static me.paypur.tconjei.ColorManager.TEXT_COLOR;
import static me.paypur.tconjei.ColorManager.getShade;
import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.RENDER_ONLY;

public abstract class AbstractMaterialStatsCategory implements IRecipeCategory<MaterialStatsWrapper> {

    protected static final Font FONT = Minecraft.getInstance().font;
    protected static final int LINE_HEIGHT = 10;
    protected static final float LINE_SPACING = 0.5f;
    protected static final int WIDTH = 176;
    protected static final int HEIGHT = 200;
    protected Component title;
    protected RecipeType<MaterialStatsWrapper> recipeType;
    protected IDrawable background, icon;
    protected TagKey<Item> tag;

    public AbstractMaterialStatsCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MaterialStatsWrapper recipe, IFocusGroup focuses) {
        final FluidStack fluidStack = recipe.getFluidStack();
        if (!fluidStack.isEmpty()) {
            final int bucket = 1000; // milli buckets
            builder.addSlot(RENDER_ONLY, 18, 0).addFluidStack(fluidStack.getFluid(), bucket);
            builder.addInvisibleIngredients(INPUT).addFluidStack(fluidStack.getFluid(), bucket);
        }
        final List<ItemStack> inputs = recipe.getInputs();
        final List<ItemStack> inputsParts = recipe.getInputsParts(tag);
        builder.addSlot(RENDER_ONLY, 0, 0).addItemStacks(inputs);
        builder.addSlot(RENDER_ONLY, WIDTH - 16, 0).addItemStacks(inputsParts);
        builder.addInvisibleIngredients(INPUT).addItemStacks(inputs);
        builder.addInvisibleIngredients(INPUT).addItemStacks(inputsParts);
    }

    @Override
    public void draw(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        final int tier = wrapper.material().getTier();
        final int color = MaterialTooltipCache.getColor(wrapper.getMaterialId()).getValue();
        drawShadowCentered(stack, ForgeI18n.getPattern(Util.makeTranslationKey("material", wrapper.getMaterialId())), 0, color);
        drawShadowCentered(stack, Component.translatable("tconjei.tooltip.tier", tier).getString(), 1, ColorManager.getTierColor(tier).orElse(color));
    }

    @Override
    public List<Component> getTooltipStrings(MaterialStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String MATERIAL_NAME = ForgeI18n.getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        final int materialWidth = FONT.width(MATERIAL_NAME);
        if (Utils.inBox(mouseX, mouseY, (WIDTH - materialWidth) / 2f, -1, materialWidth, LINE_HEIGHT)) {
            return List.of(Component.translatable(Util.makeTranslationKey("material", recipe.getMaterialId()) + ".flavor").withStyle(ChatFormatting.ITALIC));
        }
        return List.of();
    }

    protected void drawString(PoseStack stack, String string, int x, float lineNumber, int color) {
        FONT.draw(stack, string, x, lineNumber * LINE_HEIGHT, color);
    }

    protected void drawShadow(PoseStack stack, String string, int x, float lineNumber, int color) {
        drawString(stack, string, x + 1, lineNumber + 0.1f, getShade(color, 6));
        drawString(stack, string, x, lineNumber, color);
    }

    protected void drawShadowCentered(PoseStack stack, String string, float lineNumber, int color) {
        final int x = (WIDTH - FONT.width(string)) / 2;
        drawString(stack, string, x + 1, lineNumber + 0.1f, getShade(color, 6));
        drawString(stack, string, x, lineNumber, color);
    }

    protected void drawStatsShadow(PoseStack stack, Component component, float lineNumber, int color) {
        final String[] strings = Utils.colonSplit(component.getString());
        final int width = FONT.width(strings[0]);
        drawString(stack, strings[0], 0, lineNumber, TEXT_COLOR);
        drawShadow(stack, strings[1], width, lineNumber, color);
    }

    protected void drawTraits(PoseStack stack, List<ModifierEntry> traits, float lineNumber) {
        for (ModifierEntry trait : traits) {
            final String string = ForgeI18n.getPattern(Util.makeTranslationKey("modifier", trait.getId()));
            final int color = ResourceColorManager.getColor(Util.makeTranslationKey("modifier", trait.getId()));
            drawShadow(stack, string, WIDTH - FONT.width(string), lineNumber++, color);
        }
    }

    protected List<Component> getStatTooltip(IMaterialStats stats, int i, double mouseX, double mouseY, float lineNumber) {
        final int width = FONT.width(Utils.colonSplit(stats.getLocalizedInfo().get(i).getString())[0]);
        if (Utils.inBox(mouseX, mouseY, 0, lineNumber * LINE_HEIGHT - 1, width, LINE_HEIGHT)) {
            return List.of(stats.getLocalizedDescriptions().get(i));
        }
        return List.of();
    }

    protected List<Component> getTraitTooltips(List<ModifierEntry> traits, double mouseX, double mouseY, float lineNumber) {
        for (ModifierEntry trait : traits) {
            final String key = Util.makeTranslationKey("modifier", trait.getId());
            final int width = FONT.width(ForgeI18n.getPattern(key));
            if (Utils.inBox(mouseX, mouseY, WIDTH - width, lineNumber++ * LINE_HEIGHT - 1, width, LINE_HEIGHT)) {
                return List.of(Component.translatable(key + ".flavor").withStyle(ChatFormatting.ITALIC),
                        Component.translatable(key + ".description"));
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
    public RecipeType<MaterialStatsWrapper> getRecipeType() {
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
