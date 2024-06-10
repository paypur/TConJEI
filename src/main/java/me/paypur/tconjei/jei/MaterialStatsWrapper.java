package me.paypur.tconjei.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.stats.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record MaterialStatsWrapper(Material material) {

    // taken from AbstractMaterialContent
    public List<ItemStack> getItemStacks() {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return Collections.emptyList();
        }
        List<ItemStack> repairStacks;
        // simply combine all items from all recipes
        MaterialVariantId variantId = MaterialVariantId.parse(material.getIdentifier().toString());
        repairStacks = RecipeHelper.getUIRecipes(world.getRecipeManager(), TinkerRecipeTypes.MATERIAL.get(), MaterialRecipe.class, recipe -> variantId.matchesVariant(recipe.getMaterial()))
                .stream()
                .flatMap(recipe -> Arrays.stream(recipe.getIngredient().getItems()))
                .collect(Collectors.toList());
        // no repair items? use the repair kit
        if (repairStacks.isEmpty()) {
            // bypass the valid check, because we need to show something
            repairStacks = Collections.singletonList(TinkerToolParts.repairKit.get().withMaterialForDisplay(variantId));
        }
        return repairStacks;
    }

    // taken from AbstractMaterialContent
    public FluidStack getFluidStack() {
        return MaterialCastingLookup.getCastingFluids(material.getIdentifier())
                .stream()
                .flatMap(recipe -> recipe.getFluids().stream())
                .findFirst()
                .orElse(FluidStack.EMPTY);
    }

    // taken from AbstractMaterialContent
    public List<ItemStack> getToolParts() {
        return RegistryHelper.getTagValueStream(Registry.ITEM, TinkerTags.Items.TOOL_PARTS)
                .filter(part -> part instanceof IToolPart)
                .map(part -> (IToolPart) part)
                .filter(part -> part.canUseMaterial(material))
                .map(part -> part.withMaterial(material.getIdentifier()))
                .collect(Collectors.toList());
    }

    public MaterialId getMaterialId() {
        return material.getIdentifier().getId();
    }

    public <T extends IMaterialStats> Optional<T> getStats(MaterialStatsId materialStatsId) {
        return MaterialRegistry.getInstance().getMaterialStats(getMaterialId(), materialStatsId);
    }

    public List<ModifierEntry> getTraits(MaterialStatsId materialStatsId) {
        return MaterialRegistry.getInstance().getTraits(material.getIdentifier().getId(), materialStatsId);
    }

    public boolean hasTraits() {
        List<MaterialStatsId> stats = List.of(HeadMaterialStats.ID, StatlessMaterialStats.BINDING.getIdentifier(), HandleMaterialStats.ID, LimbMaterialStats.ID, GripMaterialStats.ID, StatlessMaterialStats.BOWSTRING.getIdentifier());
        return stats.stream().anyMatch(stat -> !getTraits(stat).isEmpty());
    }

}
