package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.stats.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static me.paypur.tconjei.TConJEI.MOD_ID;
import static net.minecraftforge.common.ForgeI18n.getPattern;

public class ToolStatsCategory implements IRecipeCategory<ToolStatsWrapper> {

    public static final int WIDTH = 164, HEIGHT = 240;
    public static ResourceLocation UID = new ResourceLocation(MOD_ID, "tool_stats");
    private final IDrawable BACKGROUND, ICON;

    public ToolStatsCategory(IGuiHelper guiHelper) {
        this.BACKGROUND = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        // only draws 1x1 for some reason
        this.ICON = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/icon.png"), 0, 0, 16, 16);
    }

    @Override
    public void draw(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        final String materialName = recipe.getMaterialId().getPath();
        final Font font = Minecraft.getInstance().font;

        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(recipe.getMaterialId()).getValue();
        final int BLACK = 8;
        final int GRAY = 4144959;
        final int LIGHT_GRAY = 5526612;

        final int LINE_OFFSET = 20;
        final int LINE_HEIGHT = 10;
        float lineNumber = 0;

        font.drawShadow(poseStack, getPattern("material.tconstruct." + recipe.getMaterialId().getPath()), (WIDTH - font.getSplitter().stringWidth(materialName)) / 2, 4, MATERIAL_COLOR);

        Optional<HeadMaterialStats> headStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), HeadMaterialStats.ID);
        List<ModifierEntry> headTrait = MaterialRegistry.getInstance().getTraits(recipe.getMaterialId(), HeadMaterialStats.ID);
        if (headStats.isPresent()) {
            // 545454
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.head")), 0, lineNumber * LINE_HEIGHT + LINE_OFFSET, BLACK);
            font.draw(poseStack, String.format("%s", headTrait.get(0).getId().getPath()), WIDTH - font.getSplitter().stringWidth(headTrait.get(0).getId().getPath()), lineNumber++ * LINE_HEIGHT + LINE_OFFSET, LIGHT_GRAY);
            font.draw(poseStack, String.format("%s%d", getPattern("tool_stat.tconstruct.durability"), headStats.get().getDurability()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s%s", getPattern("tool_stat.tconstruct.harvest_tier"), headStats.get().getTierId().getPath()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.mining_speed"), headStats.get().getMiningSpeed()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.attack_damage"), headStats.get().getAttack()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            lineNumber += 0.5f;
        }

        Optional<ExtraMaterialStats> extraStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), ExtraMaterialStats.ID);
        List<ModifierEntry> extraTrait = MaterialRegistry.getInstance().getTraits(recipe.getMaterialId(), ExtraMaterialStats.ID);
        if (extraStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.extra")), 0, lineNumber * LINE_HEIGHT + LINE_OFFSET, BLACK);
            font.draw(poseStack, String.format("%s", extraTrait.get(0).getId().getPath()), WIDTH - font.getSplitter().stringWidth(extraTrait.get(0).getId().getPath()), lineNumber++ * LINE_HEIGHT + LINE_OFFSET, LIGHT_GRAY);
            lineNumber += 0.5f;
        }

        Optional<HandleMaterialStats> handleStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), HandleMaterialStats.ID);
        List<ModifierEntry> handleTrait = MaterialRegistry.getInstance().getTraits(recipe.getMaterialId(), HandleMaterialStats.ID);
        if (handleStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.head")), 0, lineNumber * LINE_HEIGHT + LINE_OFFSET, BLACK);
            font.draw(poseStack, String.format("%s", handleTrait.get(0).getId().getPath()), WIDTH - font.getSplitter().stringWidth(handleTrait.get(0).getId().getPath()), lineNumber++ * LINE_HEIGHT + LINE_OFFSET, LIGHT_GRAY);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.durability"), handleStats.get().getDurability()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.attack_damage"), handleStats.get().getAttackDamage()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.attack_speed"), handleStats.get().getAttackSpeed()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.mining_speed"), handleStats.get().getMiningSpeed()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            lineNumber += 0.5f;
        }

        Optional<LimbMaterialStats> limbStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), LimbMaterialStats.ID);
        List<ModifierEntry> limbTrait = MaterialRegistry.getInstance().getTraits(recipe.getMaterialId(), LimbMaterialStats.ID);
        if (limbStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.limb")), 0, lineNumber * LINE_HEIGHT + LINE_OFFSET, BLACK);
            font.draw(poseStack, String.format("%s", limbTrait.get(0).getId().getPath()), WIDTH - font.getSplitter().stringWidth(limbTrait.get(0).getId().getPath()), lineNumber++ * LINE_HEIGHT + LINE_OFFSET, LIGHT_GRAY);
            font.draw(poseStack, String.format("%s%d", getPattern("tool_stat.tconstruct.durability"), limbStats.get().getDurability()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s+%.2f", getPattern("tool_stat.tconstruct.draw_speed"), limbStats.get().getDrawSpeed()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s+%.2f", getPattern("tool_stat.tconstruct.velocity"), limbStats.get().getVelocity()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s+%.2f", getPattern("tool_stat.tconstruct.accuracy"), limbStats.get().getDrawSpeed()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            lineNumber += 0.5f;
        }

        Optional<GripMaterialStats> gripStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), GripMaterialStats.ID);
        List<ModifierEntry> gripTrait = MaterialRegistry.getInstance().getTraits(recipe.getMaterialId(), GripMaterialStats.ID);
        if (gripStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.grip")), 0, lineNumber * LINE_HEIGHT + LINE_OFFSET, BLACK);
            font.draw(poseStack, String.format("%s", gripTrait.get(0).getId().getPath()), WIDTH - font.getSplitter().stringWidth(gripTrait.get(0).getId().getPath()), lineNumber++ * LINE_HEIGHT + LINE_OFFSET, LIGHT_GRAY);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.durability"), gripStats.get().getDurability()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s+%.2f", getPattern("tool_stat.tconstruct.accuracy"), gripStats.get().getAccuracy()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.attack_damage"), gripStats.get().getMeleeAttack()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, GRAY);
            lineNumber += 0.5f;
        }

        Optional<BowstringMaterialStats> stringStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), BowstringMaterialStats.ID);
        List<ModifierEntry> stringTrait = MaterialRegistry.getInstance().getTraits(recipe.getMaterialId(), BowstringMaterialStats.ID);
        if (stringStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.bowstring")), 0, lineNumber * LINE_HEIGHT + LINE_OFFSET, BLACK);
            font.draw(poseStack, String.format("%s", stringTrait.get(0).getId().getPath()), WIDTH - font.getSplitter().stringWidth(stringTrait.get(0).getId().getPath()), lineNumber++ * LINE_HEIGHT + LINE_OFFSET, LIGHT_GRAY);
            lineNumber += 0.5f;
        }

//        System.out.printf("x: %s y: %s\n", mouseX, mouseY);

    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolStatsWrapper recipe, IFocusGroup focuses) {
        if (!recipe.material.isCraftable()) {
            // float is in milli buckets
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 18, 0).addFluidStack(recipe.getFluidStack().getFluid(), 1000);
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addFluidStack(recipe.getFluidStack().getFluid(), 1000);
            builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addFluidStack(recipe.getFluidStack().getFluid(), 1000);
        }
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0).addItemStacks(recipe.getRepresentativeItems());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.getRepresentativeItems());
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStacks(recipe.getRepresentativeItems());

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, (WIDTH - 18), 0).addItemStacks(recipe.getParts());
    }

    @Override
    public List<Component> getTooltipStrings(ToolStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String materialName = recipe.getMaterialId().getPath();
        final Font font = Minecraft.getInstance().font;
        final int LINE_OFFSET = 20;
        final int LINE_HEIGHT = 10;
        float lineNumber = 0;

        if (((WIDTH - font.getSplitter().stringWidth(materialName)) / 2) <= mouseX && (mouseX <= ((WIDTH / 2) + font.getSplitter().stringWidth(materialName))) && 0 <= mouseY && mouseY <= 20) {
            return Collections.singletonList(new TranslatableComponent("material.tconstruct."+materialName+".flavor"));
        }


        Optional<HeadMaterialStats> headStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), HeadMaterialStats.ID);
//        Optional<ExtraMaterialStats> extraStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), ExtraMaterialStats.ID);
//        Optional<HandleMaterialStats> handleStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), HandleMaterialStats.ID);
//        Optional<LimbMaterialStats> limbStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), LimbMaterialStats.ID);
//        Optional<GripMaterialStats> gripStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), GripMaterialStats.ID);
//        Optional<BowstringMaterialStats> stringStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), BowstringMaterialStats.ID);
        List<ModifierEntry> headTrait = MaterialRegistry.getInstance().getTraits(recipe.getMaterialId(), HeadMaterialStats.ID);

        if (headStats.isPresent()) {
            int width = font.width(headTrait.get(0).getId().getPath());
            if (WIDTH - width <= mouseX && mouseX <= WIDTH && 20 <= mouseY && mouseY <= 30) {
                // TODO: add flavor text
                return Collections.singletonList(new TranslatableComponent("modifier.tconstruct."+headTrait.get(0).getId().getPath()+".description"));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Component getTitle() {
        return new TextComponent("Tool Stats");
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public IDrawable getIcon() {
        return ICON;
    }

    @Override
    public IDrawable getBackground() {
        return BACKGROUND;
    }

    @SuppressWarnings("removal")
    @Override
    public Class<? extends ToolStatsWrapper> getRecipeClass() {
        return ToolStatsWrapper.class;
    }

    @Override
    public RecipeType<ToolStatsWrapper> getRecipeType() {
        return RecipeType.create(MOD_ID, "tool_stats", getRecipeClass());
    }

}
