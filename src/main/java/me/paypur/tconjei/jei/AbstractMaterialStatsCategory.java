package me.paypur.tconjei.jei;

import me.paypur.tconjei.ColorProvider;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.utils.Util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.RENDER_ONLY;

public abstract class AbstractMaterialStatsCategory implements IRecipeCategory<MaterialStatsWrapper> {

    protected static final Font FONT = Minecraft.getInstance().font;
    protected static final int LINE_HEIGHT = 10;
    protected static final float LINE_SPACING = 0.5f;
    protected static final int WIDTH = 178;
    protected static final int HEIGHT = 200;
    protected RecipeType<MaterialStatsWrapper> recipeType;
    protected Component title;
    protected IDrawable background, icon;
    @Nullable
    protected TagKey<Item> tag;

    public AbstractMaterialStatsCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MaterialStatsWrapper wrapper, IFocusGroup focuses) {
        final FluidStack fluidStack = wrapper.getFluidStack();
        if (!fluidStack.isEmpty()) {
            final int bucket = 1000; // milli buckets
            builder.addSlot(RENDER_ONLY, 18, 0).addFluidStack(fluidStack.getFluid(), bucket);
            builder.addInvisibleIngredients(INPUT).addFluidStack(fluidStack.getFluid(), bucket);
        }
        final List<ItemStack> inputs = wrapper.getInputs();
        final List<ItemStack> inputsParts = getInputsParts(wrapper.getMaterialId());
        builder.addSlot(RENDER_ONLY, 0, 0).addItemStacks(inputs);
        builder.addSlot(RENDER_ONLY, WIDTH - 16, 0).addItemStacks(inputsParts);
        builder.addInvisibleIngredients(INPUT).addItemStacks(inputs);
        builder.addInvisibleIngredients(INPUT).addItemStacks(inputsParts);
    }

    @Override
    public void draw(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        final int tier = wrapper.material().getTier();
        final int color = MaterialTooltipCache.getColor(wrapper.getMaterialId()).getValue();
        drawComponentShadowCentered(gui, Component.translatable(Util.makeTranslationKey("material", wrapper.getMaterialId())).withStyle(ChatFormatting.UNDERLINE), 0, color);
        drawComponentShadowCentered(gui, Component.translatable("tconjei.tooltip.tier", tier), 1, ColorProvider.getTierColor(tier).orElse(color));
    }

    protected List<ItemStack> getInputsParts(MaterialId materialId) {
        if (tag == null) return List.of();
        Set<Item> seen = new HashSet<>();
        return RegistryHelper.getTagValueStream(tag)
                .filter(item -> item instanceof IModifiable)
                .flatMap(item -> ToolPartsHook.parts(((IModifiable) item).getToolDefinition()).stream()
                        .filter(part -> part.canUseMaterial(materialId))
                        .map(part -> part.withMaterial(materialId))
                )
                .filter(part -> seen.add(part.getItem()))
                .sorted(Comparator.comparing(a -> a.getItem().getDescriptionId()))
                .toList();
    }

    public final List<Component> getMaterialTooltip(MaterialStatsWrapper wrapper, double mouseX, double mouseY) {
        final String key = Util.makeTranslationKey("material", wrapper.getMaterialId());
        final int width = FONT.width(ForgeI18n.getPattern(key));
        // TODO: doesnt line up with actual ColorProvider.TEXT, slightly to the left
        if (Utils.inBox(mouseX, mouseY, (WIDTH - width) / 2f, -1, width, LINE_HEIGHT)) {
            return List.of(Component.translatable(key + ".flavor").withStyle(ChatFormatting.ITALIC));
        }
        return List.of();
    }

    protected final void drawString(GuiGraphics gui, String string, int x, float lineNumber, int color, boolean shadow) {
        final int y = (int) (lineNumber * LINE_HEIGHT);
        gui.drawString(FONT, string, x, y, color, shadow);
    }

    protected final void drawComponent(GuiGraphics gui, Component component, int x, float lineNumber, int color, boolean shadow) {
        final int y = (int) (lineNumber * LINE_HEIGHT);
        gui.drawString(FONT, component, x, y, color, shadow);
    }

    protected final void drawStatComponent(GuiGraphics gui, Component component, float lineNumber) {
        Component sibling = component.getSiblings().get(0);
        drawComponent(gui, sibling.plainCopy(), FONT.width(component.plainCopy()), lineNumber, sibling.getStyle().getColor().getValue(), true);
        drawComponent(gui, component.plainCopy(), 0, lineNumber, ColorProvider.TEXT, false);
    }

    protected final void drawComponentShadowCentered(GuiGraphics gui, Component component, float lineNumber, int color) {
        drawComponent(gui, component, (WIDTH - FONT.width(component)) / 2, lineNumber, color, true);
    }

    protected final void drawTraits(GuiGraphics gui, List<ModifierEntry> traits, float lineNumber) {
        for (ModifierEntry trait : traits) {
            final Component component = trait.getDisplayName().copy().withStyle(style -> style.withColor((TextColor) null));
            final int color = ResourceColorManager.getColor(Util.makeTranslationKey("modifier", trait.getId()));
            drawComponent(gui, component, WIDTH - FONT.width(component), lineNumber++, color, true);
        }
    }

    protected final List<Component> getStatTooltip(IMaterialStats stats, int i, double mouseX, double mouseY, float lineNumber) {
        final int width = FONT.width(stats.getLocalizedInfo().get(i).plainCopy());
        if (Utils.inBox(mouseX, mouseY, 0, lineNumber * LINE_HEIGHT - 1, width, LINE_HEIGHT)) {
            return List.of(stats.getLocalizedDescriptions().get(i));
        }
        return List.of();
    }

    protected final List<Component> getTraitTooltips(List<ModifierEntry> traits, double mouseX, double mouseY, float lineNumber) {
        for (ModifierEntry trait : traits) {
            final String key = Util.makeTranslationKey("modifier", trait.getId());
            final int width = FONT.width(trait.getDisplayName());
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
        return this.title;
    }

    @NotNull
    @Override
    public RecipeType<MaterialStatsWrapper> getRecipeType() {
        return this.recipeType;
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
