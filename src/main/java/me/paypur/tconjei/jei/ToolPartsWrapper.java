package me.paypur.tconjei.jei;

import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.util.ArrayList;
import java.util.List;

public record ToolPartsWrapper(ToolDefinition definition) {

    static List<IMaterial> MATERIALS = MaterialRegistry.getMaterials()
            .stream()
            .filter(material -> !material.isHidden())
            .toList();

    public StationSlotLayout getSlotLayout() {
        return StationSlotLayoutLoader.getInstance().get(definition.getId());
    }

    public ItemStack getOutputTool() {
        return getSlotLayout().getIcon().getValue(ItemStack.class);
    }

    public List<LayoutSlot> getSlots() {
        return getSlotLayout().getInputSlots();
    }

    public boolean isBroadTool() {
        return getSlots().size() >= 4;
    }

    // a 2d list of each part and then each variant of that part
    public List<List<ItemStack>> getInputsParts() {
        return definition.getData().getParts().stream()
            .map(PartRequirement::getPart)
            .map(part -> MATERIALS.stream()
                    .filter(part::canUseMaterial)
                    .map(material -> part.withMaterial(material.getIdentifier()))
                    .toList()
            ).toList();
    }

    // use display parts to be more consistent
    public List<ItemStack> getDisplayParts() {
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
