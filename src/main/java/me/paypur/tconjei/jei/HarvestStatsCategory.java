package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeI18n;
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
import static me.paypur.tconjei.Utils.inBox;

public class HarvestStatsCategory extends AbstractToolStatsCategory {

    public HarvestStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 0, 0, 16, 16);
        this.title = new TranslatableComponent("tconjei.toolstats.harvest");
        this.recipeType = RecipeType.create(MOD_ID, "harvest_stats", ToolStatsWrapper.class);
        this.uid = new ResourceLocation(MOD_ID, "harvest_stats");
        this.tag = TinkerTags.Items.HARVEST;
    }

    @Override
    public void draw(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        final String MATERIAL_NAME = ForgeI18n.getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(recipe.getMaterialId()).getValue();
        float lineNumber = 2f;

        Optional<HeadMaterialStats> headOptional = recipe.getStats(HeadMaterialStats.ID);
        Optional<ExtraMaterialStats> extraOptional = recipe.getStats(ExtraMaterialStats.ID);
        Optional<HandleMaterialStats> handleOptional = recipe.getStats(HandleMaterialStats.ID);

        // MATERIAL
        drawShadow(stack, MATERIAL_NAME, (WIDTH - FONT.width(MATERIAL_NAME)) / 2, LINE_SPACING, MATERIAL_COLOR);

        // TRAITS
        Optional<? extends IMaterialStats> statOptional = Stream.of(headOptional, extraOptional, handleOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            drawTraits(stack, recipe.getTraits(statOptional.get().getIdentifier()), lineNumber);
        }

        // HEAD
        if (headOptional.isPresent()) {
            HeadMaterialStats head = headOptional.get();
            drawShadow(stack, String.format("[%s]", head.getLocalizedName().getString()), 0, lineNumber++, MATERIAL_COLOR);
            drawStatsShadow(stack, head.getLocalizedInfo().get(0), lineNumber++, DURABILITY_COLOR);
            drawStatsShadow(stack, head.getLocalizedInfo().get(1), lineNumber++, getMiningLevelColor(head.getTierId()));
            drawStatsShadow(stack, head.getLocalizedInfo().get(2), lineNumber++, MINING_COLOR);
            drawStatsShadow(stack, head.getLocalizedInfo().get(3), lineNumber++, ATTACK_COLOR);
            lineNumber += LINE_SPACING;
        }

        // EXTRA
        if (extraOptional.isPresent()) {
            ExtraMaterialStats extra = extraOptional.get();
            drawShadow(stack, String.format("[%s]", extra.getLocalizedName().getString()), 0, lineNumber++, MATERIAL_COLOR);
            lineNumber += LINE_SPACING;
        }

        // HANDLE
        if (handleOptional.isPresent()) {
            HandleMaterialStats handle = handleOptional.get();
            drawShadow(stack, String.format("[%s]", handle.getLocalizedName().getString()), 0, lineNumber++, MATERIAL_COLOR);
            drawStatsShadow(stack, handle.getLocalizedInfo().get(0), lineNumber++, getMultiplierColor(handle.getDurability()));
            drawStatsShadow(stack, handle.getLocalizedInfo().get(1), lineNumber++, getMultiplierColor(handle.getAttackDamage()));
            drawStatsShadow(stack, handle.getLocalizedInfo().get(2), lineNumber++, getMultiplierColor(handle.getAttackSpeed()));
            drawStatsShadow(stack, handle.getLocalizedInfo().get(3), lineNumber, getMultiplierColor(handle.getMiningSpeed()));
        }
    }

    @Nonnull
    @Override
    public List<Component> getTooltipStrings(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String MATERIAL_NAME = ForgeI18n.getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        float lineNumber = 2f;

        Optional<HeadMaterialStats> headOptional = recipe.getStats(HeadMaterialStats.ID);
        Optional<ExtraMaterialStats> extraOptional = recipe.getStats(ExtraMaterialStats.ID);
        Optional<HandleMaterialStats> handleOptional =  recipe.getStats(HandleMaterialStats.ID);

        // MATERIAL
        int materialWidth = FONT.width(MATERIAL_NAME);
        if (inBox(mouseX, mouseY, (WIDTH - materialWidth) / 2f, LINE_SPACING * LINE_HEIGHT - 1, materialWidth, LINE_HEIGHT)) {
            return List.of(new TranslatableComponent(Util.makeTranslationKey("material", recipe.getMaterialId()) + ".flavor")
                    .withStyle(ChatFormatting.ITALIC));
        }

        // TRAIT
        Optional<? extends IMaterialStats> statOptional = Stream.of(headOptional, extraOptional, handleOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            List<Component> tooltips = getTraitTooltips(recipe.getTraits(statOptional.get().getIdentifier()), mouseX, mouseY, lineNumber);
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
        if (extraOptional.isPresent()) {
            lineNumber += LINE_SPACING + 1;
        }

        // HANDLE
        if (handleOptional.isPresent()) {
            lineNumber++;
            HandleMaterialStats handle = handleOptional.get();
            Optional<List<Component>> component = Stream.of(
                    getStatTooltip(handle, 0, mouseX, mouseY, lineNumber++),
                    getStatTooltip(handle, 1, mouseX, mouseY, lineNumber++),
                    getStatTooltip(handle, 2, mouseX, mouseY, lineNumber++),
                    getStatTooltip(handle, 3, mouseX, mouseY, lineNumber))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
        }

        return List.of();
    }
}
