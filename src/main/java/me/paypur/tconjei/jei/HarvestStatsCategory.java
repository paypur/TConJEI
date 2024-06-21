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

public class HarvestStatsCategory extends AbstractToolStatsCategory {

    public HarvestStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper, TinkerTags.Items.HARVEST);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 0, 0, 16, 16);
        this.title = new TextComponent("Harvest Stats");
        this.recipeType = RecipeType.create(MOD_ID, "harvest_stats", ToolStatsWrapper.class);
        this.uid = new ResourceLocation(MOD_ID, "harvest_stats");
    }

    @Override
    public void draw(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        final String MATERIAL_NAME = getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(recipe.getMaterialId()).getValue();
        float lineNumber = 2f;

        Optional<HeadMaterialStats> headOptional = recipe.getStats(HeadMaterialStats.ID);
        Optional<ExtraMaterialStats> extraOptional = recipe.getStats(ExtraMaterialStats.ID);
        Optional<HandleMaterialStats> handleOptional = recipe.getStats(HandleMaterialStats.ID);

        // MATERIAL
        drawShadow(stack, MATERIAL_NAME, (WIDTH - FONT.width(MATERIAL_NAME)) / 2f, LINE_SPACING, MATERIAL_COLOR);

        // TRAITS
        List<ModifierEntry> traits = Stream.of(handleOptional, extraOptional, handleOptional)
                .filter(Optional::isPresent)
                .findFirst()
                .map(stat -> recipe.getTraits(stat.get().getIdentifier()))
                .get();
        drawTraits(stack, traits, lineNumber);

        // HEAD
        if (headOptional.isPresent()) {
            HeadMaterialStats head = headOptional.get();
            ResourceLocation miningLevel = head.getTierId();
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.head")), 0, lineNumber++, MATERIAL_COLOR);
            drawStatsShadow(stack, "tool_stat.tconstruct.durability", String.valueOf(head.getDurability()), lineNumber++, DURABILITY_COLOR);
            drawStatsShadow(stack, "tool_stat.tconstruct.harvest_tier", getPattern(Util.makeTranslationKey("stat.tconstruct.harvest_tier", miningLevel)), lineNumber++, getMiningLevelColor(miningLevel));
            drawStatsShadow(stack, "tool_stat.tconstruct.mining_speed", String.format("%.2f", head.getMiningSpeed()), lineNumber++, MINING_COLOR);
            drawStatsShadow(stack, "tool_stat.tconstruct.attack_damage", String.format("%.2f", head.getAttack()), lineNumber++, ATTACK_COLOR);
            lineNumber += LINE_SPACING;
        }

        // EXTRA
        if (extraOptional.isPresent()) {
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.extra")), 0, lineNumber++, MATERIAL_COLOR);
            lineNumber += LINE_SPACING;
        }

        // HANDLE
        if (handleOptional.isPresent()) {
            HandleMaterialStats handle = handleOptional.get();
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.handle")), 0, lineNumber++, MATERIAL_COLOR);
            drawStatsShadow(stack, "tool_stat.tconstruct.durability", String.format("%.2fx", handle.getDurability()), lineNumber++, getMultiplierColor(handle.getDurability()));
            drawStatsShadow(stack, "tool_stat.tconstruct.attack_damage", String.format("%.2fx", handle.getAttackDamage()), lineNumber++, getMultiplierColor(handle.getAttackDamage()));
            drawStatsShadow(stack, "tool_stat.tconstruct.attack_speed", String.format("%.2fx", handle.getAttackSpeed()), lineNumber++, getMultiplierColor(handle.getAttackSpeed()));
            drawStatsShadow(stack, "tool_stat.tconstruct.mining_speed", String.format("%.2fx", handle.getMiningSpeed()), lineNumber, getMultiplierColor(handle.getMiningSpeed()));
        }
    }

    @Nonnull
    @Override
    public List<Component> getTooltipStrings(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String MATERIAL_NAME = getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        float lineNumber = 2f;

        Optional<HeadMaterialStats> headOptional = recipe.getStats(HeadMaterialStats.ID);
        Optional<ExtraMaterialStats> extraOptional = recipe.getStats(ExtraMaterialStats.ID);
        Optional<HandleMaterialStats> handleOptional =  recipe.getStats(HandleMaterialStats.ID);

        // MATERIAL
        int materialWidth = FONT.width(MATERIAL_NAME);
        if (inBox(mouseX, mouseY, (WIDTH - materialWidth) / 2f, LINE_SPACING * LINE_HEIGHT - 1, materialWidth, LINE_HEIGHT)) {
            return List.of(new TranslatableComponent(makeTranslationKey("material", recipe.getMaterialId()) + ".flavor").withStyle(ChatFormatting.ITALIC));
        }

        // TRAIT
        List<ModifierEntry> traits = Stream.of(headOptional, extraOptional, handleOptional)
                .filter(Optional::isPresent)
                .findFirst()
                .map(stat -> recipe.getTraits(stat.get().getIdentifier()))
                .get();
        List<Component> traitComponents = getTraitTooltips(traits, mouseX, mouseY, lineNumber);
        if (!traitComponents.isEmpty()) {
            return traitComponents;
        }

        // HEAD
        if (headOptional.isPresent()) {
            lineNumber++;
            Optional<List<Component>> component = Stream.of(
                    getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.harvest_tier", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.mining_speed", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.attack_damage", mouseX, mouseY, lineNumber++))
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
            Optional<List<Component>> component = Stream.of(
                    getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.attack_damage", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.attack_speed", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.mining_speed", mouseX, mouseY, lineNumber))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
        }

        return Collections.emptyList();
    }
}
