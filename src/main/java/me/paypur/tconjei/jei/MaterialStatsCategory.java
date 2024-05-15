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
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.stats.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.paypur.tconjei.ColorManager.getShade;
import static me.paypur.tconjei.TConJEI.MOD_ID;
import static me.paypur.tconjei.TConJEI.inBox;
import static net.minecraftforge.common.ForgeI18n.getPattern;

public class MaterialStatsCategory implements IRecipeCategory<MaterialStatsWrapper> {

    final ResourceLocation UID = new ResourceLocation(MOD_ID, "material_stats");
    final Font font = Minecraft.getInstance().font;
    final IDrawable BACKGROUND, ICON;
    final int WIDTH = 164, HEIGHT = 222;
    final int LINE_OFFSET = 20;
    final int LINE_OFFSET_HOVER = LINE_OFFSET - 1;
    final int LINE_HEIGHT = 10;
    final int WHITE = 16777215; //ffffff
    int TEXT_COLOR = 8289918; //7e7e7e
    int DURABILITY_COLOR = 4639302; //46ca46
    int MINING_COLOR = 7839435; //779ecb
    int ATTACK_COLOR = 13919075; //d46363

    public MaterialStatsCategory(IGuiHelper guiHelper) {
        this.BACKGROUND = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.ICON = guiHelper.createDrawable(new ResourceLocation(MOD_ID, "textures/gui/materialstats/icon.png"), 0, 0, 16, 16);
        try {
            ResourceLocation palette = new ResourceLocation(MOD_ID, "textures/gui/palette.png");
            InputStream stream = Minecraft.getInstance().getResourceManager().getResource(palette).getInputStream();
            BufferedImage image = ImageIO.read(stream);
            this.TEXT_COLOR = image.getRGB(0, 0);
            this.DURABILITY_COLOR = image.getRGB(1, 0);
            this.MINING_COLOR = image.getRGB(0, 1);
            this.ATTACK_COLOR = image.getRGB(1, 1);
        } catch (IOException e) {
            LogUtils.getLogger().error("Error loading palette", e);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MaterialStatsWrapper recipe, IFocusGroup focuses) {
        FluidStack fluidStack = recipe.getFluidStack();
        if (!fluidStack.isEmpty()) {
            final int BUCKET = 1000; // milli buckets
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 18, 0).addFluidStack(fluidStack.getFluid(), BUCKET);
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addFluidStack(fluidStack.getFluid(), BUCKET);
        }
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 0).addItemStacks(recipe.getItemStacks());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.getItemStacks());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, WIDTH - 16, 0).addItemStacks(recipe.getToolParts());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(recipe.getToolParts());
    }

    @Override
    public void draw(MaterialStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        final String materialName = getPattern(String.format("material.%s.%s", recipe.getMaterialId().getNamespace(), recipe.getMaterialId().getPath()));
        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(recipe.getMaterialId()).getValue();
        float lineNumber = 0;
        // Name
        drawShadow(stack, materialName, (WIDTH - font.width(materialName)) / 2f, 4, MATERIAL_COLOR);
        Optional<HeadMaterialStats> headStats = recipe.getStats(HeadMaterialStats.ID);
        Optional<ExtraMaterialStats> extraStats = recipe.getStats(ExtraMaterialStats.ID);
        Optional<HandleMaterialStats> handleStats = recipe.getStats(HandleMaterialStats.ID);
        Optional<LimbMaterialStats> limbStats = recipe.getStats(LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripStats = recipe.getStats(GripMaterialStats.ID);
        Optional<BowstringMaterialStats> stringStats = recipe.getStats(BowstringMaterialStats.ID);
        // HEAD
        if (headStats.isPresent()) {
            String miningLevel = headStats.get().getTierId().getPath();
            drawTraits(stack, recipe, HeadMaterialStats.ID, lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.head")), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MATERIAL_COLOR);
            drawStats(stack, "tool_stat.tconstruct.durability", String.valueOf(headStats.get().getDurability()), lineNumber++, DURABILITY_COLOR);
            drawStats(stack, "tool_stat.tconstruct.harvest_tier", getPattern("stat.tconstruct.harvest_tier.minecraft." + miningLevel), lineNumber++, getMiningLevelColor(miningLevel));
            drawStats(stack, "tool_stat.tconstruct.mining_speed", String.format("%.2f", headStats.get().getMiningSpeed()), lineNumber++, MINING_COLOR);
            drawStats(stack, "tool_stat.tconstruct.attack_damage", String.format("%.2f", headStats.get().getAttack()), lineNumber++, ATTACK_COLOR);
            lineNumber += 0.5f;
        }
        // EXTRA
        // only draw extra if others don't exist
        else if (extraStats.isPresent()) {
            drawTraits(stack, recipe, ExtraMaterialStats.ID, lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.extra")), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MATERIAL_COLOR);
            lineNumber += 0.5f;
        }
        // HANDLE
        if (handleStats.isPresent()) {
            drawTraits(stack, recipe, HandleMaterialStats.ID, lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.handle")), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MATERIAL_COLOR);
            drawStats(stack, "tool_stat.tconstruct.durability", String.format("%.2fx", handleStats.get().getDurability()), lineNumber++, getMultiplierColor(handleStats.get().getDurability()));
            drawStats(stack, "tool_stat.tconstruct.attack_damage", String.format("%.2fx", handleStats.get().getAttackDamage()), lineNumber++, getMultiplierColor(handleStats.get().getAttackDamage()));
            drawStats(stack, "tool_stat.tconstruct.attack_speed", String.format("%.2fx", handleStats.get().getAttackSpeed()), lineNumber++, getMultiplierColor(handleStats.get().getAttackSpeed()));
            drawStats(stack, "tool_stat.tconstruct.mining_speed", String.format("%.2fx", handleStats.get().getMiningSpeed()), lineNumber++, getMultiplierColor(handleStats.get().getMiningSpeed()));
            lineNumber += 0.5f;
        }
        // LIMB
        if (limbStats.isPresent()) {
            drawTraits(stack, recipe, LimbMaterialStats.ID, lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.limb")), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MATERIAL_COLOR);
            drawStats(stack, "tool_stat.tconstruct.durability", String.valueOf(limbStats.get().getDurability()), lineNumber++, DURABILITY_COLOR);
            drawStats(stack, "tool_stat.tconstruct.draw_speed", signedString(limbStats.get().getDrawSpeed()), lineNumber++, getDifferenceColor(limbStats.get().getDrawSpeed()));
            drawStats(stack, "tool_stat.tconstruct.velocity", signedString(limbStats.get().getVelocity()), lineNumber++, getDifferenceColor(limbStats.get().getVelocity()));
            drawStats(stack, "tool_stat.tconstruct.accuracy", signedString(limbStats.get().getAccuracy()), lineNumber++, getDifferenceColor(limbStats.get().getAccuracy()));
            lineNumber += 0.5f;
        }
        // GRIP
        if (gripStats.isPresent()) {
            drawTraits(stack, recipe, GripMaterialStats.ID, lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.grip")), 0, lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MATERIAL_COLOR);
            drawStats(stack, "tool_stat.tconstruct.durability", String.format("%.2fx", gripStats.get().getDurability()), lineNumber++, getMultiplierColor(gripStats.get().getDurability()));
            drawStats(stack, "tool_stat.tconstruct.accuracy", signedString(gripStats.get().getAccuracy()), lineNumber++, getDifferenceColor(gripStats.get().getAccuracy()));
            drawStats(stack, "tool_stat.tconstruct.attack_damage", String.format("%.2f", gripStats.get().getMeleeAttack()), lineNumber++, ATTACK_COLOR);
            lineNumber += 0.5f;
        }
        // STRING
        if (stringStats.isPresent()) {
            drawTraits(stack, recipe, BowstringMaterialStats.ID, lineNumber);
            drawShadow(stack, String.format("[%s]", getPattern("stat.tconstruct.bowstring")), 0, lineNumber * LINE_HEIGHT + LINE_OFFSET, MATERIAL_COLOR);
        }

    }

    @Override
    public List<Component> getTooltipStrings(MaterialStatsWrapper recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final String materialNamespace = recipe.getMaterialId().getNamespace();
        final String materialPath = recipe.getMaterialId().getPath();
        float lineNumber = 0;

        // TRAIT
        int matWidth = font.width(materialPath);
        if (inBox(mouseX, mouseY, (WIDTH - matWidth) / 2f, 3, matWidth, LINE_HEIGHT)) {
            return List.of(new TranslatableComponent(String.format("material.%s.%s.flavor", materialNamespace, materialPath)).setStyle(Style.EMPTY.withItalic(true).withColor(WHITE)));
        }

        Optional<HeadMaterialStats> headStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), HeadMaterialStats.ID);
        Optional<ExtraMaterialStats> extraStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), ExtraMaterialStats.ID);
        Optional<HandleMaterialStats> handleStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), HandleMaterialStats.ID);
        Optional<LimbMaterialStats> limbStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), LimbMaterialStats.ID);
        Optional<GripMaterialStats> gripStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), GripMaterialStats.ID);
        Optional<BowstringMaterialStats> stringStats = MaterialRegistry.getInstance().getMaterialStats(recipe.getMaterialId(), BowstringMaterialStats.ID);
        // HEAD
        if (headStats.isPresent()) {
            Optional<List<Component>> component = Stream.of(
                    getTraitTooltips(recipe, HeadMaterialStats.ID, mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.harvest_tier", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.mining_speed", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.attack_damage", mouseX, mouseY, lineNumber++))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
            lineNumber += 0.5f;
        }
        // EXTRA
        // only draw extra if others don't exist
        else if (extraStats.isPresent()) {
            List<Component> component = getTraitTooltips(recipe, ExtraMaterialStats.ID, mouseX, mouseY, lineNumber++);
            if (!component.isEmpty()) {
                return component;
            }
            lineNumber += 0.5f;
        }
        // HANDLE
        if (handleStats.isPresent()) {
            Optional<List<Component>> component = Stream.of(
                    getTraitTooltips(recipe, HandleMaterialStats.ID, mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.attack_damage", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.attack_speed", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.mining_speed", mouseX, mouseY, lineNumber++))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
            lineNumber += 0.5f;
        }
        // LIMB
        if (limbStats.isPresent()) {
            Optional<List<Component>> component = Stream.of(
            getTraitTooltips(recipe, LimbMaterialStats.ID, mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                    getStatTooltip( "tool_stat.tconstruct.draw_speed", mouseX, mouseY, lineNumber++),
                    getStatTooltip( "tool_stat.tconstruct.velocity", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.accuracy", mouseX, mouseY, lineNumber++))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
            lineNumber += 0.5f;
        }
        // GRIP
        if (gripStats.isPresent()) {
            Optional<List<Component>> component = Stream.of(
                    getTraitTooltips(recipe, GripMaterialStats.ID, mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.durability", mouseX, mouseY, lineNumber++),
                    getStatTooltip("tool_stat.tconstruct.accuracy", mouseX, mouseY, lineNumber++),
                    getStatTooltip( "tool_stat.tconstruct.attack_damage", mouseX, mouseY, lineNumber++))
                    .filter(list -> !list.isEmpty())
                    .findFirst();
            if (component.isPresent()) {
                return component.get();
            }
            lineNumber += 0.5f;
        }
        // STRING
        if (stringStats.isPresent()) {
            List<Component> component = getTraitTooltips(recipe, BowstringMaterialStats.ID, mouseX, mouseY, lineNumber);
            if (!component.isEmpty()) {
                return component;
            }
        }

        return Collections.emptyList();
    }

    private void drawStats(PoseStack poseStack, String type, String stat, float lineNumber, int ACCENT_COLOR) {
        String pattern = getPattern(type);
        float width = font.getSplitter().stringWidth(pattern);
        font.draw(poseStack, pattern, 0, lineNumber * LINE_HEIGHT + LINE_OFFSET, TEXT_COLOR);
        font.draw(poseStack, stat, width, lineNumber * LINE_HEIGHT + LINE_OFFSET, ACCENT_COLOR);
    }

    private void drawShadow(PoseStack poseStack, String string, float x, float y, int color) {
        font.draw(poseStack, string, x + 0.625f, y + 0.625f, getShade(color, 5));
        font.draw(poseStack, string, x, y, color);
    }

    private void drawTraits(PoseStack poseStack, MaterialStatsWrapper statsWrapper, MaterialStatsId statsId, float lineNumber) {
        final int MATERIAL_COLOR = MaterialTooltipCache.getColor(statsWrapper.getMaterialId()).getValue();
        for (ModifierEntry trait : statsWrapper.getTraits(statsId)) {
            String pattern = getPattern(String.format("modifier.%s.%s", trait.getId().getNamespace(), trait.getId().getPath()));
            drawShadow(poseStack, pattern, WIDTH - font.getSplitter().stringWidth(pattern), lineNumber++ * LINE_HEIGHT + LINE_OFFSET, MATERIAL_COLOR);
        }
    }

    private List<Component> getStatTooltip(String pattern, double mouseX, double mouseY, float lineNumber) {
        String string = getPattern(pattern);
        int textWidth = font.width(string);
        if (inBox(mouseX, mouseY, 0, lineNumber * LINE_HEIGHT + LINE_OFFSET_HOVER, textWidth, LINE_HEIGHT)) {
            return List.of(new TranslatableComponent(pattern + ".description"));
        }
        return Collections.emptyList();
    }

    private List<Component> getTraitTooltips(MaterialStatsWrapper statsWrapper, MaterialStatsId statsId, double mouseX, double mouseY, float lineNumber) {
        for (ModifierEntry trait : statsWrapper.getTraits(statsId)) {
            String namespace = trait.getId().getNamespace();
            String path = trait.getId().getPath();
            String pattern = getPattern(String.format("modifier.%s.%s", namespace, path));
            int textWidth = font.width(pattern);
            if (inBox(mouseX, mouseY, WIDTH - textWidth, lineNumber++ * LINE_HEIGHT + LINE_OFFSET_HOVER, textWidth, LINE_HEIGHT)) {
                return List.of(new TranslatableComponent(String.format("modifier.%s.%s.flavor", namespace, path)).setStyle(Style.EMPTY.withItalic(true).withColor(WHITE)),
                        new TranslatableComponent(String.format("modifier.%s.%s.description", namespace, path)));
            }
        }
        return Collections.emptyList();
    }

    private int getMiningLevelColor(String miningLevel) {
        return switch (miningLevel) {
            case "wood" -> 9200923;
            case "gold" -> 16558080;
            case "stone" -> 9934743;
            case "iron" -> 14342874; // default color 13158600 is not visible in light mode
            case "diamond" -> 5569788;
            case "netherite" -> 4997443;
            default -> TEXT_COLOR;
        };
    }

    // @formatter:off
    // TODO: found colors in assets/tconstruct/mantle/colors.json
    private int getMultiplierColor(Float f) {
        if (f < 0.55f) { return 12386304; } //bd0000
        if (f < 0.60f) { return 12396032; } //bd2600
        if (f < 0.65f) { return 12405504; } //bd4b00
        if (f < 0.70f) { return 12415232; } //bd7100
        if (f < 0.75f) { return 12424960; } //bd9700
        if (f < 0.80f) { return 12434688; } //bdbd00
        if (f < 0.85f) { return 9944320; } //97bd00
        if (f < 0.90f) { return 7453952; } //71bd00
        if (f < 0.95f) { return 4963584; } //4bbd00
        if (f < 1.00f) { return 2538752; } //26bd00
        if (f < 1.05f) { return 48384; } //00bd00
        if (f < 1.10f) { return 48422; } //00bd26
        if (f < 1.15f) { return 48459; } //00bd4b
        if (f < 1.20f) { return 48497; } //00bd71
        if (f < 1.25f) { return 48535; } //00bd97
        if (f < 1.30f) { return 48573; } //00bdbd
        if (f < 1.35f) { return 38845; } //0097bd
        if (f < 1.4f) { return 29117; } //0071bd
        return 19389; //004bbd
    }
    // @formatter:on

    private int getDifferenceColor(Float f) {
        return getMultiplierColor(f + 1f);
    }

    private String signedString(float f) {
        return String.format("%s%.2f", f >= 0 ? "+" : "", f);
    }


    @Override
    public Component getTitle() {
        return new TextComponent("Material Stats");
    }

    @Override
    public IDrawable getBackground() {
        return this.BACKGROUND;
    }

    @Override
    public IDrawable getIcon() {
        return this.ICON;
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getUid() {
        return this.UID;
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
