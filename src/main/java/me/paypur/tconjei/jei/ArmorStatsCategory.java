package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.paypur.tconjei.Utils;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeI18n;
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
    public void draw(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        super.draw(wrapper, recipeSlotsView, stack, mouseX, mouseY);

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
            drawTraits(stack, wrapper.getTraits(statOptional.get().getIdentifier()), lineNumber);
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
            drawShadow(stack, String.format("[%s]", ForgeI18n.getPattern("stat.tconstruct.plating")), 0, lineNumber++, color);

            String durabilityText = Utils.colonSplit(plating.getLocalizedInfo().get(0).getString())[0] + " ";
            String armorText = Utils.colonSplit(plating.getLocalizedInfo().get(1).getString())[0] + " ";

            int durabilityTextWidth = FONT.width(durabilityText);
            int armorTextWidth = FONT.width(armorText);

            int maxTextWidth = FONT.width(Collections.max(armorStats, Comparator.comparingInt(s -> FONT.width(s.text))).text);
            int maxArmorWidth = FONT.width(Collections.max(armorStats, Comparator.comparingInt(s -> FONT.width(s.armor))).armor);
            int maxDurabilityWidth = FONT.width(Collections.max(armorStats, Comparator.comparingInt(s -> FONT.width(s.durability))).durability);

            String line = "─";
            int lineWidth = FONT.width(line);

            int durabilityLine = (maxTextWidth + maxArmorWidth + maxDurabilityWidth - durabilityTextWidth) / lineWidth - 1;
            int armorLine = (maxTextWidth + maxArmorWidth - armorTextWidth) / lineWidth - 1;

            drawString(stack, durabilityText, 0, lineNumber, TEXT_COLOR);
            drawShadow(stack, line.repeat(durabilityLine) + "┐", durabilityTextWidth, lineNumber++, DURABILITY_COLOR);
            drawShadow(stack, "│", durabilityTextWidth + lineWidth * durabilityLine, lineNumber, DURABILITY_COLOR);

            drawString(stack, armorText, 0, lineNumber, TEXT_COLOR);
            drawShadow(stack, line.repeat(armorLine) + "┐", armorTextWidth, lineNumber++, ARMOR_COLOR);

            for (ArmorStat armorStat : armorStats) {
                drawString(stack, armorStat.text, 0, lineNumber, TEXT_COLOR);
                drawShadow(stack, armorStat.armor, maxTextWidth, lineNumber, ARMOR_COLOR); // armor, drawn first because its on the left
                drawShadow(stack, armorStat.durability, maxTextWidth + maxArmorWidth, lineNumber++, DURABILITY_COLOR); // durability
            }

            // these should be the same for the whole set
            drawStatsShadow(stack, plating.getLocalizedInfo().get(2), lineNumber++, ARMOR_COLOR); // toughness
            drawStatsShadow(stack, plating.getLocalizedInfo().get(3), lineNumber++, ARMOR_COLOR); // knockback resistance
            lineNumber += LINE_SPACING;
        }

        if (coreOptional.isPresent()) {
            StatlessMaterialStats core = coreOptional.get();
            drawShadow(stack, String.format("[%s]", core.getLocalizedName().getString()), 0, lineNumber++, color);
            drawString(stack, ForgeI18n.getPattern("tool_stat.tconstruct.extra.no_stats"), 0, lineNumber++, TEXT_COLOR);
            lineNumber += LINE_SPACING;
        }

        if (mailleOptional.isPresent()) {
            StatlessMaterialStats maille = mailleOptional.get();
            drawShadow(stack, String.format("[%s]", maille.getLocalizedName().getString()), 0, lineNumber++, color);
            drawString(stack, ForgeI18n.getPattern("tool_stat.tconstruct.extra.no_stats"), 0, lineNumber, TEXT_COLOR);
        }
    }

    @Override
    public List<Component> getTooltipStrings(MaterialStatsWrapper wrapper, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        float lineNumber = 2f;

        Optional<PlatingMaterialStats> helmetOptional = wrapper.getStats(PlatingMaterialStats.HELMET.getId());
        Optional<PlatingMaterialStats> chestplateOptional = wrapper.getStats(PlatingMaterialStats.CHESTPLATE.getId());
        Optional<PlatingMaterialStats> leggingsOptional = wrapper.getStats(PlatingMaterialStats.LEGGINGS.getId());
        Optional<PlatingMaterialStats> bootsOptional = wrapper.getStats(PlatingMaterialStats.BOOTS.getId());
        Optional<PlatingMaterialStats> shieldOptional = wrapper.getStats(PlatingMaterialStats.SHIELD.getId());
        Optional<StatlessMaterialStats> coreOptional = wrapper.getStats(StatlessMaterialStats.SHIELD_CORE.getIdentifier());
        Optional<StatlessMaterialStats> mailleOptional = wrapper.getStats(StatlessMaterialStats.MAILLE.getIdentifier());

        // MATERIAL
        List<Component> material = super.getTooltipStrings(wrapper, recipeSlotsView, mouseX, mouseY);
        if (!material.isEmpty()) {
            return material;
        }

        // TRAIT
        Optional<? extends IMaterialStats> statOptional = Stream.of(helmetOptional, chestplateOptional, leggingsOptional, bootsOptional, shieldOptional, coreOptional, mailleOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            List<Component> tooltips = getTraitTooltips(wrapper.getTraits(statOptional.get().getIdentifier()), mouseX, mouseY, lineNumber);
            if (!tooltips.isEmpty()) {
                return tooltips;
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
            Stream<List<Component>> stream = Stream.of(
                    getStatTooltip(plating, 0, mouseX, mouseY, lineNumber++), // durability
                    getStatTooltip(plating, 1, mouseX, mouseY, lineNumber) // armor
            );
            lineNumber += 6;
            stream = Stream.concat(stream, Stream.of(
                    getStatTooltip(plating, 2, mouseX, mouseY, lineNumber++), // toughness
                    getStatTooltip(plating, 3, mouseX, mouseY, lineNumber)) // knockback resistance
            );
            Optional<List<Component>> component = stream
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
        }

        return List.of();
    }

    private record ArmorStat(String text, String durability, String armor) {
        private ArmorStat(String text, int durability, float armor) {
            this(text.split(" ")[0] + ": ", String.valueOf(durability), armor + " ");
        }
    }

}
