package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.stats.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.paypur.tconjei.ColorManager.*;
import static me.paypur.tconjei.TConJEI.MOD_ID;
import static net.minecraftforge.common.ForgeI18n.getPattern;

public class RangedStatsCategory extends AbstractToolStatsCategory {

    public RangedStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 16, 0, 16, 16);
        title = MutableComponent.create(new LiteralContents("Ranged Stats"));
        recipeType = RecipeType.create(MOD_ID, "ranged_stats", ToolStatsWrapper.class);
        tag = TinkerTags.Items.RANGED;
    }

    @Override
    public void draw(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        final String MATERIAL_NAME = getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(recipe.getMaterialId()).getValue();
        float lineNumber = 2f;

        Optional<LimbMaterialStats> limbOptional = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = recipe.getStats(GripMaterialStats.ID);
        Optional<StatlessMaterialStats> stringOptional = recipe.getStats(StatlessMaterialStats.BOWSTRING.getIdentifier());

        // MATERIAL
        drawShadow(stack, MATERIAL_NAME, (WIDTH - FONT.width(MATERIAL_NAME)) / 2, LINE_SPACING, MATERIAL_COLOR);

        // TRAITS
        Optional<? extends IMaterialStats> statOptional = Stream.of(limbOptional, gripOptional, stringOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            drawTraits(stack, recipe.getTraits(statOptional.get().getIdentifier()), lineNumber);
        }

        // LIMB
        if (limbOptional.isPresent()) {
            LimbMaterialStats limb = limbOptional.get();
            drawShadow(stack, String.format("[%s]", limb.getLocalizedName().getString()), 0, lineNumber++, MATERIAL_COLOR);
            drawStatsShadow(stack, limb.getLocalizedInfo().get(0), lineNumber++, DURABILITY_COLOR);
            drawStatsShadow(stack, limb.getLocalizedInfo().get(1), lineNumber++, getMultiplierColor(limb.drawSpeed()));
            drawStatsShadow(stack, limb.getLocalizedInfo().get(2), lineNumber++, getMultiplierColor(limb.velocity()));
            drawStatsShadow(stack, limb.getLocalizedInfo().get(3), lineNumber++, getMultiplierColor(limb.accuracy()));
            lineNumber += LINE_SPACING;
        }

        // GRIP
        if (gripOptional.isPresent()) {
            GripMaterialStats grip = gripOptional.get();
            drawShadow(stack, String.format("[%s]", grip.getLocalizedName().getString()), 0, lineNumber++, MATERIAL_COLOR);
            drawStatsShadow(stack, grip.getLocalizedInfo().get(0), lineNumber++, getMultiplierColor(grip.durability()));
            drawStatsShadow(stack, grip.getLocalizedInfo().get(1), lineNumber++, getMultiplierColor(grip.accuracy()));
            drawStatsShadow(stack, grip.getLocalizedInfo().get(2), lineNumber++, ATTACK_COLOR);
            lineNumber += LINE_SPACING;
        }

        // STRING
        if (stringOptional.isPresent()) {
            StatlessMaterialStats string = stringOptional.get();
            drawShadow(stack, String.format("[%s]", string.getLocalizedName().getString()), 0, lineNumber, MATERIAL_COLOR);
        }
    }

    @Nonnull
    @Override
    public List<Component> getTooltipStrings(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        float lineNumber = 2f;

        Optional<LimbMaterialStats> limbOptional = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = recipe.getStats(GripMaterialStats.ID);
        Optional<IMaterialStats> stringOptional = recipe.getStats(StatlessMaterialStats.BOWSTRING.getIdentifier());

        // MATERIAL
        List<Component> material = super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
        if (!material.isEmpty()) {
            return material;
        }

        // TRAIT
        Optional<? extends IMaterialStats> statOptional = Stream.of(limbOptional, gripOptional, stringOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            List<Component> tooltips = getTraitTooltips(recipe.getTraits(statOptional.get().getIdentifier()), mouseX, mouseY, lineNumber);
            if (!tooltips.isEmpty()) {
                return tooltips;
            }
        }

        // LIMB
        if (limbOptional.isPresent()) {
            lineNumber++;
            LimbMaterialStats limb = limbOptional.get();
            Optional<List<Component>> component = Stream.of(
                            getStatTooltip(limb, 0, mouseX, mouseY, lineNumber++),
                            getStatTooltip(limb, 1, mouseX, mouseY, lineNumber++),
                            getStatTooltip(limb, 2, mouseX, mouseY, lineNumber++),
                            getStatTooltip(limb, 3, mouseX, mouseY, lineNumber++))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
            lineNumber += LINE_SPACING;
        }

        // GRIP
        if (gripOptional.isPresent()) {
            lineNumber++;
            GripMaterialStats grip = gripOptional.get();
            Optional<List<Component>> component = Stream.of(
                            getStatTooltip(grip, 0, mouseX, mouseY, lineNumber++),
                            getStatTooltip(grip, 1, mouseX, mouseY, lineNumber++),
                            getStatTooltip(grip, 2, mouseX, mouseY, lineNumber))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
        }

        return List.of();
    }

}
