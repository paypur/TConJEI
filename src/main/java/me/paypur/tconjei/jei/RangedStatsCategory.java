package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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

public class RangedStatsCategory extends AbstractToolStatsCategory {

    public RangedStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 16, 0, 16, 16);
        this.title = new TextComponent("Ranged Stats");
        this.recipeType = RecipeType.create(MOD_ID, "ranged_stats", ToolStatsWrapper.class);
        this.uid = new ResourceLocation(MOD_ID, "ranged_stats");
        this.tag = TinkerTags.Items.RANGED;
    }

    @Override
    public void draw(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        final String MATERIAL_NAME = ForgeI18n.getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(recipe.getMaterialId()).getValue();
        float lineNumber = 2f;

        Optional<LimbMaterialStats> limbOptional = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = recipe.getStats(GripMaterialStats.ID);
        Optional<BowstringMaterialStats> stringOptional = recipe.getStats(BowstringMaterialStats.ID);

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
            drawStatsShadow(stack, limb.getLocalizedInfo().get(1), lineNumber++, getDifferenceColor(limb.getDrawSpeed()));
            drawStatsShadow(stack, limb.getLocalizedInfo().get(2), lineNumber++, getDifferenceColor(limb.getVelocity()));
            drawStatsShadow(stack, limb.getLocalizedInfo().get(3), lineNumber++, getDifferenceColor(limb.getAccuracy()));
            lineNumber += LINE_SPACING;
        }

        // GRIP
        if (gripOptional.isPresent()) {
            GripMaterialStats grip = gripOptional.get();
            drawShadow(stack, String.format("[%s]", grip.getLocalizedName().getString()), 0, lineNumber++, MATERIAL_COLOR);
            drawStatsShadow(stack, grip.getLocalizedInfo().get(0), lineNumber++, getMultiplierColor(grip.getDurability()));
            drawStatsShadow(stack, grip.getLocalizedInfo().get(1), lineNumber++, getDifferenceColor(grip.getAccuracy()));
            drawStatsShadow(stack, grip.getLocalizedInfo().get(2), lineNumber++, ATTACK_COLOR);
            lineNumber += LINE_SPACING;
        }

        // STRING
        if (stringOptional.isPresent()) {
            BowstringMaterialStats string = stringOptional.get();
            drawShadow(stack, String.format("[%s]", string.getLocalizedName().getString()), 0, lineNumber, MATERIAL_COLOR);
        }
    }

    @Nonnull
    @Override
    public List<Component> getTooltipStrings(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String MATERIAL_NAME = ForgeI18n.getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        float lineNumber = 2f;

        Optional<LimbMaterialStats> limbOptional = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = recipe.getStats(GripMaterialStats.ID);
        Optional<BowstringMaterialStats> stringOptional = recipe.getStats(BowstringMaterialStats.ID);

        // MATERIAL
        int materialWidth = FONT.width(MATERIAL_NAME);
        if (inBox(mouseX, mouseY, (WIDTH - materialWidth) / 2f, LINE_SPACING * LINE_HEIGHT - 1, materialWidth, LINE_HEIGHT)) {
            return List.of(new TranslatableComponent(Util.makeTranslationKey("material", recipe.getMaterialId()) + ".flavor")
                        .withStyle(ChatFormatting.ITALIC));
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
