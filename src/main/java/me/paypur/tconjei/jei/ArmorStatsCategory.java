package me.paypur.tconjei.jei;

import me.paypur.tconjei.ColorProvider;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.List;

public class ArmorStatsCategory extends MaterialStatsCategory {
    public ArmorStatsCategory(IGuiHelper guiHelper, RecipeType<MaterialStatsWrapper> recipeType, List<MaterialStatsId> statsIds, Component title, IDrawable icon, TagKey<Item> tag) {
        super(guiHelper, recipeType, statsIds, title, icon, tag);
    }

    @Override
    public void draw(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        final int tier = wrapper.material().getTier();
        final int color = MaterialTooltipCache.getColor(wrapper.getMaterialId()).getValue();
        float lineNumber = 0f;

        // name and tier
        drawComponentShadowCentered(gui, Component.translatable(Util.makeTranslationKey("material", wrapper.getMaterialId())).withStyle(ChatFormatting.UNDERLINE), lineNumber++, color);
        drawComponentShadowCentered(gui, Component.translatable("tconjei.tooltip.tier", tier), lineNumber++, ColorProvider.getTierColor(tier).orElse(color));

        List<IMaterialStats> statsList = getStatsPresent(wrapper);

        List<PlatingMaterialStats> platingList = statsList.stream()
                .filter(stats -> stats instanceof PlatingMaterialStats)
                .map(stats -> (PlatingMaterialStats) stats)
                .toList();

        List<StatlessMaterialStats> statlessList = statsList.stream()
                .filter(stats -> stats instanceof StatlessMaterialStats)
                .map(stats -> (StatlessMaterialStats) stats)
                .toList();

        IMaterialStats stat = statsList.get(0);
        List<ModifierEntry> traits = wrapper.getTraits(stat.getIdentifier());
        drawTraits(gui, traits, lineNumber);

        if (!platingList.isEmpty()) {
            drawComponent(gui, Component.translatable("stat.tconstruct.plating").withStyle(ChatFormatting.UNDERLINE), 0, lineNumber, color, true);

            // armor traits can be pretty long and would overlap with other text
            lineNumber += Math.max(traits.size(), 1);

            if (stat.getType() == PlatingMaterialStats.SHIELD) {
                for (Component line : stat.getLocalizedInfo()) {
                    drawStatComponent(gui, line, lineNumber++);
                }
            } else {
                String durabilityText = stat.getLocalizedInfo().get(0).plainCopy().getString();
                String armorText = stat.getLocalizedInfo().get(1).plainCopy().getString();
                // strip : and " "
                durabilityText = durabilityText.substring(0, durabilityText.length() - 2);
                armorText = armorText.substring(0, armorText.length() - 2);

                List<ArmorStat> armorStats = platingList.stream().map(plating -> new ArmorStat(plating.getLocalizedName(), plating.durability(), plating.armor())).toList();
                final int maxTextWidth = armorStats.stream().map(s -> FONT.width(s.text)).max(Integer::compareTo).get();
                final int maxArmorWidth = Math.max(armorStats.stream().map(s -> FONT.width(s.armor)).max(Integer::compareTo).get(), FONT.width(armorText));
                final int maxDurabilityWidth = Math.max(armorStats.stream().map(s -> FONT.width(s.durability)).max(Integer::compareTo).get(), FONT.width(durabilityText));

                final int space = FONT.width(" ");

                drawString(gui, durabilityText, maxTextWidth, lineNumber, ColorProvider.TEXT, false);
                drawString(gui, armorText, maxTextWidth + maxDurabilityWidth + space, lineNumber++, ColorProvider.TEXT, false);

                for (ArmorStat armorStat : armorStats) {
                    int x = maxTextWidth + maxDurabilityWidth - FONT.width(armorStat.durability);
                    int x1 = maxTextWidth + maxDurabilityWidth + space + maxArmorWidth - FONT.width(armorStat.armor);
                    drawString(gui, armorStat.text, 0, lineNumber, ColorProvider.TEXT, false);
                    drawString(gui, armorStat.durability, x, lineNumber, ColorProvider.DURABILITY, true); // durability
                    drawString(gui, armorStat.armor, x1, lineNumber++, ColorProvider.ARMOR, true); // armor
                }

                // these should be the same for the whole set
                drawStatComponent(gui, stat.getLocalizedInfo().get(2), lineNumber++); // toughness
                drawStatComponent(gui, stat.getLocalizedInfo().get(3), lineNumber++); // knockback resistance
            }
            lineNumber += LINE_SPACING;
        }

        for (StatlessMaterialStats statless : statlessList) {
            drawComponent(gui, statless.getLocalizedName().withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);
            for (Component line : statless.getLocalizedInfo()) {
                drawStatComponent(gui, line, lineNumber++);
            }
            lineNumber += LINE_SPACING;
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltips, MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // MATERIAL
        if (addMaterialTooltip(tooltips, wrapper, mouseX, mouseY)) return;

        float lineNumber = 2f;

        List<IMaterialStats> statsList = getStatsPresent(wrapper);
        List<IMaterialStats> platingList = statsList.stream().filter(stats -> stats instanceof PlatingMaterialStats).toList();

        IMaterialStats stat = statsList.get(0);  // TODO: the first stat might have fewer traits than other stats
        List<ModifierEntry> traits = wrapper.getTraits(stat.getIdentifier());
        if (addTraitTooltip(tooltips, traits, mouseX, mouseY, lineNumber)) return;

        // PLATING
        if (!platingList.isEmpty()) {
            if (stat.getType() == PlatingMaterialStats.SHIELD) {
                for (int i = 0; i < stat.getLocalizedDescriptions().size(); i++) {
                    if (addStatTooltip(tooltips, stat, i, mouseX, mouseY, lineNumber++)) return;
                }
            } else {
                // armor traits can be pretty long and would overlap with other text
                lineNumber += Math.max(traits.size(), 1);

                final int maxTextWidth = statsList.stream()
                        .map(s -> FONT.width(s.getLocalizedName().plainCopy().getString().split(" ")[0] + ": "))
                        .max(Integer::compareTo)
                        .get();

                String durabilityText = stat.getLocalizedInfo().get(0).plainCopy().getString();
                // strip : and " "
                durabilityText = durabilityText.substring(0, durabilityText.length() - 2);

                if (addStatTooltip(tooltips, stat, 0, maxTextWidth, mouseX, mouseY, lineNumber)) return;
                if (addStatTooltip(tooltips, stat, 1, maxTextWidth + FONT.width(durabilityText), mouseX, mouseY, lineNumber++))
                    return;

                lineNumber += platingList.size();
                for (int i = 2; i < stat.getLocalizedDescriptions().size(); i++) {
                    if (addStatTooltip(tooltips, stat, i, mouseX, mouseY, lineNumber++)) return;
                }
            }
        }
    }

    private record ArmorStat(String text, String durability, String armor) {
        private ArmorStat(Component text, int durability, float armor) {
            this(text.plainCopy().getString().split(" ")[0] + ": ", String.valueOf(durability), armor + " ");
        }
    }

}
