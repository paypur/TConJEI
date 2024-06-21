package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.paypur.tconjei.ColorManager.*;
import static me.paypur.tconjei.TConJEI.MOD_ID;
import static net.minecraftforge.common.ForgeI18n.getPattern;

public class ArmorStatsCategory extends AbstractToolStatsCategory {

    public ArmorStatsCategory(IGuiHelper guiHelper) {
        super(guiHelper, TinkerTags.Items.ARMOR);
        this.icon = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/jei.png"), 32, 0, 16, 16);
        this.title =  MutableComponent.create(new LiteralContents("Armor Stats"));
        this.recipeType = RecipeType.create(MOD_ID, "armor_stats", ToolStatsWrapper.class);
        this.uid = new ResourceLocation(MOD_ID, "armor_stats");
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

        // MATERIAL
        drawShadow(stack, MATERIAL_NAME, (WIDTH - FONT.width(MATERIAL_NAME)) / 2f, LINE_SPACING, MATERIAL_COLOR);

        // TRAITS
        Optional<List<ModifierEntry>> traits = Stream.of(helmetOptional, chestplateOptional, leggingsOptional, bootsOptional)
                .filter(Optional::isPresent)
                .findFirst()
                .map(stat -> recipe.getTraits(stat.get().getIdentifier()));
        traits.ifPresent(modifierEntries -> drawTraits(stack, modifierEntries, lineNumber));

        if (helmetOptional.isPresent()) {
            PlatingMaterialStats helmet = helmetOptional.get();
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.plating")), 0, lineNumber++, MATERIAL_COLOR);
            drawStatsShadow(stack, helmet.getLocalizedInfo().get(0), lineNumber++, );
            drawStatsShadow(stack, helmet.getLocalizedInfo().get(1), lineNumber++, );
            drawStatsShadow(stack, helmet.getLocalizedInfo().get(2), lineNumber++, );
            drawStatsShadow(stack, helmet.getLocalizedInfo().get(3), lineNumber++, );
            lineNumber += LINE_SPACING;
        }

    }

    @Override
    public List<Component> getTooltipStrings(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        return super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
    }

}
