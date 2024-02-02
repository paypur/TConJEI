package me.paypur.tconjei.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.*;
import java.util.stream.Collectors;

public class ToolStatsWrapper {

    public final Material material;

    public ToolStatsWrapper(Material material) {
        this.material = material;
    }

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

    // taken from AbstractMaterialContent
    public FluidStack getFluidStack() {
        return MaterialCastingLookup.getCastingFluids(material.getIdentifier())
                .stream()
                .flatMap(recipe -> recipe.getFluids().stream())
                .findFirst().orElse(FluidStack.EMPTY);
    }

    public List<ItemStack> getParts() {
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

    public MaterialId getMaterialId() {
        return  material.getIdentifier().getId();
    }

}
