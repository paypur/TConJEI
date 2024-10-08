package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeI18n;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.tools.stats.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.paypur.tconjei.ColorManager.*;
import static me.paypur.tconjei.TConJEI.MOD_ID;

public class RangedStatsCategory extends AbstractMaterialStatsCategory {

    public RangedStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 16, 0, 16, 16);
        this.title = Component.translatable("tconjei.tool_stats.ranged");
        this.recipeType = RecipeType.create(MOD_ID, "ranged_stats", MaterialStatsWrapper.class);
        this.tag = TinkerTags.Items.RANGED;
    }

    @Override
    public void draw(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        super.draw(wrapper, recipeSlotsView, stack, mouseX, mouseY);

        final int color = MaterialTooltipCache.getColor(wrapper.getMaterialId()).getValue();
        float lineNumber = 2f;

        Optional<LimbMaterialStats> limbOptional = wrapper.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = wrapper.getStats(GripMaterialStats.ID);
        Optional<StatlessMaterialStats> stringOptional = wrapper.getStats(StatlessMaterialStats.BOWSTRING.getIdentifier());

        // TRAITS
        Optional<? extends IMaterialStats> statOptional = Stream.of(limbOptional, gripOptional, stringOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            drawTraits(stack, wrapper.getTraits(statOptional.get().getIdentifier()), lineNumber);
        }

        // LIMB
        if (limbOptional.isPresent()) {
            LimbMaterialStats limb = limbOptional.get();
            drawStringShadow(stack, String.format("[%s]", limb.getLocalizedName().getString()), 0, lineNumber++, color);
            drawStatComponentShadow(stack, limb.getLocalizedInfo().get(0), lineNumber++);
            drawStatComponentShadow(stack, limb.getLocalizedInfo().get(1), lineNumber++);
            drawStatComponentShadow(stack, limb.getLocalizedInfo().get(2), lineNumber++);
            drawStatComponentShadow(stack, limb.getLocalizedInfo().get(3), lineNumber++);
            lineNumber += LINE_SPACING;
        }

        // GRIP
        if (gripOptional.isPresent()) {
            GripMaterialStats grip = gripOptional.get();
            drawStringShadow(stack, String.format("[%s]", grip.getLocalizedName().getString()), 0, lineNumber++, color);
            drawStatComponentShadow(stack, grip.getLocalizedInfo().get(0), lineNumber++);
            drawStatComponentShadow(stack, grip.getLocalizedInfo().get(1), lineNumber++);
            drawStatComponentShadow(stack, grip.getLocalizedInfo().get(2), lineNumber++);
            lineNumber += LINE_SPACING;
        }

        // STRING
        if (stringOptional.isPresent()) {
            StatlessMaterialStats string = stringOptional.get();
            drawStringShadow(stack, String.format("[%s]", string.getLocalizedName().getString()), 0, lineNumber++, color);
            drawString(stack, ForgeI18n.getPattern("tool_stat.tconstruct.extra.no_stats"), 0, lineNumber, TEXT_COLOR);
        }
    }

    @Nonnull
    @Override
    public List<Component> getTooltipStrings(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // MATERIAL
        List<Component> material = super.getTooltipStrings(wrapper, recipeSlotsView, mouseX, mouseY);
        if (!material.isEmpty()) {
            return material;
        }

        float lineNumber = 2f;

        Optional<LimbMaterialStats> limbOptional = wrapper.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = wrapper.getStats(GripMaterialStats.ID);
        Optional<IMaterialStats> stringOptional = wrapper.getStats(StatlessMaterialStats.BOWSTRING.getIdentifier());

        // TRAIT
        Optional<? extends IMaterialStats> statOptional = Stream.of(limbOptional, gripOptional, stringOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            List<Component> tooltips = getTraitTooltips(wrapper.getTraits(statOptional.get().getIdentifier()), mouseX, mouseY, lineNumber);
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
