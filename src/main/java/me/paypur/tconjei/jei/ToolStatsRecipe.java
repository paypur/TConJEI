package me.paypur.tconjei.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.*;
import java.util.stream.Collectors;

public class ToolStatsRecipe implements IRecipeCategoryExtension {


    public final Material material;
    private final IDrawable slot;

    public ToolStatsRecipe(Material material, IGuiHelper guiHelper) {
        this.material = material;
        this.slot = guiHelper.getSlotDrawable();
    }

//    @SuppressWarnings("removal")
//    @Override
//    public void setIngredients(IIngredients ingredients) {
//
//        ingredients.setInputs(VanillaTypes.ITEM, getRepresentatives());
//        ingredients.setOutputs(VanillaTypes.ITEM, getRepresentatives());
//
//        if (!material.isCraftable()) {
//            ingredients.setInputs(ForgeTypes.FLUID, Collections.singletonList(getFluidStack()));
//            ingredients.setOutputs(ForgeTypes.FLUID, Collections.singletonList(getFluidStack()));
//        }
//
//    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, PoseStack stack, double mouseX, double mouseY) {
        IRecipeCategoryExtension.super.drawInfo(recipeWidth, recipeHeight, stack, mouseX, mouseY);
    }

    private List<ItemStack> getRepresentatives() {

        ArrayList<ItemStack> list = new ArrayList<ItemStack>();

//        if (!material.isCraftable()) {
//            for (MeltingRecipe recipe : MaterialRegistry.getMaterials().getAllMeltingRecipes())
//                if (getFluid(material).equals(getFluid(recipe.output)))
//                    list.addAll(recipe.input.getInputs());
//        }

//        if (material.getRepresentativeItem() != null && !material.getRepresentativeItem().isEmpty())
        list.addAll(getRepresentativeItems());
        list.addAll(getParts());

        return list;

    }

    private List<ItemStack> getParts() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (IToolPart part : getToolParts()) {
            if (part.canUseMaterial(material)) {
                list.add(part.withMaterial(material.getIdentifier()));
            }
        }
        return list;
    }

    // taken from AbstractMaterialContent
    private List<IToolPart> getToolParts() {
        return RegistryHelper.getTagValueStream(Registry.ITEM, TinkerTags.Items.TOOL_PARTS)
                .filter(item -> item instanceof IToolPart)
                .map(item -> (IToolPart) item)
                .collect(Collectors.toList());
    }

    // taken from AbstractMaterialContent
    private FluidStack getFluidStack() {
        return MaterialCastingLookup.getCastingFluids(material.getIdentifier())
                .stream()
                .flatMap(recipe -> recipe.getFluids().stream())
                .findFirst().orElse(FluidStack.EMPTY);
    }

//    private void getAllMeltingRecipes(IMaterial material) {
//        return RegistryHelper.getTagValueStream(Registry)
//    }

    // taken from AbstractMaterialContent
    public List<ItemStack> getRepresentativeItems() {
        List<ItemStack> repairStacks;
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return Collections.emptyList();
        }
        // simply combine all items from all recipes
        MaterialVariantId materialVariantId = MaterialVariantId.parse(material.getIdentifier().toString());
        repairStacks = RecipeHelper.getUIRecipes(world.getRecipeManager(), TinkerRecipeTypes.MATERIAL.get(), MaterialRecipe.class, recipe -> materialVariantId.matchesVariant(recipe.getMaterial()))
                .stream()
                .flatMap(recipe -> Arrays.stream(recipe.getIngredient().getItems()))
                .collect(Collectors.toList());
        // no repair items? use the repair kit
        if (repairStacks.isEmpty()) {
            // bypass the valid check, because we need to show something
            repairStacks = Collections.singletonList(TinkerToolParts.repairKit.get().withMaterialForDisplay(materialVariantId));
        }
        return repairStacks;
    }

}
