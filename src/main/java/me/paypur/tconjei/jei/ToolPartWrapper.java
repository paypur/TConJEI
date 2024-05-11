package me.paypur.tconjei.jei;

import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import slimeknights.tconstruct.tools.TinkerTools;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record ToolPartWrapper(IToolPart toolPart) {

    static List<Material> MATERIALS = MaterialRegistry.getMaterials()
            .stream()
            .map(material -> (Material) material)
            .filter(material -> !material.isHidden())
            .collect(Collectors.toList());

    public List<ItemStack> getToolParts() {
        return MATERIALS.stream()
            .filter(toolPart::canUseMaterial)
            .map(material -> toolPart.withMaterial(material.getIdentifier()))
            .toList();
    }

    public List<ItemStack> getCraftableTools() {
        // AbstractToolDefinitionDataProvider
        List<ResourceLocation> toolResourceLocations = ToolDefinitionLoader.getInstance().getRegisteredToolDefinitions()
            .stream()
            .filter(tool -> tool.getData()
                    // match tool part to every tool it is used in
                    .getParts()
                    .stream()
                    .anyMatch(part -> part.matches((Item) toolPart)))
            .map(ToolDefinition::getId)
            .toList();

        List<ItemStack> toolIcons = toolResourceLocations.stream()
            .map(StationSlotLayoutLoader.getInstance()::get)
            .map(layout ->  layout.getIcon().getValue(ItemStack.class))
            .toList();

        return toolIcons;
    }

}
