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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.stats.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.paypur.tconjei.ColorManager.*;
import static me.paypur.tconjei.TConJEI.MOD_ID;
import static me.paypur.tconjei.TConJEI.inBox;
import static net.minecraftforge.common.ForgeI18n.getPattern;

public class MaterialStatsCategory implements IRecipeCategory<MaterialStatsWrapper> {

    final MutableComponent TITLE = MutableComponent.create(new LiteralContents("Material Stats"));
    final RecipeType<MaterialStatsWrapper> RECIPE_TYPE = RecipeType.create(MOD_ID, "material_stats", MaterialStatsWrapper.class);
    final IDrawable BACKGROUND, ICON;
    final Font font = Minecraft.getInstance().font;
    final int WIDTH = 172, HEIGHT = 220;
    final int LINE_HEIGHT = 10;

    public MaterialStatsCategory(IGuiHelper guiHelper) {
        this.BACKGROUND = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.ICON = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/materialstats/icon.png"), 0, 0, 16, 16);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MaterialStatsWrapper recipe, IFocusGroup focuses) {
        FluidStack fluidStack = recipe.getFluidStack();
        if (!fluidStack.isEmpty()) {
            final int BUCKET = 1000; // milli buckets
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 18, 0).addFluidStack(fluidStack.getFluid(), BUCKET);
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addFluidStack(fluidStack.getFluid(), BUCKET);
        }
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0).addItemStacks(recipe.getItemStacks());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.getItemStacks());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, WIDTH - 16, 0).addItemStacks(recipe.getToolParts());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.getToolParts());
    }

    @Override
    public void draw(MaterialStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        final String materialName = getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(recipe.getMaterialId()).getValue();
        // Name
        font.drawShadow(stack, materialName, (WIDTH - font.width(materialName)) / 2f, 0.4f, MATERIAL_COLOR);

        Optional<HeadMaterialStats> headOptional = recipe.getStats(HeadMaterialStats.ID);
        Optional<IMaterialStats> extraOptional = recipe.getStats(StatlessMaterialStats.BINDING.getIdentifier());
        Optional<HandleMaterialStats> handleOptional = recipe.getStats(HandleMaterialStats.ID);
        Optional<LimbMaterialStats> limbOptional = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = recipe.getStats(GripMaterialStats.ID);
        Optional<IMaterialStats> stringOptional = recipe.getStats(StatlessMaterialStats.BOWSTRING.getIdentifier());

        float lineNumber = 2f;
        // HEAD
        if (headOptional.isPresent()) {
            HeadMaterialStats head = headOptional.get();
            drawTraits(stack, recipe, HeadMaterialStats.ID, lineNumber);
            font.drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.head")), 0, lineNumber++, MATERIAL_COLOR);
            drawStats(stack, head.getLocalizedInfo().get(0), lineNumber++, DURABILITY_COLOR);
            drawStats(stack, head.getLocalizedInfo().get(1), lineNumber++, head.getLocalizedInfo().get(1).getSiblings().get(0).getStyle().getColor().getValue());
            drawStats(stack, head.getLocalizedInfo().get(2), lineNumber++, MINING_COLOR);
            drawStats(stack, head.getLocalizedInfo().get(3), lineNumber++, ATTACK_COLOR);
            lineNumber += 0.4f;
        }
        // EXTRA
        // only draw extra if others don't exist
        else if (extraOptional.isPresent()) {
            drawTraits(stack, recipe, StatlessMaterialStats.BINDING.getIdentifier(), lineNumber);
            font.drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.binding")), 0, lineNumber++, MATERIAL_COLOR);
            lineNumber += 0.4f;
        }
        // HANDLE
        if (handleOptional.isPresent()) {
            HandleMaterialStats handle = handleOptional.get();
            drawTraits(stack, recipe, HandleMaterialStats.ID, lineNumber);
            font.drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.handle")), 0, lineNumber++, MATERIAL_COLOR);
            drawStats(stack, handle.getLocalizedInfo().get(0), lineNumber++, getMultiplierColor(handle.durability()));
            drawStats(stack, handle.getLocalizedInfo().get(1), lineNumber++, getMultiplierColor(handle.attackDamage()));
            drawStats(stack, handle.getLocalizedInfo().get(2), lineNumber++, getMultiplierColor(handle.durability()));
            drawStats(stack, handle.getLocalizedInfo().get(3), lineNumber++, getMultiplierColor(handle.attackDamage()));
            lineNumber += 0.4f;
        }
        // LIMB
        if (limbOptional.isPresent()) {
            LimbMaterialStats limb = limbOptional.get();
            drawTraits(stack, recipe, LimbMaterialStats.ID, lineNumber);
            font.drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.limb")), 0, lineNumber++, MATERIAL_COLOR);
            drawStats(stack, limb.getLocalizedInfo().get(0), lineNumber++, DURABILITY_COLOR);
            drawStats(stack, limb.getLocalizedInfo().get(1), lineNumber++, getMultiplierColor(limb.drawSpeed()));
            drawStats(stack, limb.getLocalizedInfo().get(2), lineNumber++, getMultiplierColor(limb.velocity()));
            drawStats(stack, limb.getLocalizedInfo().get(3), lineNumber++, getMultiplierColor(limb.accuracy()));
            lineNumber += 0.4f;
        }
        // GRIP
        if (gripOptional.isPresent()) {
            GripMaterialStats grip = gripOptional.get();
            drawTraits(stack, recipe, GripMaterialStats.ID, lineNumber);
            font.drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.grip")), 0, lineNumber++, MATERIAL_COLOR);
            drawStats(stack, grip.getLocalizedInfo().get(0), lineNumber++, getMultiplierColor(grip.durability()));
            drawStats(stack, grip.getLocalizedInfo().get(1), lineNumber++, getMultiplierColor(grip.accuracy()));
            drawStats(stack, grip.getLocalizedInfo().get(2), lineNumber++, ATTACK_COLOR);
            lineNumber += 0.4f;
        }
        // STRING
        else if (stringOptional.isPresent()) {
            drawTraits(stack, recipe, StatlessMaterialStats.BOWSTRING.getIdentifier(), lineNumber);
            font.drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.bowstring")), 0, lineNumber, MATERIAL_COLOR);
        }

    }

    @Nonnull
    @Override
    public List<Component> getTooltipStrings(MaterialStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String materialNamespace = recipe.getMaterialId().getNamespace();
        final String materialPath = recipe.getMaterialId().getPath();

        // TRAIT
        int matWidth = font.width(materialPath);
        if (inBox(mouseX, mouseY, (WIDTH - matWidth) / 2f, 0.4f * LINE_HEIGHT - 1, matWidth, LINE_HEIGHT)) {
            return List.of(MutableComponent.create(new TranslatableContents(String.format("material.%s.%s.flavor", materialNamespace, materialPath))).withStyle(ChatFormatting.ITALIC));
        }

        Optional<HeadMaterialStats> headOptional = recipe.getStats(HeadMaterialStats.ID);
        Optional<IMaterialStats> extraOptional = recipe.getStats(StatlessMaterialStats.BINDING.getIdentifier());
        Optional<HandleMaterialStats> handleOptional = recipe.getStats(HandleMaterialStats.ID);
        Optional<LimbMaterialStats> limbOptional = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = recipe.getStats(GripMaterialStats.ID);
        Optional<IMaterialStats> stringOptional = recipe.getStats(StatlessMaterialStats.BOWSTRING.getIdentifier());

        float lineNumber = 2f;
        // HEAD
        if (headOptional.isPresent()) {
            Optional<List<Component>> component = Stream.of(
                            getTraitTooltips(recipe, HeadMaterialStats.ID, mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.harvest_tier", mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.mining_speed", mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.attack_damage", mouseX, mouseY, lineNumber++))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
            lineNumber += 0.4f;
        }
        // EXTRA
        // only draw extra if others don't exist
        else if (extraOptional.isPresent()){
            List<Component> component = getTraitTooltips(recipe, StatlessMaterialStats.BINDING.getIdentifier(), mouseX, mouseY, lineNumber++);
            if (!component.isEmpty()) {
                return component;
            }
            lineNumber += 0.4f;
        }
        // HANDLE
        if (handleOptional.isPresent()) {
            Optional<List<Component>> component = Stream.of(
                            getTraitTooltips(recipe, HandleMaterialStats.ID, mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.attack_damage", mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.attack_speed", mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.mining_speed", mouseX, mouseY, lineNumber++))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
            lineNumber += 0.4f;
        }
        // LIMB
        if (limbOptional.isPresent()) {
            Optional<List<Component>> component = Stream.of(
                            getTraitTooltips(recipe, LimbMaterialStats.ID, mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                            getStatTooltip( "tool_stat.tconstruct.draw_speed", mouseX, mouseY, lineNumber++),
                            getStatTooltip( "tool_stat.tconstruct.velocity", mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.accuracy", mouseX, mouseY, lineNumber++))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
            lineNumber += 0.4f;
        }
        // GRIP
        if (gripOptional.isPresent()) {
            Optional<List<Component>> component = Stream.of(
                            getTraitTooltips(recipe, GripMaterialStats.ID, mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.accuracy", mouseX, mouseY, lineNumber++),
                            getStatTooltip( "tool_stat.tconstruct.attack_damage", mouseX, mouseY, lineNumber++))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
            lineNumber += 0.4f;
        }
        // STRING
        else if (stringOptional.isPresent()) {
            List<Component> component = getTraitTooltips(recipe, StatlessMaterialStats.BOWSTRING.getIdentifier(), mouseX, mouseY, lineNumber);
            if (!component.isEmpty()) {
                return component;
            }
        }

        return Collections.emptyList();
    }

    private void drawStats(PoseStack poseStack, Component component, float lineNumber, int ACCENT_COLOR) {
        String[] list = component.getString().split(":");
        list[0] += ":";
        float width = font.getSplitter().stringWidth(list[0]);
        font.draw(poseStack, list[0], 0, lineNumber * LINE_HEIGHT, TEXT_COLOR);
        font.draw(poseStack, list[1], width, lineNumber * LINE_HEIGHT, ACCENT_COLOR);
    }

    private void drawStatsShadow(PoseStack poseStack, Component component, float lineNumber, int ACCENT_COLOR) {
        String[] list = component.getString().split(":");
        list[0] += ":";
        float width = font.getSplitter().stringWidth(list[0]);
        font.draw(poseStack, list[0], 0, lineNumber * LINE_HEIGHT, TEXT_COLOR);
        drawShadow(poseStack, list[1], width, lineNumber, ACCENT_COLOR);
    }

    private void drawTraits(PoseStack poseStack, MaterialStatsWrapper statsWrapper, MaterialStatsId statsId, float lineNumber) {
        for (ModifierEntry trait : statsWrapper.getTraits(statsId)) {
            String pattern = getPattern(Util.makeTranslationKey("modifier", trait.getId()));
            int traitColor = ResourceColorManager.getColor(Util.makeTranslationKey("modifier", trait.getId()));
            drawShadow(poseStack, pattern, WIDTH - font.getSplitter().stringWidth(pattern), lineNumber++, traitColor);
        }
    }

    private void drawShadow(PoseStack poseStack, String string, float x, float lineNumber, int color) {
        font.draw(poseStack, string, x + 1f, lineNumber * LINE_HEIGHT + 1f, getShade(color, 6));
        font.draw(poseStack, string, x, lineNumber * LINE_HEIGHT, color);
    }

    private List<Component> getStatTooltip(String pattern, double mouseX, double mouseY, float lineNumber) {
        String string = getPattern(pattern);
        int textWidth = font.width(string);
        if (inBox(mouseX, mouseY, 0, lineNumber * LINE_HEIGHT - 1, textWidth, LINE_HEIGHT)) {
            return List.of(MutableComponent.create(new LiteralContents(pattern + ".description")));
        }
        return Collections.emptyList();
    }

    private List<Component> getTraitTooltips(MaterialStatsWrapper statsWrapper, MaterialStatsId statsId, double mouseX, double mouseY, float lineNumber) {
        for (ModifierEntry trait : statsWrapper.getTraits(statsId)) {
            String namespace = trait.getId().getNamespace();
            String path = trait.getId().getPath();
            String pattern = getPattern(Util.makeTranslationKey("modifier", trait.getId()));
            int textWidth = font.width(pattern);
            if (inBox(mouseX, mouseY, WIDTH - textWidth, lineNumber++ * LINE_HEIGHT - 1, textWidth, LINE_HEIGHT)) {
                return List.of(MutableComponent.create(new TranslatableContents(String.format("modifier.%s.%s.flavor", namespace, path))).setStyle(Style.EMPTY.withItalic(true).withColor(WHITE)),
                        MutableComponent.create(new TranslatableContents((String.format("modifier.%s.%s.description", namespace, path)))));
            }
        }
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Nonnull
    @Override
    public RecipeType<MaterialStatsWrapper> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.BACKGROUND;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.ICON;
    }

}
