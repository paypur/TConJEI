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
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.stats.*;

import java.util.List;
import java.util.Optional;

import static me.paypur.tconjei.TConJEI.MOD_ID;
import static net.minecraftforge.common.ForgeI18n.getPattern;

public class ToolStatsCategory implements IRecipeCategory<ToolStatsRecipe> {

    public static final int WIDTH = 176, HEIGHT = 256;
    public static ResourceLocation UID = new ResourceLocation(MOD_ID, "tool_stats");
    private final IDrawable BACKGROUND, ICON;

    public ToolStatsCategory(IGuiHelper guiHelper) {
        this.BACKGROUND = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.ICON = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/icon.png"), 0, 0, 16, 16);
    }

    @Override
    public void draw(ToolStatsRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        String materialName = recipe.material.getIdentifier().getId().getPath();
        Font font = Minecraft.getInstance().font;

        int materialColor = MaterialTooltipCache.getColor(recipe.material.getIdentifier()).getValue();

        font.drawShadow(poseStack, getPattern("material.tconstruct." + recipe.material.getIdentifier().getId().getPath()), (WIDTH - font.getSplitter().stringWidth(materialName)) / 2, 3, materialColor);

        Optional<HeadMaterialStats> headStats = MaterialRegistry.getInstance().getMaterialStats(recipe.material.getIdentifier(), HeadMaterialStats.ID);
        List<ModifierEntry> headTrait = MaterialRegistry.getInstance().getTraits(recipe.material.getIdentifier(), HeadMaterialStats.ID);
        if (headStats.isPresent()) {
            // 545454
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.head")), 0, 20, 8);
            font.draw(poseStack, String.format("%s", headTrait.get(0).getId().getPath()), WIDTH - font.getSplitter().stringWidth(headTrait.get(0).getId().getPath()), 20, 4144959);
            font.draw(poseStack, String.format("%s%d", getPattern("tool_stat.tconstruct.durability"), headStats.get().getDurability()), 0, 30, 4144959);
            font.draw(poseStack, String.format("%s%s", getPattern("tool_stat.tconstruct.harvest_tier"), headStats.get().getTierId().getPath()), 0, 40, 4144959);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.mining_speed"), headStats.get().getMiningSpeed()), 0, 50, 4144959);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.attack_damage"), headStats.get().getAttack()), 0, 60, 4144959);
        }

        Optional<ExtraMaterialStats> extraStats = MaterialRegistry.getInstance().getMaterialStats(recipe.material.getIdentifier(), ExtraMaterialStats.ID);
        List<ModifierEntry> extraTrait = MaterialRegistry.getInstance().getTraits(recipe.material.getIdentifier(), ExtraMaterialStats.ID);
        if (extraStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.extra")), 0, 80, 8);
            font.draw(poseStack, String.format("%s", extraTrait.get(0).getId().getPath()), WIDTH - font.getSplitter().stringWidth(extraTrait.get(0).getId().getPath()), 80, 4144959);
        }

        Optional<HandleMaterialStats> handleStats = MaterialRegistry.getInstance().getMaterialStats(recipe.material.getIdentifier(), HandleMaterialStats.ID);
        List<ModifierEntry> handleTrait = MaterialRegistry.getInstance().getTraits(recipe.material.getIdentifier(), HandleMaterialStats.ID);
        if (handleStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.head")), 0, 100, 8);
            font.draw(poseStack, String.format("%s", handleTrait.get(0).getId().getPath()), WIDTH - font.getSplitter().stringWidth(handleTrait.get(0).getId().getPath()), 100, 4144959);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.durability"), handleStats.get().getDurability()), 0, 110, 4144959);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.attack_damage"), handleStats.get().getAttackDamage()), 0, 120, 4144959);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.mining_speed"), handleStats.get().getMiningSpeed()), 0, 130, 4144959);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.attack_speed"), handleStats.get().getAttackSpeed()), 0, 140, 4144959);
        }

        Optional<LimbMaterialStats> limbStats = MaterialRegistry.getInstance().getMaterialStats(recipe.material.getIdentifier(), LimbMaterialStats.ID);
        if (limbStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.limb")), 0, 160, 8);
            font.draw(poseStack, String.format("%s%d", getPattern("tool_stat.tconstruct.durability"), limbStats.get().getDurability()), 0, 170, 4144959);
            font.draw(poseStack, String.format("%s+%.2f", getPattern("tool_stat.tconstruct.draw_speed"), limbStats.get().getDrawSpeed()), 0, 180, 4144959);
            font.draw(poseStack, String.format("%s+%.2f", getPattern("tool_stat.tconstruct.velocity"), limbStats.get().getVelocity()), 0, 190, 4144959);
            font.draw(poseStack, String.format("%s+%.2f", getPattern("tool_stat.tconstruct.accuracy"), limbStats.get().getDrawSpeed()), 0, 200, 4144959);
        }

        Optional<GripMaterialStats> gripStats = MaterialRegistry.getInstance().getMaterialStats(recipe.material.getIdentifier(), GripMaterialStats.ID);
        if (gripStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.grip")), 0, 220, 8);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.durability"), gripStats.get().getDurability()), 0, 230, 4144959);
            font.draw(poseStack, String.format("%s+%.2f", getPattern("tool_stat.tconstruct.accuracy"), gripStats.get().getAccuracy()), 0, 240, 4144959);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.attack_damage"), gripStats.get().getMeleeAttack()), 0, 250, 4144959);
        }

        Optional<BowstringMaterialStats> stringStats = MaterialRegistry.getInstance().getMaterialStats(recipe.material.getIdentifier(), BowstringMaterialStats.ID);
        if (stringStats.isPresent()) {
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.bowstring")), 0, 270, 8);
        }



    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ToolStatsRecipe recipe, IFocusGroup focuses) {
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
    public Class<? extends ToolStatsRecipe> getRecipeClass() {
        return ToolStatsRecipe.class;
    }

    @Override
    public RecipeType<ToolStatsRecipe> getRecipeType() {
        return RecipeType.create(MOD_ID, "tool_stats", getRecipeClass());
    }

}
