package me.paypur.tconjei.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.*;

public record ToolStatsWrapper(IMaterial material) {
    private static final IMaterialRegistry REGISTRY = MaterialRegistry.getInstance();

    public MaterialId getMaterialId() {
        return material.getIdentifier().getId();
    }

    // taken from AbstractMaterialContent
    public List<ItemStack> getInputs() {
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
                .toList();
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
    public List<ItemStack> getInputsParts(TagKey<Item> tag) {
        Set<Item> seen = new HashSet<>();
        return RegistryHelper.getTagValueStream(Registry.ITEM, tag)
            .filter(item -> item instanceof IModifiable)
            .flatMap(item -> ((IModifiable) item).getToolDefinition().getData().getParts()
                    .stream()
                    .filter(part -> part.canUseMaterial(material.getIdentifier()))
                    .map(part -> part.getPart().withMaterial(material.getIdentifier())))
            .filter(part -> seen.add(part.getItem()))
            .sorted(Comparator.comparing(a -> a.getItem().getDescriptionId()))
            .toList();
    }

    public <T extends BaseMaterialStats> Optional<T> getStats(MaterialStatsId statsId) {
        return REGISTRY.getMaterialStats(getMaterialId(), statsId);
    }

    public boolean hasStats(List<MaterialStatsId> statsIds) {
        return statsIds.stream().anyMatch(stat -> getStats(stat).isPresent());
    }

    public List<ModifierEntry> getTraits(MaterialStatsId statsId) {
        return REGISTRY.getTraits(material.getIdentifier().getId(), statsId);
    }

}
