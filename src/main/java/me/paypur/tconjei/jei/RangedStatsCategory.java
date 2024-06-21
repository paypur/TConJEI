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
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
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
        super(guiHelper, TinkerTags.Items.RANGED);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 16, 0, 16, 16);
        this.title = MutableComponent.create(new LiteralContents("Ranged Stats"));
        this.recipeType = RecipeType.create(MOD_ID, "ranged_stats", ToolStatsWrapper.class);
        this.uid = new ResourceLocation(MOD_ID, "ranged_stats");
    }

    @Override
    public void draw(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        final String MATERIAL_NAME = getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(recipe.getMaterialId()).getValue();
        float lineNumber = 2f;

        Optional<LimbMaterialStats> limbOptional = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = recipe.getStats(GripMaterialStats.ID);
        Optional<IMaterialStats> stringOptional = recipe.getStats(StatlessMaterialStats.BOWSTRING.getIdentifier());

        // MATERIAL
        drawShadow(stack, MATERIAL_NAME, (WIDTH - FONT.width(MATERIAL_NAME)) / 2f, LINE_SPACING, MATERIAL_COLOR);

        // TRAITS
        Optional<List<ModifierEntry>> traits = Stream.of(limbOptional, gripOptional, stringOptional)
                .filter(Optional::isPresent)
                .findFirst()
                .map(stat -> recipe.getTraits(stat.get().getIdentifier()));
        traits.ifPresent(modifierEntries -> drawTraits(stack, modifierEntries, lineNumber));

        // LIMB
        if (limbOptional.isPresent()) {
            LimbMaterialStats limb = limbOptional.get();
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.limb")), 0, lineNumber++, MATERIAL_COLOR);
            drawStatsShadow(stack, limb.getLocalizedInfo().get(0), lineNumber++, DURABILITY_COLOR);
            drawStatsShadow(stack, limb.getLocalizedInfo().get(1), lineNumber++, getMultiplierColor(limb.drawSpeed()));
            drawStatsShadow(stack, limb.getLocalizedInfo().get(2), lineNumber++, getMultiplierColor(limb.velocity()));
            drawStatsShadow(stack, limb.getLocalizedInfo().get(3), lineNumber++, getMultiplierColor(limb.accuracy()));
            lineNumber += LINE_SPACING;
        }

        // GRIP
        if (gripOptional.isPresent()) {
            GripMaterialStats grip = gripOptional.get();
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.grip")), 0, lineNumber++, MATERIAL_COLOR);
            drawStatsShadow(stack, grip.getLocalizedInfo().get(0), lineNumber++, getMultiplierColor(grip.durability()));
            drawStatsShadow(stack, grip.getLocalizedInfo().get(1), lineNumber++, getMultiplierColor(grip.accuracy()));
            drawStatsShadow(stack, grip.getLocalizedInfo().get(2), lineNumber++, ATTACK_COLOR);
            lineNumber += LINE_SPACING;
        }

        // STRING
        if (stringOptional.isPresent()) {
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.bowstring")), 0, lineNumber, MATERIAL_COLOR);
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
        List<ModifierEntry> traits = Stream.of(limbOptional, gripOptional, stringOptional)
                .filter(Optional::isPresent)
                .findFirst()
                .map(stat -> recipe.getTraits(stat.get().getIdentifier()))
                .get();
        List<Component> traitComponents = getTraitTooltips(traits, mouseX, mouseY, lineNumber);
        if (!traitComponents.isEmpty()) {
            return traitComponents;
        }

        // LIMB
        if (limbOptional.isPresent()) {
            lineNumber++;
            Optional<List<Component>> component = Stream.of(
                            getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                            getStatTooltip( "tool_stat.tconstruct.draw_speed", mouseX, mouseY, lineNumber++),
                            getStatTooltip( "tool_stat.tconstruct.velocity", mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.accuracy", mouseX, mouseY, lineNumber++))
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
            Optional<List<Component>> component = Stream.of(
                            getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                            getStatTooltip("tool_stat.tconstruct.accuracy", mouseX, mouseY, lineNumber++),
                            getStatTooltip( "tool_stat.tconstruct.attack_damage", mouseX, mouseY, lineNumber))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
        }

        return List.of();
    }

}
