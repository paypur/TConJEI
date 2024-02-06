package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
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
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.stats.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static me.paypur.tconjei.TConJEI.MOD_ID;
import static net.minecraftforge.common.ForgeI18n.getPattern;

public class MaterialStatsCategory implements IRecipeCategory<MaterialStatsWrapper> {

    final ResourceLocation UID = new ResourceLocation(MOD_ID, "material_stats");
    final Font font = Minecraft.getInstance().font;
    final IDrawable BACKGROUND, ICON;
    final int WIDTH = 164, HEIGHT = 240;
    final int LINE_OFFSET = 20;
    final int LINE_OFFSET_HOVER = LINE_OFFSET - 1;
    final int LINE_HEIGHT = 10;
    final int WHITE = 16777215;
    int MAIN_COLOR = 0;
    int STAT_COLOR = 5526612;
    int TRAIT_COLOR = 8289918;

    public MaterialStatsCategory(IGuiHelper guiHelper) {
        this.BACKGROUND = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.ICON = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/icon.png"), 0, 0, 16, 16);
        ResourceLocation palette = new ResourceLocation(MOD_ID, "textures/gui/palette.png");
        try {
            InputStream stream = Minecraft.getInstance().getResourceManager().getResource(palette).getInputStream();
            BufferedImage image = ImageIO.read(stream);
            this.MAIN_COLOR = image.getRGB(0, 0);
            this.STAT_COLOR = image.getRGB(1, 0);
            this.TRAIT_COLOR = image.getRGB(0, 1);
        } catch (IOException e) {
            LogUtils.getLogger().error("Error loading palette", e);
        }
    }

    @Override
    public void draw(MaterialStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(recipe.getMaterialId()).getValue();
        final String materialNamespace = recipe.getMaterialId().getNamespace();
        final String materialPath = recipe.getMaterialId().getPath();

        float lineNumber = 0;
        font.drawShadow(poseStack, getPattern(String.format("material.%s.%s", materialNamespace, materialPath)), (WIDTH - font.getSplitter().stringWidth(materialPath)) / 2, 4, MATERIAL_COLOR);

        Optional<HeadMaterialStats> headStats = recipe.getMaterialStats(HeadMaterialStats.ID);
        Optional<ExtraMaterialStats> extraStats = recipe.getMaterialStats(ExtraMaterialStats.ID);
        Optional<HandleMaterialStats> handleStats = recipe.getMaterialStats(HandleMaterialStats.ID);
        Optional<LimbMaterialStats> limbStats = recipe.getMaterialStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripStats = recipe.getMaterialStats(GripMaterialStats.ID);
        Optional<BowstringMaterialStats> stringStats = recipe.getMaterialStats(BowstringMaterialStats.ID);

        if (headStats.isPresent()) {
            drawTraits(poseStack, recipe.getMaterialId(), HeadMaterialStats.ID, lineNumber);
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.head")), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MAIN_COLOR);
            font.draw(poseStack, String.format("%s%d", getPattern("tool_stat.tconstruct.durability"), headStats.get().getDurability()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%s", getPattern("tool_stat.tconstruct.harvest_tier"), getPattern("stat.tconstruct.harvest_tier.minecraft." + headStats.get().getTierId().getPath())), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.mining_speed"), headStats.get().getMiningSpeed()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.attack_damage"), headStats.get().getAttack()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            lineNumber += 0.5f;
        }

        if (extraStats.isPresent()) {
            drawTraits(poseStack, recipe.getMaterialId(), ExtraMaterialStats.ID, lineNumber);
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.extra")), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MAIN_COLOR);
            lineNumber += 0.5f;
        }

        if (handleStats.isPresent()) {
            drawTraits(poseStack, recipe.getMaterialId(), HandleMaterialStats.ID, lineNumber);
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.handle")), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MAIN_COLOR);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.durability"), handleStats.get().getDurability()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.attack_damage"), handleStats.get().getAttackDamage()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.attack_speed"), handleStats.get().getAttackSpeed()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.mining_speed"), handleStats.get().getMiningSpeed()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            lineNumber += 0.5f;
        }

        if (limbStats.isPresent()) {
            drawTraits(poseStack, recipe.getMaterialId(), LimbMaterialStats.ID, lineNumber);
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.limb")), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MAIN_COLOR);
            font.draw(poseStack, String.format("%s%d", getPattern("tool_stat.tconstruct.durability"), limbStats.get().getDurability()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%s%.2f", getPattern("tool_stat.tconstruct.draw_speed"), limbStats.get().getDrawSpeed() >= 0 ? "+" : "", limbStats.get().getDrawSpeed()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%s%.2f", getPattern("tool_stat.tconstruct.velocity"), limbStats.get().getVelocity() >= 0 ? "+" : "", limbStats.get().getVelocity()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%s%.2f", getPattern("tool_stat.tconstruct.accuracy"), limbStats.get().getDrawSpeed() >= 0 ? "+" : "", limbStats.get().getDrawSpeed()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            lineNumber += 0.5f;
        }

        if (gripStats.isPresent()) {
            drawTraits(poseStack, recipe.getMaterialId(), GripMaterialStats.ID, lineNumber);
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.grip")), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MAIN_COLOR);
            font.draw(poseStack, String.format("%s%.2fx", getPattern("tool_stat.tconstruct.durability"), gripStats.get().getDurability()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%s%.2f", getPattern("tool_stat.tconstruct.accuracy"), gripStats.get().getAccuracy() >= 0 ? "+" : "", gripStats.get().getAccuracy()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            font.draw(poseStack, String.format("%s%.2f", getPattern("tool_stat.tconstruct.attack_damage"), gripStats.get().getMeleeAttack()), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, STAT_COLOR);
            lineNumber += 0.5f;
        }

        if (stringStats.isPresent()) {
            drawTraits(poseStack, recipe.getMaterialId(), BowstringMaterialStats.ID, lineNumber);
            font.draw(poseStack, String.format("[%s]", getPattern("stat.tconstruct.bowstring")), 0, lineNumber * LINE_HEIGHT + LINE_OFFSET, MAIN_COLOR);
        }

    }

    private void drawTraits(PoseStack poseStack, MaterialId materialId, MaterialStatsId materialStatsId, float lineNumber) {
        List<ModifierEntry> traits = MaterialRegistry.getInstance().getTraits(materialId, materialStatsId);
        for (ModifierEntry trait : traits) {
            String pattern = getPattern(String.format("modifier.%s.%s", trait.getId().getNamespace(), trait.getId().getPath()));
            font.draw(poseStack, String.format("%s", pattern), WIDTH - font.getSplitter().stringWidth(pattern), lineNumber++ * LINE_HEIGHT + LINE_OFFSET, TRAIT_COLOR);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MaterialStatsWrapper recipe, IFocusGroup focuses) {
        if (!recipe.material.isCraftable()) {
            final int BUCKET = 1000; // milli buckets
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 18, 0).addFluidStack(recipe.getFluidStack().getFluid(), BUCKET);
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addFluidStack(recipe.getFluidStack().getFluid(), BUCKET);
            builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addFluidStack(recipe.getFluidStack().getFluid(), BUCKET);
        }
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0).addItemStacks(recipe.getItemStacks());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.getItemStacks());
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStacks(recipe.getItemStacks());

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, (WIDTH - 16), 0).addItemStacks(recipe.getParts());
    }

    @Override
    public List<Component> getTooltipStrings(MaterialStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String materialNamespace = recipe.getMaterialId().getNamespace();
        final String materialPath = recipe.getMaterialId().getPath();

        float lineNumber = 0;

        int matWidth = font.width(materialPath);
        if (inBox(mouseX, mouseY, (WIDTH - matWidth) / 2, 3, matWidth, LINE_HEIGHT)) {
            return Collections.singletonList(new TranslatableComponent(String.format("material.%s.%s.flavor", materialNamespace, materialPath)).setStyle(Style.EMPTY.withItalic(true).withColor(WHITE)));
        }

        Optional<HeadMaterialStats> headStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), HeadMaterialStats.ID);
        Optional<ExtraMaterialStats> extraStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), ExtraMaterialStats.ID);
        Optional<HandleMaterialStats> handleStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), HandleMaterialStats.ID);
        Optional<LimbMaterialStats> limbStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), GripMaterialStats.ID);
        Optional<BowstringMaterialStats> stringStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), BowstringMaterialStats.ID);

        List<Component> components = Collections.emptyList();

        if (headStats.isPresent()) {
            components = getTooltips(recipe.getMaterialId(), BowstringMaterialStats.ID, mouseX, mouseY, lineNumber);
            if (!components.isEmpty()) {
                return components;
            }
            lineNumber += 5.5f;
        }

        if (extraStats.isPresent()) {
            components = getTooltips(recipe.getMaterialId(), BowstringMaterialStats.ID, mouseX, mouseY, lineNumber);
            if (!components.isEmpty()) {
                return components;
            }
            lineNumber += 1.5f;
        }

        if (handleStats.isPresent()) {
            components = getTooltips(recipe.getMaterialId(), BowstringMaterialStats.ID, mouseX, mouseY, lineNumber);
            if (!components.isEmpty()) {
                return components;
            }
            lineNumber += 5.5f;
        }

        if (limbStats.isPresent()) {
            components = getTooltips(recipe.getMaterialId(), BowstringMaterialStats.ID, mouseX, mouseY, lineNumber);
            if (!components.isEmpty()) {
                return components;
            }
            lineNumber += 5.5f;
        }

        if (gripStats.isPresent()) {
            components = getTooltips(recipe.getMaterialId(), BowstringMaterialStats.ID, mouseX, mouseY, lineNumber);
            if (!components.isEmpty()) {
                return components;
            }
            lineNumber += 4.5f;
        }

        if (stringStats.isPresent()) {
            components = getTooltips(recipe.getMaterialId(), BowstringMaterialStats.ID, mouseX, mouseY, lineNumber);
            if (!components.isEmpty()) {
                return components;
            }
        }

        return components;
    }

    private List<Component> getTooltips(MaterialId materialId, MaterialStatsId materialStatsId, double mouseX, double mouseY, float lineNumber) {
        List<ModifierEntry> traits = MaterialRegistry.getInstance().getTraits(materialId, materialStatsId);
        for (ModifierEntry trait : traits) {
            String namespace = trait.getId().getNamespace();
            String path = trait.getId().getPath();
            String pattern = getPattern(String.format("modifier.%s.%s", namespace, path));
            int textWidth = font.width(pattern);
            if (inBox(mouseX, mouseY, WIDTH - textWidth, (int) (lineNumber * LINE_HEIGHT + LINE_OFFSET_HOVER), textWidth, LINE_HEIGHT)) {
                return List.of(new TranslatableComponent(String.format("modifier.%s.%s.flavor", namespace, path)).setStyle(Style.EMPTY.withItalic(true).withColor(WHITE)),
                        new TranslatableComponent(String.format("modifier.%s.%s.description", namespace, path)));
            }
            lineNumber += 1f;
        }
        return Collections.emptyList();
    }

    private boolean inBox(double mX, double mY, int x, int y, int w, int h) {
        return (x <= mX && mX <= x + w && y <= mY && mY <= y + h);
    }

    @Override
    public Component getTitle() {
        return new TextComponent("Material Stats");
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
    public Class<? extends MaterialStatsWrapper> getRecipeClass() {
        return MaterialStatsWrapper.class;
    }

    @Override
    public RecipeType<MaterialStatsWrapper> getRecipeType() {
        return RecipeType.create(MOD_ID, "material_stats", getRecipeClass());
    }

}
