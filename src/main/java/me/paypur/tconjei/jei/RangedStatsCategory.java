package me.paypur.tconjei.jei;

import me.paypur.tconjei.ColorProvider;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.tools.stats.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.paypur.tconjei.TConJEI.MOD_ID;

public class RangedStatsCategory extends AbstractMaterialStatsCategory {

    public RangedStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 16, 0, 16, 16);
        this.title = Component.translatable("tconjei.tool_stats.ranged");
        this.recipeType = TConJEIPlugin.RANGED_STATS;
        this.tag = TinkerTags.Items.RANGED;
    }

    @Override
    public void draw(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        super.draw(wrapper, recipeSlotsView, gui, mouseX, mouseY);

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
            drawTraits(gui, wrapper.getTraits(statOptional.get().getIdentifier()), lineNumber);
        }

        // LIMB
        if (limbOptional.isPresent()) {
            LimbMaterialStats limb = limbOptional.get();
            drawComponent(gui, limb.getLocalizedName().withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);
            drawStatComponent(gui, limb.getLocalizedInfo().get(0), lineNumber++);
            drawStatComponent(gui, limb.getLocalizedInfo().get(1), lineNumber++);
            drawStatComponent(gui, limb.getLocalizedInfo().get(2), lineNumber++);
            drawStatComponent(gui, limb.getLocalizedInfo().get(3), lineNumber++);
            lineNumber += LINE_SPACING;
        }

        // GRIP
        if (gripOptional.isPresent()) {
            GripMaterialStats grip = gripOptional.get();
            drawComponent(gui, grip.getLocalizedName().withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);
            drawStatComponent(gui, grip.getLocalizedInfo().get(0), lineNumber++);
            drawStatComponent(gui, grip.getLocalizedInfo().get(1), lineNumber++);
            drawStatComponent(gui, grip.getLocalizedInfo().get(2), lineNumber++);
            lineNumber += LINE_SPACING;
        }

        // STRING
        if (stringOptional.isPresent()) {
            StatlessMaterialStats string = stringOptional.get();
            drawComponent(gui, string.getLocalizedName().withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);
            drawComponent(gui, string.getLocalizedInfo().get(0), 0, lineNumber, ColorProvider.TEXT, false);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // MATERIAL
        List<Component> materialTooltips = getMaterialTooltip(wrapper, mouseX, mouseY);
        if (!materialTooltips.isEmpty()) {
            tooltip.addAll(materialTooltips);
            return;
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
            List<Component> traitTooltips = getTraitTooltips(wrapper.getTraits(statOptional.get().getIdentifier()), mouseX, mouseY, lineNumber);
            if (!traitTooltips.isEmpty()) {
                tooltip.addAll(traitTooltips);
                return;
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
                tooltip.addAll(component.get());
                return;
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
                tooltip.addAll(component.get());
                return;
            }
        }
    }

}
