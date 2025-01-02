package me.paypur.tconjei.jei;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

public class HarvestStatsCategory extends AbstractMaterialStatsCategory {

    public HarvestStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 0, 0, 16, 16);
        this.title = Component.translatable("tconjei.tool_stats.harvest");
        this.recipeType = RecipeType.create(MOD_ID, "harvest_stats", MaterialStatsWrapper.class);
        this.tag = TinkerTags.Items.HARVEST;
    }

    @Override
    public void draw(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        super.draw(wrapper, recipeSlotsView, gui, mouseX, mouseY);

        final int color = MaterialTooltipCache.getColor(wrapper.getMaterialId()).getValue();
        float lineNumber = 2f;

        Optional<HeadMaterialStats> headOptional = wrapper.getStats(HeadMaterialStats.ID);
        Optional<StatlessMaterialStats> bindingOptional = wrapper.getStats(StatlessMaterialStats.BINDING.getIdentifier());
        Optional<HandleMaterialStats> handleOptional = wrapper.getStats(HandleMaterialStats.ID);

        // TRAITS
        Optional<? extends IMaterialStats> statOptional = Stream.of(headOptional, bindingOptional, handleOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            drawTraits(gui, wrapper.getTraits(statOptional.get().getIdentifier()), lineNumber);
        }

        // HEAD
        if (headOptional.isPresent()) {
            HeadMaterialStats head = headOptional.get();
            drawComponent(gui, head.getLocalizedName().withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);
            drawStatComponent(gui, head.getLocalizedInfo().get(0), lineNumber++); // durability
            drawStatComponent(gui, head.getLocalizedInfo().get(1), lineNumber++); // mining tier
            drawStatComponent(gui, head.getLocalizedInfo().get(2), lineNumber++); // mining speed
            drawStatComponent(gui, head.getLocalizedInfo().get(3), lineNumber++); // melee damage
            lineNumber += LINE_SPACING;
        }

        // BINDING
        if (bindingOptional.isPresent()) {
            StatlessMaterialStats binding = bindingOptional.get();
            drawComponent(gui, binding.getLocalizedName().withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);
            drawComponent(gui, binding.getLocalizedInfo().get(0), 0, lineNumber++, TEXT_COLOR, false);
            lineNumber += LINE_SPACING;
        }

        // HANDLE
        if (handleOptional.isPresent()) {
            HandleMaterialStats handle = handleOptional.get();
            drawComponent(gui, handle.getLocalizedName().withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);
            drawStatComponent(gui, handle.getLocalizedInfo().get(0), lineNumber++); // durability
            drawStatComponent(gui, handle.getLocalizedInfo().get(1), lineNumber++); // melee damage
            drawStatComponent(gui, handle.getLocalizedInfo().get(2), lineNumber++); // melee speed
            drawStatComponent(gui, handle.getLocalizedInfo().get(3), lineNumber);   // mining speed
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

        Optional<HeadMaterialStats> headOptional = wrapper.getStats(HeadMaterialStats.ID);
        Optional<IMaterialStats> bindingOptional = wrapper.getStats(StatlessMaterialStats.BINDING.getIdentifier());
        Optional<HandleMaterialStats> handleOptional =  wrapper.getStats(HandleMaterialStats.ID);

        // TRAIT
        Optional<? extends IMaterialStats> statOptional = Stream.of(handleOptional, bindingOptional, handleOptional)
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

        // HEAD
        if (headOptional.isPresent()) {
            lineNumber++;
            HeadMaterialStats head = headOptional.get();
            Optional<List<Component>> component = Stream.of(
                    getStatTooltip(head, 0, mouseX, mouseY, lineNumber++),
                    getStatTooltip(head, 1, mouseX, mouseY, lineNumber++),
                    getStatTooltip(head, 2, mouseX, mouseY, lineNumber++),
                    getStatTooltip(head, 3, mouseX, mouseY, lineNumber++))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                tooltip.addAll(component.get());
                return;
            }
            lineNumber += LINE_SPACING;
        }

        // EXTRA
        if (bindingOptional.isPresent()) {
            lineNumber += LINE_SPACING + 2;
        }

        // HANDLE
        if (handleOptional.isPresent()) {
            lineNumber++;
            HandleMaterialStats handle = handleOptional.get();
            Optional<List<Component>> component = Stream.of(
                    getStatTooltip(handle, 0,  mouseX, mouseY, lineNumber++),
                    getStatTooltip(handle, 1,  mouseX, mouseY, lineNumber++),
                    getStatTooltip(handle, 2,  mouseX, mouseY, lineNumber++),
                    getStatTooltip(handle, 3,  mouseX, mouseY, lineNumber))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                tooltip.addAll(component.get());
                return;
            }
        }
    }
}
