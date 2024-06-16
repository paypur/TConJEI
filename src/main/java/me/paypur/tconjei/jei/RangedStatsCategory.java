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
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
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
import static slimeknights.tconstruct.library.utils.Util.makeTranslationKey;

public class RangedStatsCategory extends AbstractToolStatsCategory {

    public RangedStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper, TinkerTags.Items.RANGED);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 16, 0, 16, 16);
        this.title = new TextComponent("Ranged Stats");
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
        Optional<BowstringMaterialStats> stringOptional = recipe.getStats(BowstringMaterialStats.ID);

        // MATERIAL
        drawShadow(stack, MATERIAL_NAME, (WIDTH - FONT.width(MATERIAL_NAME)) / 2f, LINE_SPACING, MATERIAL_COLOR);

        // TRAITS
        List<ModifierEntry> traits = Stream.of(limbOptional, gripOptional, stringOptional)
                .filter(Optional::isPresent)
                .findFirst()
                .map(stat -> recipe.getTraits(stat.get().getIdentifier()))
                .get();
        drawTraits(stack, traits, lineNumber);

        // LIMB
        if (limbOptional.isPresent()) {
            LimbMaterialStats limbStats = limbOptional.get();
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.limb")), 0, lineNumber++, MATERIAL_COLOR);
            drawStats(stack, "tool_stat.tconstruct.durability", String.valueOf(limbStats.getDurability()), lineNumber++, DURABILITY_COLOR);
            drawStats(stack, "tool_stat.tconstruct.draw_speed", signedString(limbStats.getDrawSpeed()), lineNumber++, getDifferenceColor(limbStats.getDrawSpeed()));
            drawStats(stack, "tool_stat.tconstruct.velocity", signedString(limbStats.getVelocity()), lineNumber++, getDifferenceColor(limbStats.getVelocity()));
            drawStats(stack, "tool_stat.tconstruct.accuracy", signedString(limbStats.getAccuracy()), lineNumber++, getDifferenceColor(limbStats.getAccuracy()));
            lineNumber += LINE_SPACING;
        }

        // GRIP
        if (gripOptional.isPresent()) {
            GripMaterialStats gripStats = gripOptional.get();
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.grip")), 0, lineNumber++, MATERIAL_COLOR);
            drawStats(stack, "tool_stat.tconstruct.durability", String.format("%.2fx", gripStats.getDurability()), lineNumber++, getMultiplierColor(gripStats.getDurability()));
            drawStats(stack, "tool_stat.tconstruct.accuracy", signedString(gripStats.getAccuracy()), lineNumber++, getDifferenceColor(gripStats.getAccuracy()));
            drawStats(stack, "tool_stat.tconstruct.attack_damage", String.format("%.2f", gripStats.getMeleeAttack()), lineNumber++, ATTACK_COLOR);
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
        final String MATERIAL_NAME = getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        float lineNumber = 2f;

        Optional<LimbMaterialStats> limbOptional = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripOptional = recipe.getStats(GripMaterialStats.ID);
        Optional<BowstringMaterialStats> stringOptional = recipe.getStats(BowstringMaterialStats.ID);

        // MATERIAL
        int materialWidth = FONT.width(MATERIAL_NAME);
        if (inBox(mouseX, mouseY, (WIDTH - materialWidth) / 2f, LINE_SPACING * LINE_HEIGHT - 1, materialWidth, LINE_HEIGHT)) {
            return List.of(new TranslatableComponent(makeTranslationKey("material", recipe.getMaterialId()) + ".flavor").withStyle(ChatFormatting.ITALIC));
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

        return Collections.emptyList();
    }

    private String signedString(float f) {
        return String.format("%s%.2f", f >= 0 ? "+" : "", f);
    }

}
