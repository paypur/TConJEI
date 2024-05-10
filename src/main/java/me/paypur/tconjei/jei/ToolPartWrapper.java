package me.paypur.tconjei.jei;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.util.List;
import java.util.stream.Collectors;

public record ToolPartWrapper(IToolPart toolPart) {

    static List<Material> MATERIALS = MaterialRegistry.getMaterials()
            .stream()
            .map(material -> (Material) material)
            .filter(material -> !material.isHidden())
            .collect(Collectors.toList());
//            .filter(material -> material.toString().equals("Material{tconstruct:iron}"))
//            .findFirst()
//            .get();


    public List<ItemStack> getToolParts() {
        return MATERIALS.stream()
                        .filter(toolPart::canUseMaterial)
                        .map(material -> toolPart.withMaterial(material.getIdentifier()))
                        .collect(Collectors.toList());
    }

    public List<ResourceLocation> getCraftableTools() {
        return ToolDefinitionLoader.getInstance().getRegisteredToolDefinitions()
            .stream()
            .filter(tool -> tool.getData()
                    .getParts()
                    .stream()
                    .anyMatch(part -> part.matches((Item) toolPart)))
            .map(toolDefinition -> toolDefinition.getId())
            .collect(Collectors.toList());
    }

}
