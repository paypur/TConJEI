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

public class HarvestStatsCategory extends AbstractMaterialStatsCategory {

    public HarvestStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 0, 0, 16, 16);
        this.title = Component.translatable("tconjei.tool_stats.harvest");
        this.recipeType = RecipeType.create(MOD_ID, "harvest_stats", MaterialStatsWrapper.class);
        this.tag = TinkerTags.Items.HARVEST;
    }

    @Override
    public void draw(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        super.draw(wrapper, recipeSlotsView, stack, mouseX, mouseY);

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
            drawTraits(stack, wrapper.getTraits(statOptional.get().getIdentifier()), lineNumber);
        }

        // HEAD
        if (headOptional.isPresent()) {
            HeadMaterialStats head = headOptional.get();
            drawShadow(stack, String.format("[%s]", head.getLocalizedName().getString()), 0, lineNumber++, color);
            drawStatsShadow(stack, head.getLocalizedInfo().get(0), lineNumber++, DURABILITY_COLOR);
            drawStatsShadow(stack, head.getLocalizedInfo().get(1), lineNumber++, head.getLocalizedInfo().get(1).getSiblings().get(0).getStyle().getColor().getValue());
            drawStatsShadow(stack, head.getLocalizedInfo().get(2), lineNumber++, MINING_COLOR);
            drawStatsShadow(stack, head.getLocalizedInfo().get(3), lineNumber++, ATTACK_COLOR);
            lineNumber += LINE_SPACING;
        }

        // BINDING
        if (bindingOptional.isPresent()) {
            StatlessMaterialStats binding = bindingOptional.get();
            drawShadow(stack, String.format("[%s]", binding.getLocalizedName().getString()), 0, lineNumber++, color);
            drawString(stack, ForgeI18n.getPattern("tool_stat.tconstruct.extra.no_stats"), 0, lineNumber++, TEXT_COLOR);
            lineNumber += LINE_SPACING;
        }

        // HANDLE
        if (handleOptional.isPresent()) {
            HandleMaterialStats handle = handleOptional.get();
            drawShadow(stack, String.format("[%s]", handle.getLocalizedName().getString()), 0, lineNumber++, color);
            drawStatsShadow(stack, handle.getLocalizedInfo().get(0), lineNumber++, getMultiplierColor(handle.durability()));
            drawStatsShadow(stack, handle.getLocalizedInfo().get(1), lineNumber++, getMultiplierColor(handle.attackDamage()));
            drawStatsShadow(stack, handle.getLocalizedInfo().get(2), lineNumber++, getMultiplierColor(handle.durability()));
            drawStatsShadow(stack, handle.getLocalizedInfo().get(3), lineNumber, getMultiplierColor(handle.attackDamage()));
        }
    }

    @Nonnull
    @Override
    public List<Component> getTooltipStrings(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        float lineNumber = 2f;

        Optional<HeadMaterialStats> headOptional = wrapper.getStats(HeadMaterialStats.ID);
        Optional<IMaterialStats> bindingOptional = wrapper.getStats(StatlessMaterialStats.BINDING.getIdentifier());
        Optional<HandleMaterialStats> handleOptional =  wrapper.getStats(HandleMaterialStats.ID);

        // MATERIAL
        List<Component> material = super.getTooltipStrings(wrapper, recipeSlotsView, mouseX, mouseY);
        if (!material.isEmpty()) {
            return material;
        }

        // TRAIT
        Optional<? extends IMaterialStats> statOptional = Stream.of(handleOptional, bindingOptional, handleOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            List<Component> tooltips = getTraitTooltips(wrapper.getTraits(statOptional.get().getIdentifier()), mouseX, mouseY, lineNumber);
            if (!tooltips.isEmpty()) {
                return tooltips;
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
                return component.get();
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
                return component.get();
            }
        }

        return List.of();
    }
}
