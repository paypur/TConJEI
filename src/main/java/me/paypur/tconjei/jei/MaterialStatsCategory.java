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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.stats.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.paypur.tconjei.ColorManager.*;
import static me.paypur.tconjei.TConJEI.MOD_ID;
import static me.paypur.tconjei.TConJEI.inBox;
import static net.minecraftforge.common.ForgeI18n.getPattern;

public class MaterialStatsCategory implements IRecipeCategory<MaterialStatsWrapper> {

    final ResourceLocation UID = new ResourceLocation(MOD_ID, "material_stats");
    final Font font = Minecraft.getInstance().font;
    final IDrawable BACKGROUND, ICON;
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
        drawShadow(stack, materialName, (WIDTH - font.width(materialName)) / 2f, 0.4f, MATERIAL_COLOR);
        Optional<HeadMaterialStats> headOptional = recipe.getStats(HeadMaterialStats.ID);
        Optional<ExtraMaterialStats> extraOptional = recipe.getStats(ExtraMaterialStats.ID);
        Optional<HandleMaterialStats> handleOptional = recipe.getStats(HandleMaterialStats.ID);
        Optional<LimbMaterialStats> limbOptional = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = recipe.getStats(GripMaterialStats.ID);
        Optional<BowstringMaterialStats> stringOptional = recipe.getStats(BowstringMaterialStats.ID);
        float lineNumber = 2f;
        // HEAD
        if (headOptional.isPresent()) {
            HeadMaterialStats headStats = headOptional.get();
            ResourceLocation miningLevel = headStats.getTierId();
            drawTraits(stack, recipe.getTraits(HeadMaterialStats.ID), lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.head")), 0, lineNumber++, MATERIAL_COLOR);
            drawStats(stack, "tool_stat.tconstruct.durability", String.valueOf(headOptional.get().getDurability()), lineNumber++, DURABILITY_COLOR);
            drawStatsShadow(stack, "tool_stat.tconstruct.harvest_tier", getPattern(Util.makeTranslationKey("stat.tconstruct.harvest_tier", miningLevel)), lineNumber++, getMiningLevelColor(miningLevel));
            drawStats(stack, "tool_stat.tconstruct.mining_speed", String.format("%.2f", headOptional.get().getMiningSpeed()), lineNumber++, MINING_COLOR);
            drawStats(stack, "tool_stat.tconstruct.attack_damage", String.format("%.2f", headOptional.get().getAttack()), lineNumber++, ATTACK_COLOR);
            lineNumber += 0.4f;
        }
        // EXTRA
        // only draw extra if others don't exist
        else if (extraOptional.isPresent()) {
            drawTraits(stack, recipe.getTraits(ExtraMaterialStats.ID), lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.extra")), 0, lineNumber++, MATERIAL_COLOR);
            lineNumber += 0.4f;
        }
        // HANDLE
        if (handleOptional.isPresent()) {
            HandleMaterialStats handleStats = handleOptional.get();
            drawTraits(stack, recipe.getTraits(HandleMaterialStats.ID), lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.handle")), 0, lineNumber++, MATERIAL_COLOR);
            drawStats(stack, "tool_stat.tconstruct.durability", String.format("%.2fx", handleStats.getDurability()), lineNumber++, getMultiplierColor(handleStats.getDurability()));
            drawStats(stack, "tool_stat.tconstruct.attack_damage", String.format("%.2fx", handleStats.getAttackDamage()), lineNumber++, getMultiplierColor(handleStats.getAttackDamage()));
            drawStats(stack, "tool_stat.tconstruct.attack_speed", String.format("%.2fx", handleStats.getAttackSpeed()), lineNumber++, getMultiplierColor(handleStats.getAttackSpeed()));
            drawStats(stack, "tool_stat.tconstruct.mining_speed", String.format("%.2fx", handleStats.getMiningSpeed()), lineNumber++, getMultiplierColor(handleStats.getMiningSpeed()));
            lineNumber += 0.4f;
        }
        // LIMB
        if (limbOptional.isPresent()) {
            LimbMaterialStats limbStats = limbOptional.get();
            drawTraits(stack, recipe.getTraits(LimbMaterialStats.ID), lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.limb")), 0, lineNumber++, MATERIAL_COLOR);
            drawStats(stack, "tool_stat.tconstruct.durability", String.valueOf(limbStats.getDurability()), lineNumber++, DURABILITY_COLOR);
            drawStats(stack, "tool_stat.tconstruct.draw_speed", signedString(limbStats.getDrawSpeed()), lineNumber++, getDifferenceColor(limbStats.getDrawSpeed()));
            drawStats(stack, "tool_stat.tconstruct.velocity", signedString(limbStats.getVelocity()), lineNumber++, getDifferenceColor(limbStats.getVelocity()));
            drawStats(stack, "tool_stat.tconstruct.accuracy", signedString(limbStats.getAccuracy()), lineNumber++, getDifferenceColor(limbStats.getAccuracy()));
            lineNumber += 0.4f;
        }
        // GRIP
        if (gripOptional.isPresent()) {
            GripMaterialStats gripStats = gripOptional.get();
            drawTraits(stack, recipe.getTraits(GripMaterialStats.ID), lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.grip")), 0, lineNumber++, MATERIAL_COLOR);
            drawStats(stack, "tool_stat.tconstruct.durability", String.format("%.2fx", gripStats.getDurability()), lineNumber++, getMultiplierColor(gripStats.getDurability()));
            drawStats(stack, "tool_stat.tconstruct.accuracy", signedString(gripStats.getAccuracy()), lineNumber++, getDifferenceColor(gripStats.getAccuracy()));
            drawStats(stack, "tool_stat.tconstruct.attack_damage", String.format("%.2f", gripStats.getMeleeAttack()), lineNumber++, ATTACK_COLOR);
            lineNumber += 0.4f;
        }
        // STRING
        else if (stringOptional.isPresent()) {
            drawTraits(stack, recipe.getTraits(BowstringMaterialStats.ID), lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.bowstring")), 0, lineNumber, MATERIAL_COLOR);
        }

    }

    @Override
    public List<Component> getTooltipStrings(MaterialStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String materialNamespace = recipe.getMaterialId().getNamespace();
        final String materialPath = recipe.getMaterialId().getPath();
        // TRAIT
        int matWidth = font.width(materialPath);
        if (inBox(mouseX, mouseY, (WIDTH - matWidth) / 2f, 0.4f * LINE_HEIGHT - 1, matWidth, LINE_HEIGHT)) {
            return List.of(new TranslatableComponent(String.format("material.%s.%s.flavor", materialNamespace, materialPath)).withStyle(ChatFormatting.ITALIC));
        }
        float lineNumber = 2f;
        Optional<HeadMaterialStats> headOptional = recipe.getStats(HeadMaterialStats.ID);
        Optional<ExtraMaterialStats> extraOptional = recipe.getStats(ExtraMaterialStats.ID);
        Optional<HandleMaterialStats> handleOptional =  recipe.getStats(HandleMaterialStats.ID);
        Optional<LimbMaterialStats> limbOptional = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = recipe.getStats(GripMaterialStats.ID);
        Optional<BowstringMaterialStats> stringOptional = recipe.getStats(BowstringMaterialStats.ID);
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
        else if (extraOptional.isPresent()) {
            List<Component> component = getTraitTooltips(recipe, ExtraMaterialStats.ID, mouseX, mouseY, lineNumber++);
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
            List<Component> component = getTraitTooltips(recipe, BowstringMaterialStats.ID, mouseX, mouseY, lineNumber);
            if (!component.isEmpty()) {
                return component;
            }
        }

        return Collections.emptyList();
    }

    private void drawStats(PoseStack poseStack, String type, String stat, float lineNumber, int ACCENT_COLOR) {
        String pattern = getPattern(type);
        float width = font.getSplitter().stringWidth(pattern);
        font.draw(poseStack, pattern, 0, lineNumber * LINE_HEIGHT, TEXT_COLOR);
        font.draw(poseStack, stat, width, lineNumber * LINE_HEIGHT, ACCENT_COLOR);
    }

    private void drawStatsShadow(PoseStack poseStack, String type, String stat, float lineNumber, int ACCENT_COLOR) {
        String pattern = getPattern(type);
        float width = font.getSplitter().stringWidth(pattern);
        font.draw(poseStack, pattern, 0, lineNumber * LINE_HEIGHT, TEXT_COLOR);
        drawShadow(poseStack, stat, width, lineNumber, ACCENT_COLOR);
    }

    private void drawTraits(PoseStack poseStack, List<ModifierEntry> traits, float lineNumber) {
        for (ModifierEntry trait : traits) {
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
            return List.of(new TranslatableComponent(pattern + ".description"));
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
                return List.of(new TranslatableComponent(String.format("modifier.%s.%s.flavor", namespace, path)).withStyle(ChatFormatting.ITALIC),
                        new TranslatableComponent(String.format("modifier.%s.%s.description", namespace, path)));
            }
        }
        return Collections.emptyList();
    }

    private String signedString(float f) {
        return String.format("%s%.2f", f >= 0 ? "+" : "", f);
    }

    @Override
    public Component getTitle() {
        return new TextComponent("Material Stats");
    }

    @Override
    public RecipeType<MaterialStatsWrapper> getRecipeType() {
        return RecipeType.create(MOD_ID, "material_stats", MaterialStatsWrapper.class);
    }

    @Override
    public IDrawable getBackground() {
        return this.BACKGROUND;
    }

    @Override
    public IDrawable getIcon() {
        return this.ICON;
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getUid() {
        return this.UID;
    }

    @SuppressWarnings("removal")
    @Override
    public Class<? extends MaterialStatsWrapper> getRecipeClass() {
        return MaterialStatsWrapper.class;
    }


}
