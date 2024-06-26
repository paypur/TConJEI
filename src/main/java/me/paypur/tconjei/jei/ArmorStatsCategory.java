package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static me.paypur.tconjei.ColorManager.*;
import static me.paypur.tconjei.TConJEI.MOD_ID;
import static net.minecraftforge.common.ForgeI18n.getPattern;

public class ArmorStatsCategory extends AbstractToolStatsCategory {

    public ArmorStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 32, 0, 16, 16);
        this.title = MutableComponent.create(new LiteralContents("Armor Stats"));
        this.recipeType = RecipeType.create(MOD_ID, "armor_stats", ToolStatsWrapper.class);
        this.tag = TinkerTags.Items.ARMOR;
    }

    @Override
    public void draw(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        final String MATERIAL_NAME = getPattern(Util.makeTranslationKey("material", recipe.getMaterialId()));
        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(recipe.getMaterialId()).getValue();
        float lineNumber = 2f;

        Optional<PlatingMaterialStats> helmetOptional = recipe.getStats(PlatingMaterialStats.HELMET.getId());
        Optional<PlatingMaterialStats> chestplateOptional = recipe.getStats(PlatingMaterialStats.CHESTPLATE.getId());
        Optional<PlatingMaterialStats> leggingsOptional = recipe.getStats(PlatingMaterialStats.LEGGINGS.getId());
        Optional<PlatingMaterialStats> bootsOptional = recipe.getStats(PlatingMaterialStats.BOOTS.getId());
        Optional<PlatingMaterialStats> shieldOptional = recipe.getStats(PlatingMaterialStats.SHIELD.getId());
        Optional<StatlessMaterialStats> coreOptional = recipe.getStats(StatlessMaterialStats.SHIELD_CORE.getIdentifier());
        Optional<StatlessMaterialStats> mailleOptional = recipe.getStats(StatlessMaterialStats.MAILLE.getIdentifier());

        // MATERIAL
        drawShadow(stack, MATERIAL_NAME, (WIDTH - FONT.width(MATERIAL_NAME)) / 2, LINE_SPACING, MATERIAL_COLOR);

        // TRAITS
        Optional<List<ModifierEntry>> traits = Stream.of(helmetOptional, chestplateOptional, leggingsOptional, bootsOptional, shieldOptional, coreOptional, mailleOptional)
                .filter(Optional::isPresent)
                .findFirst()
                .map(stat -> recipe.getTraits(stat.get().getIdentifier()));

        if (traits.isPresent()) {
            drawTraits(stack, traits.get(), lineNumber);
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
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.plating")), 0, lineNumber++, MATERIAL_COLOR);

            String durabilityText = plating.getLocalizedInfo().get(0).getString().split(":")[0] + ": ";
            String armorText = plating.getLocalizedInfo().get(1).getString().split(":")[0] + ": ";

            int durabilityTextWidth = FONT.width(durabilityText);
            int armorTextWidth = FONT.width(armorText);

            int maxTextWidth = FONT.width(Collections.max(armorStats, Comparator.comparingInt(s -> FONT.width(s.text))).text);
            int maxArmorWidth = FONT.width(Collections.max(armorStats, Comparator.comparingInt(s -> FONT.width(s.armor))).armor);
            int maxDurabilityWidth = FONT.width(Collections.max(armorStats, Comparator.comparingInt(s -> FONT.width(s.durability))).durability);

            String line = "─";
            int lineWidth = FONT.width(line);

            int durabilityLine = (maxTextWidth + maxArmorWidth + maxDurabilityWidth - durabilityTextWidth) / lineWidth - 1;
            int armorLine = (maxTextWidth + maxArmorWidth - armorTextWidth) / lineWidth - 1;

            draw(stack, durabilityText, 0, lineNumber, TEXT_COLOR);
            drawShadow(stack, line.repeat(durabilityLine) + "┐", durabilityTextWidth, lineNumber++, DURABILITY_COLOR);
            drawShadow(stack, "│", durabilityTextWidth + lineWidth * durabilityLine, lineNumber, DURABILITY_COLOR);

            draw(stack, armorText, 0, lineNumber, TEXT_COLOR);
            drawShadow(stack, line.repeat(armorLine) + "┐", armorTextWidth, lineNumber++, ARMOR_COLOR);

            for (ArmorStat armorStat : armorStats) {
                draw(stack, armorStat.text, 0, lineNumber, TEXT_COLOR);
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
            drawShadow(stack, String.format("[%s]", core.getLocalizedName().getString()), 0, lineNumber++, MATERIAL_COLOR);
            lineNumber += LINE_SPACING;
        }

        if (mailleOptional.isPresent()) {
            StatlessMaterialStats maille = mailleOptional.get();
            drawShadow(stack, String.format("[%s]", maille.getLocalizedName().getString()), 0, lineNumber, MATERIAL_COLOR);
        }
    }

    @Override
    public List<Component> getTooltipStrings(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        float lineNumber = 2f;

        Optional<PlatingMaterialStats> helmetOptional = recipe.getStats(PlatingMaterialStats.HELMET.getId());
        Optional<PlatingMaterialStats> chestplateOptional = recipe.getStats(PlatingMaterialStats.CHESTPLATE.getId());
        Optional<PlatingMaterialStats> leggingsOptional = recipe.getStats(PlatingMaterialStats.LEGGINGS.getId());
        Optional<PlatingMaterialStats> bootsOptional = recipe.getStats(PlatingMaterialStats.BOOTS.getId());
        Optional<PlatingMaterialStats> shieldOptional = recipe.getStats(PlatingMaterialStats.SHIELD.getId());
        Optional<StatlessMaterialStats> coreOptional = recipe.getStats(StatlessMaterialStats.SHIELD_CORE.getIdentifier());
        Optional<StatlessMaterialStats> mailleOptional = recipe.getStats(StatlessMaterialStats.MAILLE.getIdentifier());

        // MATERIAL
        List<Component> material = super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
        if (!material.isEmpty()) {
            return material;
        }

        // TRAIT
        Optional<? extends IMaterialStats> statOptional = Stream.of(helmetOptional, chestplateOptional, leggingsOptional, bootsOptional, shieldOptional, coreOptional, mailleOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (statOptional.isPresent()) {
            List<Component> tooltips = getTraitTooltips(recipe.getTraits(statOptional.get().getIdentifier()), mouseX, mouseY, lineNumber);
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

        // TODO: add tooltips to the stats themselves to help distinguish them
        List<Optional<PlatingMaterialStats>> plating = List.of(helmetOptional, chestplateOptional, leggingsOptional, bootsOptional, shieldOptional);

        return List.of();
    }

    private record ArmorStat(String text, String durability, String armor) {
        private ArmorStat(String text, int durability, float armor) {
            this(text.split(" ")[0] + ": ", String.valueOf(durability), armor + " ");
        }
    }

}
