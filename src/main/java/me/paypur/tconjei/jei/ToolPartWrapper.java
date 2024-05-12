package me.paypur.tconjei.jei;

import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record ToolPartWrapper(ToolDefinition definition) {

    static List<Material> MATERIALS = MaterialRegistry.getMaterials()
            .stream()
            .map(material -> (Material) material)
            .filter(material -> !material.isHidden())
            .collect(Collectors.toList());

    public ItemStack getTool() {
        return StationSlotLayoutLoader.getInstance().get(definition.getId()).getIcon().getValue(ItemStack.class);
    }

    public List<LayoutSlot> getSlots() {
        return StationSlotLayoutLoader.getInstance().get(definition.getId()).getInputSlots();
    }

    public List<List<ItemStack>> getToolParts() {
        return definition.getData().getParts().stream()
            .map(PartRequirement::getPart)
            .map(part ->
                MATERIALS.stream()
                    .filter(part::canUseMaterial)
                    .map(material -> part.withMaterial(material.getIdentifier()))
                    .toList()
            ).toList();
    }

    public List<ItemStack> getToolRecipe() {
        List<IToolPart> parts = definition.getData().getParts()
                .stream()
                .map(PartRequirement::getPart)
                .toList();

        // ContentTool
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
             ItemStack item = parts.get(i).withMaterialForDisplay(ToolBuildHandler.getRenderMaterial(i));
             item.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
             items.add(item);
        }
        return items;
    }

}
