package me.paypur.tconjei.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ToolStatsRecipe {


    public final IMaterial material;
    private final IDrawable slot;

    public ToolStatsRecipe(IMaterial material, IGuiHelper guiHelper) {
        this.material = material;
        this.slot = guiHelper.getSlotDrawable();
    }

    public void getIngredients(IIngredients ingredients) {

        ingredients.setInputs(VanillaTypes.ITEM, getRepresentatives());
        ingredients.setOutputs(VanillaTypes.ITEM, getRepresentatives());

        if (!material.isCraftable()) {
            ingredients.setInputs(ForgeTypes.FLUID, Collections.singletonList(getFluidStack(material))));
            ingredients.setOutputs(ForgeTypes.FLUID, Collections.singletonList(getFluidStack(material)));
        }

    }

    public List<ItemStack> getRepresentatives() {

        ArrayList<ItemStack> list = new ArrayList<ItemStack>();

//        if (!material.isCraftable()) {
//            for (MeltingRecipe recipe : MaterialRegistry.getMaterials().getAllMeltingRecipes())
//                if (getFluid(material).equals(getFluid(recipe.output)))
//                    list.addAll(recipe.input.getInputs());
//        }
        if (material.getRepresentativeItem() != null && !material.getRepresentativeItem().isEmpty())
            list.add(material.getRepresentativeItem());
        list.addAll(getParts());

        return list;

    }

    public List<ItemStack> getParts() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (IToolPart part : getToolParts()) {
            if (part.canUseMaterial(material)) {
                ItemStack stack = part.withMaterial(material.getIdentifier());
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
    private FluidStack getFluidStack(IMaterial material) {
        return MaterialCastingLookup.getCastingFluids(material.getIdentifier())
                .stream()
                .flatMap(recipe -> recipe.getFluids().stream())
                .findFirst().orElse(FluidStack.EMPTY);
    }

    private void getAllMeltingRecipes(IMaterial material) {
        return RegistryHelper.getTagValueStream(Registry)
    }

}
