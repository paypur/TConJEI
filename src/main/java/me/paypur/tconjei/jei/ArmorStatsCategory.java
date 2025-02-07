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
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static me.paypur.tconjei.ColorManager.*;
import static me.paypur.tconjei.TConJEI.MOD_ID;

public class ArmorStatsCategory extends AbstractMaterialStatsCategory {

    public ArmorStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 32, 0, 16, 16);
        this.title = Component.translatable("tconjei.tool_stats.armor");
        this.recipeType = RecipeType.create(MOD_ID, "armor_stats", MaterialStatsWrapper.class);
        this.tag = TinkerTags.Items.ARMOR;
    }

    @Override
    public void draw(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        super.draw(wrapper, recipeSlotsView, gui, mouseX, mouseY);

        final int color = MaterialTooltipCache.getColor(wrapper.getMaterialId()).getValue();
        float lineNumber = 2f;

        Optional<PlatingMaterialStats> helmetOptional = wrapper.getStats(PlatingMaterialStats.HELMET.getId());
        Optional<PlatingMaterialStats> chestplateOptional = wrapper.getStats(PlatingMaterialStats.CHESTPLATE.getId());
        Optional<PlatingMaterialStats> leggingsOptional = wrapper.getStats(PlatingMaterialStats.LEGGINGS.getId());
        Optional<PlatingMaterialStats> bootsOptional = wrapper.getStats(PlatingMaterialStats.BOOTS.getId());
        Optional<PlatingMaterialStats> shieldOptional = wrapper.getStats(PlatingMaterialStats.SHIELD.getId());
        Optional<StatlessMaterialStats> coreOptional = wrapper.getStats(StatlessMaterialStats.SHIELD_CORE.getIdentifier());
        Optional<StatlessMaterialStats> mailleOptional = wrapper.getStats(StatlessMaterialStats.MAILLE.getIdentifier());

        // TRAITS
        Optional<? extends IMaterialStats> statOptional = Stream.of(helmetOptional, chestplateOptional, leggingsOptional, bootsOptional, shieldOptional, coreOptional, mailleOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            drawTraits(gui, wrapper.getTraits(statOptional.get().getIdentifier()), lineNumber);
        }

        List<ArmorStat> armorStats = new ArrayList<>();

        if (helmetOptional.isPresent()) {
            PlatingMaterialStats helmet = helmetOptional.get();
            armorStats.add(new ArmorStat(helmet.getLocalizedName().getString(), helmet.durability(), helmet.armor()));
        }

        if (chestplateOptional.isPresent()) {
            PlatingMaterialStats chestplate = chestplateOptional.get();
            armorStats.add(new ArmorStat(chestplate.getLocalizedName().getString(), chestplate.durability(), chestplate.armor()));
        }

        if (leggingsOptional.isPresent()) {
            PlatingMaterialStats leggings = leggingsOptional.get();
            armorStats.add(new ArmorStat(leggings.getLocalizedName().getString(), leggings.durability(), leggings.armor()));
        }

        if (bootsOptional.isPresent()) {
            PlatingMaterialStats boots = bootsOptional.get();
            armorStats.add(new ArmorStat(boots.getLocalizedName().getString(), boots.durability(), boots.armor()));
        }

        if (shieldOptional.isPresent()) {
            PlatingMaterialStats shield = shieldOptional.get();
            armorStats.add(new ArmorStat(shield.getLocalizedName().getString(), shield.durability(), shield.armor()));
        }

        Optional<PlatingMaterialStats> platingStats = Stream.of(helmetOptional, chestplateOptional, leggingsOptional, bootsOptional, shieldOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (platingStats.isPresent()) {
            PlatingMaterialStats plating = platingStats.get();
            drawComponent(gui, Component.translatable("stat.tconstruct.plating").withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);

            if (plating.getType() == PlatingMaterialStats.SHIELD) {
                drawStatComponent(gui, plating.getLocalizedInfo().get(0), lineNumber++); // durability
                drawStatComponent(gui, plating.getLocalizedInfo().get(1), lineNumber++); // toughness
                drawStatComponent(gui, plating.getLocalizedInfo().get(2), lineNumber++); // knockback resistance
            } else {
                String durabilityText = plating.getLocalizedInfo().get(0).plainCopy().getString();
                String armorText = plating.getLocalizedInfo().get(1).plainCopy().getString();

                int durabilityTextWidth = FONT.width(durabilityText);
                int armorTextWidth = FONT.width(armorText);

                int maxTextWidth = FONT.width(Collections.max(armorStats, Comparator.comparingInt(s -> FONT.width(s.text))).text);
                int maxArmorWidth = FONT.width(Collections.max(armorStats, Comparator.comparingInt(s -> FONT.width(s.armor))).armor);
                int maxDurabilityWidth = FONT.width(Collections.max(armorStats, Comparator.comparingInt(s -> FONT.width(s.durability))).durability);

                String line = "─";
                int lineWidth = FONT.width(line);

                int durabilityLine = Math.max((maxTextWidth + maxArmorWidth + maxDurabilityWidth - durabilityTextWidth) / lineWidth - 1, 0);
                int armorLine = Math.max((maxTextWidth + maxArmorWidth - armorTextWidth) / lineWidth - 1, 0);

                drawString(gui, durabilityText, 0, lineNumber, TEXT_COLOR, false);
                // durability line
                drawString(gui, line.repeat(durabilityLine) + "┐", durabilityTextWidth, lineNumber++, DURABILITY_COLOR, true);
                drawString(gui, "│", durabilityTextWidth + lineWidth * durabilityLine, lineNumber, DURABILITY_COLOR, true);

                drawString(gui, armorText, 0, lineNumber, TEXT_COLOR, false);
                // armor line
                drawString(gui, line.repeat(armorLine) + "┐", armorTextWidth, lineNumber++, ARMOR_COLOR, true);

                for (ArmorStat armorStat : armorStats) {
                    drawString(gui, armorStat.text, 0, lineNumber, TEXT_COLOR, false);
                    drawString(gui, armorStat.armor, maxTextWidth + maxArmorWidth - FONT.width(armorStat.armor), lineNumber, ARMOR_COLOR, true); // armor, drawn first because its on the left
                    drawString(gui, armorStat.durability, maxTextWidth + maxArmorWidth + maxDurabilityWidth - FONT.width(armorStat.durability), lineNumber++, DURABILITY_COLOR, true); // durability
                }

                // these should be the same for the whole set
                drawStatComponent(gui, plating.getLocalizedInfo().get(2), lineNumber++); // toughness
                drawStatComponent(gui, plating.getLocalizedInfo().get(3), lineNumber++); // knockback resistance
            }
            lineNumber += LINE_SPACING;
        }

        if (coreOptional.isPresent()) {
            StatlessMaterialStats core = coreOptional.get();
            drawComponent(gui, core.getLocalizedName().withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);
            drawComponent(gui, core.getLocalizedInfo().get(0), 0, lineNumber++, TEXT_COLOR, false);
            lineNumber += LINE_SPACING;
        }

        if (mailleOptional.isPresent()) {
            StatlessMaterialStats maille = mailleOptional.get();
            drawComponent(gui, maille.getLocalizedName().withStyle(ChatFormatting.UNDERLINE), 0, lineNumber++, color, true);
            drawComponent(gui, maille.getLocalizedInfo().get(0), 0, lineNumber, TEXT_COLOR, false);
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

        Optional<PlatingMaterialStats> helmetOptional = wrapper.getStats(PlatingMaterialStats.HELMET.getId());
        Optional<PlatingMaterialStats> chestplateOptional = wrapper.getStats(PlatingMaterialStats.CHESTPLATE.getId());
        Optional<PlatingMaterialStats> leggingsOptional = wrapper.getStats(PlatingMaterialStats.LEGGINGS.getId());
        Optional<PlatingMaterialStats> bootsOptional = wrapper.getStats(PlatingMaterialStats.BOOTS.getId());
        Optional<PlatingMaterialStats> shieldOptional = wrapper.getStats(PlatingMaterialStats.SHIELD.getId());
        Optional<StatlessMaterialStats> coreOptional = wrapper.getStats(StatlessMaterialStats.SHIELD_CORE.getIdentifier());
        Optional<StatlessMaterialStats> mailleOptional = wrapper.getStats(StatlessMaterialStats.MAILLE.getIdentifier());

        // TRAIT
        Optional<? extends IMaterialStats> statOptional = Stream.of(helmetOptional, chestplateOptional, leggingsOptional, bootsOptional, shieldOptional, coreOptional, mailleOptional)
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

        Optional<PlatingMaterialStats> platingStats = Stream.of(helmetOptional, chestplateOptional, leggingsOptional, bootsOptional, shieldOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        // PLATING
        if (platingStats.isPresent()) {
            lineNumber++;
            PlatingMaterialStats plating = platingStats.get();
            if (plating.getType() == PlatingMaterialStats.SHIELD) {
                Stream<List<Component>> stream = Stream.of(
                        getStatTooltip(plating, 0, mouseX, mouseY, lineNumber++),  // durability
                        getStatTooltip(plating, 1, mouseX, mouseY, lineNumber++), // toughness
                        getStatTooltip(plating, 2, mouseX, mouseY, lineNumber)   // knockback resistance
                );
                Optional<List<Component>> component = stream
                        .filter(list -> !list.isEmpty())
                        .findFirst();
                if (component.isPresent()) {
                    tooltip.addAll(component.get());
                }
            } else {
                Stream<List<Component>> stream = Stream.of(
                        getStatTooltip(plating, 0, mouseX, mouseY, lineNumber++), // durability
                        getStatTooltip(plating, 1, mouseX, mouseY, lineNumber)    // armor
                );
                lineNumber += 6;
                stream = Stream.concat(stream, Stream.of(
                        getStatTooltip(plating, 2, mouseX, mouseY, lineNumber++), // toughness
                        getStatTooltip(plating, 3, mouseX, mouseY, lineNumber))   // knockback resistance
                );
                Optional<List<Component>> component = stream
                        .filter(list -> !list.isEmpty())
                        .findFirst();
                if (component.isPresent()) {
                    tooltip.addAll(component.get());
                }
            }
        }
    }

    private record ArmorStat(String text, String durability, String armor) {
        private ArmorStat(String text, int durability, float armor) {
            this(text.split(" ")[0] + ": ", String.valueOf(durability), armor + " ");
        }
    }

}
